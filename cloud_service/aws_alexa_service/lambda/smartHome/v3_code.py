<<<<<<< HEAD
import uuid
import async_code
import logging
import time
import json
import requests

from  sample_appliances import *
from appliance_code import *

# Setup logger
logger = logging.getLogger()
logger.setLevel(logging.INFO)


def get_utc_timestamp(seconds=None):
    return time.strftime("%Y-%m-%dT%H:%M:%S.00Z", time.gmtime(seconds))

def get_uuid():
    return str(uuid.uuid4())

def handle_discovery_v3(request):
    endpoints = []

    customer_data = get_data(customer_id)
    devices = customer_data["devices"]
    for device in devices:
        endpoints.append(device)

    response = {
        "event": {
            "header": {
                "namespace": "Alexa.Discovery",
                "name": "Discover.Response",
                "payloadVersion": "3",
                "messageId": get_uuid()
            },
            "payload": {
                "endpoints": endpoints
            }
        }
    }
    return response

def handle_non_discovery_v3(request,customer_id):
    request_namespace = request["directive"]["header"]["namespace"]
    request_name = request["directive"]["header"]["name"]

    if request_namespace == "Alexa.PowerController":

        device_value = "OFF"
        if request_name == "TurnOn":
            device_value = "ON"


        customer_id = "NONDESCv3"
        device_id = request["directive"]["endpoint"]["endpointId"]

        put_data(customer_id,device_id,device_value=="ON")

        response = {
            "context": {
                "properties": [
                    {
                        "namespace": "Alexa.PowerController",
                        "name": "powerState",
                        "value": device_value,
                        "timeOfSample": get_utc_timestamp(),
                        "uncertaintyInMilliseconds": 500
                    }
                ]
            },
            "event": {
                "header": {
                    "namespace": "Alexa",
                    "name": "Response",
                    "payloadVersion": "3",
                    "messageId": get_uuid(),
                    "correlationToken": request["directive"]["header"]["correlationToken"]
                },
                "endpoint": {
                    "scope": {
                        "type": "BearerToken",
                        "token": "access-token-from-Amazon"
                    },
                    "endpointId": request["directive"]["endpoint"]["endpointId"]
                },
                "payload": {}
            }
        }
        return response

    elif request_namespace == "Alexa.Authorization":
        if request_name == "AcceptGrant":
            response = {
                "event": {
                    "header": {
                        "namespace": "Alexa.Authorization",
                        "name": "AcceptGrant.Response",
                        "payloadVersion": "3",
                        "messageId": get_uuid()
                    },
                    "payload": {

                    }
                }
            }
            
            logger.info("async_code.get_access_token before")
            request_code = request["directive"]["payload"]["grant"]["code"]
            async_code.get_access_token(request_code,"","")
            return response

    elif request_namespace == "Alexa":
        if request_name == "ReportState":

            correlationToken = request["directive"]["header"]["correlationToken"]
            scope = endpointId = request["directive"]["endpoint"]["scope"]["token"]
            endpointId = request["directive"]["endpoint"]["endpointId"]

            logger.info("REPORTSTATE directive recieved!")
            response = get_report_state_response(endpointId,scope,correlationToken)
            logger.info("REPORTSTATE response:")
            logger.info(response)
            
            return response
    else:
        logger.info("INVALID STATE REQUEST RECEIVED!")
        logger.info(request)
        #todo: return error

def get_report_state_response(endpointId,scope,correlationToken):
    response = ""

   
    if endpointId == "endpoint-008":
        response = {
                    "context": {
                        "properties": [
                            {
                                "namespace": "Alexa.EndpointHealth",
                                "name": "connectivity",
                                "value": {
                                    "value": "OK"
                                },
                                "timeOfSample": get_utc_timestamp(),
                                "uncertaintyInMilliseconds": 200
                            },
                            {
                                "namespace": "Alexa.PowerController",
                                "name": "powerState",
                                "value": "ON",
                                "timeOfSample": "2017-02-03T16:20:50.52Z",
                                "uncertaintyInMilliseconds": 0
                            }
                        ]
                    },
                    "event": {
                        "header": {
                            "namespace": "Alexa",
                            "name": "StateReport",
                            "payloadVersion": "3",
                            "messageId": get_uuid(),
                            "correlationToken": correlationToken
                        },
                        "endpoint": {
                            "scope": {
                                "type": "BearerToken",
                                "token": scope
                            },
                            "endpointId": endpointId
                        },
                        "payload": {}
                    }
                }

