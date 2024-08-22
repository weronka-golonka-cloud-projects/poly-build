import requests
import unittest

from .test_requests import inaccurate_height_plateaus_body, valid_body, invalid_polygon_body, invalid_body


class PolyBuildAPITestCases(unittest.TestCase):
    def __init__(self, api_url, methodName='runTest'):
        super().__init__(methodName)
        self.api_url = api_url

    def test_valid_request(self):
        response=requests.post(url=self.api_url, json=valid_body)
        data=response.json()

        assert response.status_code == 201
        assert isinstance(data['id'], str)
        assert isinstance(data['building_limits'], dict)
        assert isinstance(data['height_plateaus'], dict)
        assert isinstance(data['split_building_limits'], dict)

    def test_inaccurate_height_plateaus(self):
        response=requests.post(url=self.api_url, json=inaccurate_height_plateaus_body)

        assert response.status_code == 400

    def test_invalid_polygon(self):
        response=requests.post(url=self.api_url, json=invalid_polygon_body)

        assert response.status_code == 400

    def test_invalid_body(self):
        response=requests.post(url=self.api_url, json=invalid_body)

        assert response.status_code == 400