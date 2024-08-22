#!/bin/bash

apt-get -y install jq

export AWS_REGION=us-east-1
ACCOUNT_ID=$(awslocal sts get-caller-identity | jq -r '.Account')

# IAM?
echo "CREATE IAM ROLE"
ROLE_ARN=$(awslocal iam create-role --role-name poly-build-api-default-role \
    --assume-role-policy-document file:///etc/localstack/init/ready.d/assume-role-policy.json | jq -r '.Role.Arn')

awslocal iam attach-role-policy --role-name poly-build-api-default-role \
    --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole


echo "CREATE DYNAMO DB TABLE"
awslocal dynamodb create-table \
    --table-name BuildingSites \
    --attribute-definitions AttributeName=id,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --region us-east-1


echo "CREATE LAMBDA FUNCTION"

LAMBDA_ARN=$(awslocal lambda create-function \
  --function-name poly-build-api \
  --runtime java17 \
  --handler com.weronka.golonka.LocalPolyBuildApi \
  --memory-size 512 \
  --zip-file fileb:///etc/localstack/init/ready.d/target/api.zip \
  --region us-east-1 \
  --role $ROLE_ARN \
  --environment file:///etc/localstack/init/ready.d/env.json | jq -r '.FunctionArn')

awslocal lambda add-permission \
    --function-name poly-build-api \
    --statement-id id12345 \
    --action lambda:InvokeFunction \
    --principal apigateway.amazonaws.com

awslocal lambda add-permission \
    --function-name poly-build-api \
    --principal logs.amazonaws.com \
    --statement-id log-permission \
    --action lambda:InvokeFunction \
    --source-arn arn:aws:logs:$AWS_REGION:$ACCOUNT_ID:log-group:/aws/lambda/poly-build-api


echo "CREATE API GATEWAY"
REST_API_ID=$(awslocal apigateway create-rest-api --name poly-build-api-gateway | jq -r '.id')
ROOT_ID=$(awslocal apigateway get-resources --rest-api-id "$REST_API_ID" --query "items[?path=='/'].id" --output text)

# POST ENDPOINT
echo "EXPOSE SPLIT ENDPOINT"
SPLIT_ENDPOINT_RESOURCE_ID=$(awslocal apigateway create-resource \
    --rest-api-id $REST_API_ID \
    --parent-id $ROOT_ID \
    --path-part "split" | jq -r '.id')

awslocal apigateway put-method \
    --rest-api-id $REST_API_ID \
    --resource-id $SPLIT_ENDPOINT_RESOURCE_ID \
    --http-method POST \
    --authorization-type "NONE"

awslocal apigateway put-integration --rest-api-id $REST_API_ID \
    --resource-id $SPLIT_ENDPOINT_RESOURCE_ID \
    --http-method POST \
    --type AWS_PROXY \
    --integration-http-method POST \
    --uri arn:aws:apigateway:$AWS_REGION:lambda:path/2015-03-31/functions/$LAMBDA_ARN/invocations

echo "EXPOSE OPENAPI ENDPOINT"

OPEN_API_ENDPOINT_RESOURCE_ID=$(awslocal apigateway create-resource \
    --rest-api-id $REST_API_ID \
    --parent-id $ROOT_ID \
    --path-part "openapi.json" | jq -r '.id')

awslocal apigateway put-method \
    --rest-api-id $REST_API_ID \
    --resource-id $OPEN_API_ENDPOINT_RESOURCE_ID \
    --http-method GET \
    --authorization-type "NONE"

# INTEGRATION METHOD NEEDS TO BE POST
awslocal apigateway put-integration --rest-api-id $REST_API_ID \
    --resource-id $OPEN_API_ENDPOINT_RESOURCE_ID \
    --http-method GET \
    --type AWS_PROXY \
    --integration-http-method POST \
    --uri arn:aws:apigateway:$AWS_REGION:lambda:path/2015-03-31/functions/$LAMBDA_ARN/invocations

STAGE="local"

awslocal lambda add-permission --function-name poly-build-api \
    --statement-id poly-build-api-permission \
    --action lambda:InvokeFunction \
    --principal apigateway.amazonaws.com \
    --source-arn "arn:aws:execute-api:$AWS_REGION:$ACCOUNT_ID:$REST_API_ID/$STAGE/ANY/*"

awslocal apigateway create-deployment --rest-api-id "$REST_API_ID" --stage-name $STAGE

echo "API is available on http://$REST_API_ID.execute-api.localhost.localstack.cloud:4566/$STAGE"
