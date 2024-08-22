import sys
import unittest

import boto3

from tests.test_cases import PolyBuildAPITestCases

def main():
    ENDPOINT_URL = "http://local-aws:4566"
    client = boto3.client('apigateway', endpoint_url=ENDPOINT_URL)

    apis=client.get_rest_apis()

    API_STAGE="local"
    REST_API_ID=apis['items'][0]['id']
    URL=f"{ENDPOINT_URL}/restapis/{REST_API_ID}/{API_STAGE}/_user_request_/split"

    suite=unittest.TestSuite([
        PolyBuildAPITestCases(URL, "test_valid_request"),
        PolyBuildAPITestCases(URL, "test_inaccurate_height_plateaus"),
        PolyBuildAPITestCases(URL, "test_invalid_polygon"),
        PolyBuildAPITestCases(URL, "test_invalid_body")
    ])
    runner=unittest.TextTestRunner()
    result = runner.run(suite)

    if not result.wasSuccessful():
        sys.exit(1)

if __name__ == "__main__":
    main()