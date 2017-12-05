SAMPLE_APPLIANCES = [
    
                    {
                    "endpointId": "endpoint-008",
                    "manufacturerName": "Dazcodeapps",
                    "friendlyName": "Custom Smart Cam v1",
                    "description": "Your very own smart camera!",
                    "modelName": "v1.0",
                    "displayCategories": [
                        "CAMERA"
                    ],
                    "cookie": {},
                    "capabilities": [
                        {
                            "type": "AlexaInterface",
                            "interface": "Alexa",
                            "version": "3"
                        },
                        {
                            "type": "AlexaInterface",
                            "interface": "Alexa.CameraStreamController",
                            "version": "3",
                            "cameraStreamConfigurations": [
                                {
                                    "protocols": [
                                        "RTSP"
                                    ],
                                    "resolutions": [
                                        {
                                            "width": 1280,
                                            "height": 720
                                        }
                                    ],
                                    "authorizationTypes": [
                                        "NONE"
                                    ],
                                    "videoCodecs": [
                                        "H264"
                                    ],
                                    "audioCodecs": [
                                        "AAC"
                                    ]
                                }
                            ]
                        },
                        {
                            "type": "AlexaInterface",
                            "interface": "Alexa.PowerController",
                            "version": "3"
                        },
                        {
                            "type": "AlexaInterface",
                            "interface": "Alexa.EndpointHealth",
                            "version": "3",
                            "properties": {
                                "supported": [
                                    {
                                        "name": "connectivity"
                                    }
                                ],
                                "proactivelyReported": True,
                                "retrievable": True
                            }
                        }
                    ]
                }]

"""
{
                    "endpointId": "endpoint-001",
                    "manufacturerName": "Sample Manufacturer",
                    "friendlyName": "CameraSwitch",
                    "description": "001 Switch that can only be turned on/off",
                    "displayCategories": [
                        "SWITCH"
                    ],
                    "cookie": {
                        "detail1": "For simplicity, this is the only appliance",
                        "detail2": "that has some values in the additionalApplianceDetails"
                    },
                    "capabilities": [
                        {
                            "type": "AlexaInterface",
                            "interface": "Alexa",
                            "version": "3"
                        },
                        {
                            "type": "AlexaInterface",
                            "interface": "Alexa.PowerController",
                            "version": "3",
                            "properties": {
                                "supported": [
                                    {
                                        "name": "powerState"
                                    }
                                ],
                                "proactivelyReported": True,
                                "retrievable": True
                            }
                        },
                        {
                            "type": "AlexaInterface",
                            "interface": "Alexa.EndpointHealth",
                            "version": "3",
                            "properties": {
                                "supported": [
                                    {
                                        "name": "connectivity"
                                    }
                                ],
                                "proactivelyReported": True,
                                "retrievable": True
                            }
                        }
                    ]
                    },
"""