=======
import uuid
import async_code
import logging
import time
import json
import requests

from  sample_appliances import *
from appliance_code import *

# Setup logger
logger = logging.getLogger()
logger.setLevel(logging.INFO)


def get_utc_timestamp(seconds=None):
    return time.strftime("%Y-%m-%dT%H:%M:%S.00Z", time.gmtime(seconds))

def get_uuid():
    return str(uuid.uuid4())

def handle_discovery_v3(request):
    endpoints = []

    customer_data = get_data(customer_id)
    devices = customer_data["devices"]
    for device in devices:
        endpoints.append(device)

    response = {
        "event": {
            "header": {
                "namespace": "Alexa.Discovery",
                "name": "Discover.Response",
                "payloadVersion": "3",
                "messageId": get_uuid()
            },
            "payload": {
                "endpoints": endpoints
            }
        }
    }
    return response

def handle_non_discovery_v3(request,customer_id):
    request_namespace = request["directive"]["header"]["namespace"]
    request_name = request["directive"]["header"]["name"]

    if request_namespace == "Alexa.PowerController":

        device_value = "OFF"
        if request_name == "TurnOn":
            device_value = "ON"


        customer_id = "NONDESCv3"
        device_id = request["directive"]["endpoint"]["endpointId"]

        put_data(customer_id,device_id,device_value=="ON")

        response = {
            "context": {
                "properties": [
                    {
                        "namespace": "Alexa.PowerController",
                        "name": "powerState",
                        "value": device_value,
                        "timeOfSample": get_utc_timestamp(),
                        "uncertaintyInMilliseconds": 500
                    }
                ]
            },
            "event": {
                "header": {
                    "namespace": "Alexa",
                    "name": "Response",
                    "payloadVersion": "3",
                    "messageId": get_uuid(),
                    "correlationToken": request["directive"]["header"]["correlationToken"]
                },
                "endpoint": {
                    "scope": {
                        "type": "BearerToken",
                        "token": "access-token-from-Amazon"
                    },
                    "endpointId": request["directive"]["endpoint"]["endpointId"]
                },
                "payload": {}
            }
        }
        return response

    elif request_namespace == "Alexa.Authorization":
        if request_name == "AcceptGrant":
            response = {
                "event": {
                    "header": {
                        "namespace": "Alexa.Authorization",
                        "name": "AcceptGrant.Response",
                        "payloadVersion": "3",
                        "messageId": get_uuid()
                    },
                    "payload": {

                    }
                }
            }
            
            logger.info("async_code.get_access_token before")
            request_code = request["directive"]["payload"]["grant"]["code"]
            async_code.get_access_token(request_code,"","")
            return response

    elif request_namespace == "Alexa":
        if request_name == "ReportState":

            correlationToken = request["directive"]["header"]["correlationToken"]
            scope = endpointId = request["directive"]["endpoint"]["scope"]["token"]
            endpointId = request["directive"]["endpoint"]["endpointId"]

            logger.info("REPORTSTATE directive recieved!")
            response = get_report_state_response(endpointId,scope,correlationToken)
            logger.info("REPORTSTATE response:")
            logger.info(response)
            
            return response
    else:
        logger.info("INVALID STATE REQUEST RECEIVED!")
        logger.info(request)
        #todo: return error

def get_report_state_response(endpointId,scope,correlationToken):
    response = ""

   
    if endpointId == "endpoint-008":
        response = {
                    "context": {
                        "properties": [
                            {
                                "namespace": "Alexa.EndpointHealth",
                                "name": "connectivity",
                                "value": {
                                    "value": "OK"
                                },
                                "timeOfSample": get_utc_timestamp(),
                                "uncertaintyInMilliseconds": 200
                            },
                            {
                                "namespace": "Alexa.PowerController",
                                "name": "powerState",
                                "value": "ON",
                                "timeOfSample": "2017-02-03T16:20:50.52Z",
                                "uncertaintyInMilliseconds": 0
                            }
                        ]
                    },
                    "event": {
                        "header": {
                            "namespace": "Alexa",
                            "name": "StateReport",
                            "payloadVersion": "3",
                            "messageId": get_uuid(),
                            "correlationToken": correlationToken
                        },
                        "endpoint": {
                            "scope": {
                                "type": "BearerToken",
                                "token": scope
                            },
                            "endpointId": endpointId
                        },
                        "payload": {}
                    }
                }

>>>>>>> 7ba82746a1578756c23a8c48ddbf6fa93d554228
    return response