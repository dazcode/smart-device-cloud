# -*- coding: utf-8 -*-

# Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# Licensed under the Amazon Software License (the "License"). You may not use this file except in
# compliance with the License. A copy of the License is located at
#
#    http://aws.amazon.com/asl/
#
# or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or implied. See the License for the specific
# language governing permissions and limitations under the License.


import logging
import sys
import time
import datetime
import json
import uuid
import os
import requests
import boto3



s3 = boto3.resource('s3')

# constants
UTC_FORMAT = "%Y-%m-%dT%H:%M:%S.00Z"
LWA_TOKEN_URI = "https://api.amazon.com/auth/o2/token"
LWA_HEADERS = {
    "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
}
ALEXA_URI = "https://api.amazonalexa.com/v3/events"
ALEXA_HEADERS = {
    "Content-Type": "application/json;charset=UTF-8"
}

# setup logger
LOGGER = logging.getLogger()



with open('config.json', 'r') as f:
    config = json.load(f)


# LWA constants
CLIENT_ID = config['DEFAULT']['CLIENT_ID']
CLIENT_SECRET = config['DEFAULT']['CLIENT_SECRET']
PREEMPTIVE_REFRESH_TTL_IN_SECONDS = 300
#S3
S3_BUCKET_CREDENTIAL_STORAGE = config['DEFAULT']['S3_BUCKET_IMAGE_STORAGE']





def get_token_filename(code_param):
    return code_param

def write_boto3(filename,text):
    
    object = s3.Object(S3_BUCKET_CREDENTIAL_STORAGE, filename)
    object.put(Body=text)

def read_boto3(filename):

    obj = s3.Object(S3_BUCKET_CREDENTIAL_STORAGE, filename)
    thedata = obj.get()['Body'].read().decode('utf-8')

    return thedata

def boto_file_exists(filename):
    
    try:
        s3.Object(S3_BUCKET_CREDENTIAL_STORAGE,filename).load()
    except:
        return False

    return True

def get_utc_timestamp(seconds=None):
    return time.strftime(UTC_FORMAT, time.gmtime(seconds))

def get_utc_timestamp_from_string(string):
    return datetime.datetime.strptime(string, UTC_FORMAT)

def get_uuid():
    return str(uuid.uuid4())

# authentication functions
def get_need_new_token(code_param,access_param,refresh_param):
    """Checks whether the access token is missing or needed to be refreshed"""
    need_new_token_response = {
        "need_new_token": True,
        "access_token": access_param,
        "refresh_token": refresh_param
    }

    filename = get_token_filename(code_param)

    if boto_file_exists(filename):
        # if token file exists, then we've already gotten the first access token for this user skill enablement
        filedata = read_boto3(filename)
        last_line = filedata.splitlines()[-1]

        token = last_line.split("***")
        token_received_datetime = get_utc_timestamp_from_string(token[0])
        token_json = json.loads(token[1])
        token_expires_in = token_json["expires_in"] - PREEMPTIVE_REFRESH_TTL_IN_SECONDS
        token_expires_datetime = token_received_datetime + datetime.timedelta(seconds=token_expires_in)
        current_datetime = datetime.datetime.utcnow()

        need_new_token_response["need_new_token"] = current_datetime > token_expires_datetime
        need_new_token_response["access_token"] = token_json["access_token"]
        need_new_token_response["refresh_token"] = token_json["refresh_token"]
    else:
        # else, we've never gotten an access token for this user skill enablement
        need_new_token_response["need_new_token"] = True

    return need_new_token_response

def get_access_token(code_param,access_param,refresh_param):
    """Performs access token or token refresh request as needed and returns valid access token"""

    need_new_token_response = get_need_new_token(code_param,access_param,refresh_param)
    access_token = ""
    CODE = code_param
    do_refresh = False

    if need_new_token_response["need_new_token"]:
        if do_refresh:
            lwa_params = {
                "grant_type" : "refresh_token",
                "refresh_token": need_new_token_response["refresh_token"],
                "client_id": CLIENT_ID,
                "client_secret": CLIENT_SECRET
            }
            LOGGER.info("Calling LWA to refresh the access token...")
        else:
            # access token not retrieved yet for the first time, so this should be an access token request
            lwa_params = {
                "grant_type" : "authorization_code",
                "code": CODE,
                "client_id": CLIENT_ID,
                "client_secret": CLIENT_SECRET
            }
            LOGGER.info("Calling LWA to get the access token for the first time...")
            LOGGER.info("Params: " + json.dumps(lwa_params))

        response = requests.post(LWA_TOKEN_URI, headers=LWA_HEADERS, data=lwa_params, allow_redirects=True)
        LOGGER.info("LWA response header: " + format(response.headers))
        LOGGER.info("LWA response status: " + format(response.status_code))
        LOGGER.info("LWA response body  : " + format(response.text))

        if response.status_code != 200:
            LOGGER.info("Error calling LWA!")
            return None

        filename = CODE
        token = get_utc_timestamp() + "***" + response.text
        write_boto3(filename,token)
        

        access_token = json.loads(response.text)["access_token"]
        LOGGER.info("GOT ACCESS: " + access_token)
    else:
        LOGGER.info("Latest access token has not expired, so using it and won't call LWA...")
        access_token = need_new_token_response["access_token"]
        LOGGER.info("REUSING ACCESS: " + access_token)

    return access_token

def send_async():
    """Main function that sends a proactive state or change report to Alexa"""

    token = get_access_token()

    if token:
        message_id = get_uuid()
        time_of_sample = get_utc_timestamp()

        
        alexa_params = {
            "context": {
                "properties": [{
                    "namespace": "Alexa.EndpointHealth",
                    "name": "connectivity",
                    "value": {
                        "value": "OK"
                    },
                    "timeOfSample": time_of_sample,
                    "uncertaintyInMilliseconds": 500
                }, {
                    "namespace": "Alexa.BrightnessController",
                    "name": "brightness",
                    "value": 99,
                    "timeOfSample": time_of_sample,
                    "uncertaintyInMilliseconds": 500
                }]
            },
            "event": {
                "header": {
                    "namespace": "Alexa",
                    "name": "ChangeReport",
                    "payloadVersion": "3",
                    "messageId": message_id
                },
                "endpoint": {
                    "scope": {
                        "type": "BearerToken",
                        "token": token
                    },
                    "endpointId": "endpoint-002"
                },
                "payload": {
                    "change": {
                        "cause": {
                            "type": "PHYSICAL_INTERACTION"
                        },
                        "properties": [{
                            "namespace": "Alexa.PowerController",
                            "name": "powerState",
                            "value": "ON",
                            "timeOfSample": time_of_sample,
                            "uncertaintyInMilliseconds": 500
                        }]
                    }
                }
            }
        }

        response = requests.post(ALEXA_URI, headers=ALEXA_HEADERS, data=json.dumps(alexa_params), allow_redirects=True)
        LOGGER.info("Request data: " + json.dumps(alexa_params))
        LOGGER.info("Alexa response header: " + format(response.headers))
        LOGGER.info("Alexa response status: " + format(response.status_code))
        LOGGER.info("Alexa response body  : " + format(response.text))

