import json
import boto3
import logging
import datetime

dynamodb = boto3.resource('dynamodb')


def get_data(customer_id,device_id):
    
    table = dynamodb.Table('testcustomsmartdevice1')
    try:
        response = table.get_item(
            Key={
                'customer_id': customer_id
            }
        )
    except ClientError as e:
        print(e.response['Error']['Message'])
    else:
        print("GetItem succeeded:")

    return response

def put_data(customer_id,device_id,device_on):

    table = dynamodb.Table('testcustomsmartdevice1')
    response = table.update_item(
            Key={
                    'customer_id': customer_id
                },
            UpdateExpression="set  update_time=:r, customer_description=:p, devices=:a",
            ExpressionAttributeValues={
                ':r': "test",
                ':p': "StringDESCRIPTION",
                ':a': ["device1", "device2", "device3"]
            },
            ReturnValues="UPDATED_NEW"
            )




    return response