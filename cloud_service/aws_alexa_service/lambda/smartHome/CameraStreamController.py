<<<<<<< HEAD
"""
{
  "event": {
    "header": {
      "namespace":"Alexa.Discovery",
      "name":"Discover.Response",
      "payloadVersion":"3",
      "messageId":"ff746d98-ab02-4c9e-9d0d-b44711658414"
    },
    "payload":{
      "endpoints":[
        {
          "endpointId": "uniqueIdOfCameraEndpoint",
          "endpointTypeId": "type id",
          "manufacturerName": "the manufacturer name of the endpoint",
          "modelName": "the model name of the endpoint",
          "friendlyName": "Camera",
          "description": "a description that is shown to the customer",
          "displayCategories": [ "CAMERA" ],
          "cookie": {
              "key1": "arbitrary key/value pairs for skill to reference this endpoint.",
              "key2": "There can be multiple entries",
              "key3": "but they should only be used for reference purposes.",
              "key4": "This is not a suitable place to maintain current endpoint state."
          },
          "capabilities":
          [
            {
              "type": "AlexaInterface",
              "interface": "Alexa.CameraStreamController",
              "version": "3",
              "cameraStreamConfigurations" : [
                  {
                    "protocols": ["RTSP"], 
                    "resolutions": [{"width":1920, "height":1080}, {"width":1280, "height":720}], 
                    "authorizationTypes": ["BASIC"], 
                    "videoCodecs": ["H264", "MPEG2"], 
                    "audioCodecs": ["G711"] 
                  },
                  {
                    "protocols": ["RTSP"], 
                    "resolutions": [{"width":1920, "height":1080}, {"width":1280, "height":720}], 
                    "authorizationTypes": ["NONE"], 
                    "videoCodecs": ["H264"], 
                    "audioCodecs": ["AAC"] 
                 }
              ]
            },
            {
              "type": "AlexaInterface",
              "interface": "Alexa.PowerController",
              "version": "3"
            }
          ]
        }
      ]
    }
  }
}

{
  "directive": {
    "header": {
      "namespace": "Alexa.CameraStreamController",
      "name": "InitializeCameraStreams",
      "payloadVersion": "3",
      "messageId": "1bd5d003-31b9-476f-ad03-71d471922820",
      "correlationToken": "dFMb0z+PgpgdDmluhJ1LddFvSqZ/jCc8ptlAKulUj90jSqg=="
    },
    "endpoint": {
      "endpointId": "appliance-001",
       "scope": {
                "type": "BearerToken",
                "token": "access-token-from-skill"
            },
      "cookie": {}
    },
    "payload": {
      "cameraStreams": [{
        "protocol": "RTSP",
        "resolution": {
          "width": 1920,
          "height": 1080
        },
        "authorizationType": "BASIC",
        "videoCodec": "H264",
        "audioCodec": "AAC"
      }, {
        "protocol": "RTSP",
        "resolution": {
          "width": 1280,
          "height": 720
        },
        "authorizationType": "NONE",
        "videoCodec": "MPEG2",
        "audioCodec": "G711"
      }]
    }
  }
}


{
  "event": {
    "header": {
      "namespace": "Alexa.CameraStreamController",
      "name": "Response",
      "payloadVersion": "3",
      "messageId": "5f8a426e-01e4-4cc9-8b79-65f8bd0fd8a4",
      "correlationToken": "dFMb0z+PgpgdDmluhJ1LddFvSqZ/jCc8ptlAKulUj90jSqg=="
    },
    "endpoint": {
       "endpointId": "appliance-001"
    },
    "payload": {
      "cameraStreams": [ {
        "uri": "rtsp://username:password@link.to.video:443/feed1.mp4",
        "expirationTime": "2017-02-03T16:20:50.52Z",
        "idleTimeoutSeconds": 30,
        "protocol": "RTSP",
        "resolution": {
          "width": 1920,
          "height": 1080
        },
        "authorizationType": "BASIC",
        "videoCodec": "H264",
        "audioCodec": "AAC"
      }
     ],
      "imageUri": "https://username:password@link.to.image/image.jpg"
    }
  }
}
=======
"""
{
  "event": {
    "header": {
      "namespace":"Alexa.Discovery",
      "name":"Discover.Response",
      "payloadVersion":"3",
      "messageId":"ff746d98-ab02-4c9e-9d0d-b44711658414"
    },
    "payload":{
      "endpoints":[
        {
          "endpointId": "uniqueIdOfCameraEndpoint",
          "endpointTypeId": "type id",
          "manufacturerName": "the manufacturer name of the endpoint",
          "modelName": "the model name of the endpoint",
          "friendlyName": "Camera",
          "description": "a description that is shown to the customer",
          "displayCategories": [ "CAMERA" ],
          "cookie": {
              "key1": "arbitrary key/value pairs for skill to reference this endpoint.",
              "key2": "There can be multiple entries",
              "key3": "but they should only be used for reference purposes.",
              "key4": "This is not a suitable place to maintain current endpoint state."
          },
          "capabilities":
          [
            {
              "type": "AlexaInterface",
              "interface": "Alexa.CameraStreamController",
              "version": "3",
              "cameraStreamConfigurations" : [
                  {
                    "protocols": ["RTSP"], 
                    "resolutions": [{"width":1920, "height":1080}, {"width":1280, "height":720}], 
                    "authorizationTypes": ["BASIC"], 
                    "videoCodecs": ["H264", "MPEG2"], 
                    "audioCodecs": ["G711"] 
                  },
                  {
                    "protocols": ["RTSP"], 
                    "resolutions": [{"width":1920, "height":1080}, {"width":1280, "height":720}], 
                    "authorizationTypes": ["NONE"], 
                    "videoCodecs": ["H264"], 
                    "audioCodecs": ["AAC"] 
                 }
              ]
            },
            {
              "type": "AlexaInterface",
              "interface": "Alexa.PowerController",
              "version": "3"
            }
          ]
        }
      ]
    }
  }
}

