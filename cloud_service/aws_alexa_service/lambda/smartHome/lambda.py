<<<<<<< HEAD
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
import json

# Imports for v3 validation
from validation import validate_message

# Import code for v3 api
import v3_code

# Setup logger
logger = logging.getLogger()
logger.setLevel(logging.INFO)


def lambda_handler(request, context):

    customer_id=""

    try:    
        formatted_log("Request",request)
        version = get_directive_version(request)

        if version == "3":
            if request["directive"]["header"]["name"] == "Discover":
                response = v3_code.handle_discovery_v3(request,customer_id)
            else:
                response = v3_code.handle_non_discovery_v3(request,customer_id)

            formatted_log("Validate v3 response",response)
            validate_message(request, response)

        else:
            logger.error("Received unsupported directive!")

        return response

    except ValueError as error:
        logger.error("ERROR IN DIRECTIVE")
        logger.error(request)
        logger.error(error)
        raise

def formatted_log(header,logentry):
    logger.info(header)
    logger.info(json.dumps(logentry, indent=4, sort_keys=True))

def get_directive_version(request):
    try:
        return request["directive"]["header"]["payloadVersion"]
    except:
        try:
            return request["header"]["payloadVersion"]
        except:
=======
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
import json

# Imports for v3 validation
from validation import validate_message

# Import code for v3 api
import v3_code

# Setup logger
logger = logging.getLogger()
logger.setLevel(logging.INFO)


def lambda_handler(request, context):

    customer_id=""

    try:    
        formatted_log("Request",request)
        version = get_directive_version(request)

        if version == "3":
            if request["directive"]["header"]["name"] == "Discover":
                response = v3_code.handle_discovery_v3(request,customer_id)
            else:
                response = v3_code.handle_non_discovery_v3(request,customer_id)

            formatted_log("Validate v3 response",response)
            validate_message(request, response)

        else:
            logger.error("Received unsupported directive!")

        return response

    except ValueError as error:
        logger.error("ERROR IN DIRECTIVE")
        logger.error(request)
        logger.error(error)
        raise

def formatted_log(header,logentry):
    logger.info(header)
    logger.info(json.dumps(logentry, indent=4, sort_keys=True))

def get_directive_version(request):
    try:
        return request["directive"]["header"]["payloadVersion"]
    except:
        try:
            return request["header"]["payloadVersion"]
        except:
>>>>>>> 7ba82746a1578756c23a8c48ddbf6fa93d554228
            return "-1"