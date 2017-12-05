import json
import boto3
import logging
import datetime
import boto3
import base64

from appliances_code import *

now = datetime.datetime.now()
LOGGER = logging.getLogger()
dynamodb = boto3.resource('dynamodb')
rekognition = boto3.client('rekognition')
s3 = boto3.resource('s3')


with open('config.json', 'r') as f:
        config = json.load(f)
        
S3_BUCKET_IMAGE_STORAGE = config['DEFAULT']['S3_BUCKET_IMAGE_STORAGE']
S3_BUCKET_IMAGE_STORAGE = ""

def run_rekognition(image_name):

    print("ATTEMPTING REKOGNITION:"+ str(image_name) )  

    resp = rekognition.detect_labels(
    Image={
        'S3Object': {
            'Bucket': S3_BUCKET_IMAGE_STORAGE,
            'Name': image_name
        }
    }
    )
    
    print("FINISHED REKOGNITION:"+ str(resp))
    return resp


def respond(err, res=None):
    return {
        'statusCode': '400' if err else '200',
        'body': err.message if err else json.dumps(res),
        'headers': {
            'Content-Type': 'application/json',
        },
    }

def respond_html(err, res=None):
    return {
        'statusCode': '400' if err else '200',
        'body': err.message if err else res,
        'headers': {
            'Content-Type': 'text/html',
        },
    }    

def get_data(customer_id,device_id):
    response = ""
    table = dynamodb.Table('testcustomsmartdevice1')
    try:
        response = table.get_item(
            Key={
                'customer_id': customer_id
            }
        )
    except ClientError as e:
        print("CLIENTERROR" + e.response['Error']['Message'])
    else:
        print("GetItem succeeded:"+str(response))
        
        filename =  format_s3_image_filename(device_id)
        temp_image_data = read_boto3(filename)
        response["image_data"]= base64.b64encode(temp_image_data)



    return response



def read_boto3(filename):

    obj = s3.Object(S3_BUCKET_IMAGE_STORAGE, filename)
    thedata = obj.get()['Body'].read()

    return thedata

def boto_file_exists(filename):
    
    try:
        s3.Object(S3_BUCKET_IMAGE_STORAGE,filename).load()
    except ex:
        print("ERROR:" + str(ex))
        return False

    return True



def put_data(customer_id,device_id,device_on):

    table = dynamodb.Table('testcustomsmartdevice1')
    devices = SAMPLE_APPLIANCES
    table.put_item(
            Item={
                    'customer_id': customer_id,
                    'devices' : devices,
                    'device_id': device_id,
                    'device_on': device_on,
                    'image_date': now.strftime("%Y-%m-%d %H:%M")
                }
            )
    return True


def format_s3_image_filename(filename):
    filename = str(filename).replace("\"","")
    filename = filename + '.jpg'
    return filename


def output_image(customer_id,device_id):
    
    customer_data =  get_data(customer_id,device_id)
    image_data =  '<div><p>Current Image:</p><img src=\'data:image/jpeg;base64,'
    image_data += str(customer_data["Item"]["image_data"]).replace("\"","")
    image_data += '\' alt="smart_camera_image" /></div>'

    return image_data

def lambda_handler(event, context):



with open('config.json', 'r') as f:

    

    response_text = ""
    table = dynamodb.Table('testcustomsmartdevice1')
    querystring = event['queryStringParameters']
    customer_id = json.dumps(querystring['account_id'])
    device_id = json.dumps(querystring['device_id'])

    if "device_on" in querystring:
        device_data = json.dumps(event['body'])
        device_on = json.dumps(querystring['device_on'])
        put_data(customer_id,device_id,device_on)
        
        
        #TODO validate device_id:
        binary_image = bytearray(base64.b64decode(device_data))
        filename =  format_s3_image_filename(device_id)
        image_file_s3 = s3.Object(S3_BUCKET_IMAGE_STORAGE, filename)
        image_file_s3.put(Body=binary_image)
        test_ai_data = run_rekognition(filename)
        print(test_ai_data)
        response_text = test_ai_data
    else:
        response_text = get_data(customer_id,device_id)

    if "output_image" in querystring:
        response_text = output_image(customer_id,device_id)
        return respond_html(None,response_text)

    print(json.dumps(event))

    return respond(None, response_text)


def hello(event, context):
    return lambda_handler(event,context)
