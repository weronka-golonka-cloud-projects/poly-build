#!/bin/bash

ENDPOINT=$1

export AWS_ACCESS_KEY_ID=$2
export AWS_SECRET_ACCESS_KEY=$3

# Create a DynamoDB table
aws --endpoint-url=$ENDPOINT dynamodb create-table \
    --table-name BuildingSites \
    --attribute-definitions AttributeName=id,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --region us-east-1