{
  "directive": {
    "header": {
      "namespace": "Alexa.CameraStreamController",
      "name": "InitializeCameraStreams",
      "payloadVersion": "3",
      "messageId": "1bd5d003-31b9-476f-ad03-71d471922820",
      "correlationToken": "dFMb0z+PgpgdDmluhJ1LddFvSqZ/jCc8ptlAKulUj90jSqg=="
    },
    "endpoint": {
      "endpointId": "appliance-001",
       "scope": {
                "type": "BearerToken",
                "token": "access-token-from-skill"
            },
      "cookie": {}
    },
    "payload": {
      "cameraStreams": [{
        "protocol": "RTSP",
        "resolution": {
          "width": 1920,
          "height": 1080
        },
        "authorizationType": "BASIC",
        "videoCodec": "H264",
        "audioCodec": "AAC"
      }, {
        "protocol": "RTSP",
        "resolution": {
          "width": 1280,
          "height": 720
        },
        "authorizationType": "NONE",
        "videoCodec": "MPEG2",
        "audioCodec": "G711"
      }]
    }
  }
}


{
  "event": {
    "header": {
      "namespace": "Alexa.CameraStreamController",
      "name": "Response",
      "payloadVersion": "3",
      "messageId": "5f8a426e-01e4-4cc9-8b79-65f8bd0fd8a4",
      "correlationToken": "dFMb0z+PgpgdDmluhJ1LddFvSqZ/jCc8ptlAKulUj90jSqg=="
    },
    "endpoint": {
       "endpointId": "appliance-001"
    },
    "payload": {
      "cameraStreams": [ {
        "uri": "rtsp://username:password@link.to.video:443/feed1.mp4",
        "expirationTime": "2017-02-03T16:20:50.52Z",
        "idleTimeoutSeconds": 30,
        "protocol": "RTSP",
        "resolution": {
          "width": 1920,
          "height": 1080
        },
        "authorizationType": "BASIC",
        "videoCodec": "H264",
        "audioCodec": "AAC"
      }
     ],
      "imageUri": "https://username:password@link.to.image/image.jpg"
    }
  }
}
>>>>>>> 7ba82746a1578756c23a8c48ddbf6fa93d554228
"""