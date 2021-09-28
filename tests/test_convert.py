import pytest
import requests
from .configuration import config
from assertpy import assert_that


class TestConvert:

    @pytest.mark.parametrize(
        "headers",
        [
            # Scenario 1: invalid Content-Type header
            (
                {
                "Content-Type": "invalid",
                "Accept": "application/fhir+json; fhirVersion=3.0"  
                }
            ),
            # Scenario 2: empty Content-Type header
            (
                {
                "Content-Type": "",
                "Accept": "application/fhir+json; fhirVersion=3.0" 
                }
            ),
             # Scenario 3: missing Content-Type header
            (
                {
                "Accept": "application/fhir+json; fhirVersion=3.0" 
                }
            ),
        ],
    )
    def test_convert_invalid_content_type(self, get_token_client_credentials, headers):
        # Given
        token = get_token_client_credentials["access_token"]
        expected_status_code = 400
        
        headers.update({"Authorization": f"Bearer {token}"}) 

        # When
        response = requests.post(
            url=f"{config.BASE_URL}/{config.BASE_PATH}/convert",
            headers=headers
        )

        # Then
        assert_that(expected_status_code).is_equal_to(response.status_code)


    @pytest.mark.parametrize(
        "headers",
        [
            # Scenario 1: invalid Accept header
            (
                {
                "Content-Type": "application/fhir+json; fhirVersion=3.0",
                "Accept": "invalid"  
                }
            ),
            # Scenario 2: empty Accept header
            (
                {
                "Content-Type": "application/fhir+json; fhirVersion=3.0",
                "Accept": "a" 
                }
            ),
             # Scenario 3: missing Accept header
            (
                {
                "Content-Type": "application/fhir+json; fhirVersion=3.0"
                }
            ),
        ],
    )
    def test_convert_invalid_accept(self, get_token_client_credentials, headers):
        # Given
        token = get_token_client_credentials["access_token"]
        expected_status_code = 400

        headers.update({"Authorization": f"Bearer {token}"}) 

        # When
        response = requests.post(
            url=f"{config.BASE_URL}/{config.BASE_PATH}/convert",
            headers=headers
        )

        # Then
        assert_that(expected_status_code).is_equal_to(response.status_code)
