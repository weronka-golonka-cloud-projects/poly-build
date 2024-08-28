# Poly-Build - Polygons and Height Plateaus API

A tiny API that helps to divide building areas according to provided height plateaus.
The API operates on polygons and utilizes `GeoJson` format in requests and responses.

Currently, one endpoint for calculating building splits is exposed. 
Check the OpenApi spec [here](https://ipqtel8ou1.execute-api.eu-north-1.amazonaws.com/openapi.json)

## Architecture

Poly-Build is a serverless kotlin application deployed as a Lambda function.
Its endpoints are exposed via API Gateway.
 
The service operates on entity called `BuildingSite`, that holds a unique ID, 
a `FeatureCollection` of building limits polygons, another `FeatureCollection` of height plateaus
polygons, and, lastly, a `FeatureCollection` of split building limits with corresponding height plateaus.

The `BuildingSite` objects are persisted in a DynamoDB table, where each `FeatureCollection`
is stored as the JSON string. It also features a [optimistic locking with version number](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBMapper.OptimisticLocking.html), 
which prevents data corruption if two users were to update the same item.

# CI/CD

The application is deployed automatically on each push to the main branch, 
using the `terraform` infrastructure code.

The application is verified with a set of unit and integration tests that need to pass 
before merging the change (and on push to main branch). 
In addition, there is a small set of end-to-end tests that run in a local docker environment.

# Build

To build the project and run unit/integration tests:

```shell
./gradlew build
```
To build API lambda zip (will also run build and test tasks):

```shell
./gradlew buildLambdaZip
```
To build end-to-end tests docker image:

```shell
./gradlew dockerBuild
```

### Testing

To run unit/integration tests:
```shell
./gradlew test
```

To run local end-to-end tests:

```shell
# build API lambda zip and end-to-end tests image
./gradlew buildLambdaZip dockerBuild

cd local

make up

# check if local AWS environment is ready
# should be "true"
curl -s localhost:4566/_localstack/init/ready | jq .completed

# run tests
make e2e-tests
```

When running in local environment, you can access the API using

```
http://<API-ID>.execute-api.localhost.localstack.cloud:4566/local
```

API ID can be determined with AWS CLI. Apart from that, the init script
for localstack also outputs API URL at the end of its execution,
so you can check container logs to get the URL.