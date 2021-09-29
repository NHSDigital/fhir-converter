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
                    "Accept": "application/fhir+json; fhirVersion=3.0",
                }
            ),
            # Scenario 2: empty Content-Type header
            ({"Content-Type": "", "Accept": "application/fhir+json; fhirVersion=3.0"}),
            # Scenario 3: missing Content-Type header
            ({"Accept": "application/fhir+json; fhirVersion=3.0"}),
        ],
    )
    def test_convert_invalid_content_type(self, get_token_client_credentials, headers):
        # Given
        token = get_token_client_credentials["access_token"]
        expected_status_code = 400
        expected_response = {
            "resourceType": "OperationOutcome",
            "issue": [
                {
                    "severity": "error",
                    "code": "value",
                    "details": {
                        "coding": [
                            {
                                "system": "https://fhir.nhs.uk/R4/CodeSystem/Spine-ErrorOrWarningCode",
                                "version": "1",
                                "code": "MISSING_OR_INVALID_HEADER",
                                "display": "There is a required header missing or invalid",
                            }
                        ]
                    },
                    "diagnostics": "Content-Type Header is missing or invalid",
                }
            ],
        }

        headers.update({"Authorization": f"Bearer {token}"})

        # When
        response = requests.post(
            url=f"{config.BASE_URL}/{config.BASE_PATH}/convert", headers=headers
        )

        # Then
        assert_that(expected_status_code).is_equal_to(response.status_code)
        assert_that(expected_response).is_equal_to(response.json())

    @pytest.mark.parametrize(
        "headers",
        [
            # Scenario 1: invalid Accept header
            (
                {
                    "Content-Type": "application/fhir+json; fhirVersion=3.0",
                    "Accept": "invalid",
                }
            ),
            # Scenario 2: empty Accept header
            ({"Content-Type": "application/fhir+json; fhirVersion=3.0", "Accept": "a"}),
            # Scenario 3: missing Accept header
            ({"Content-Type": "application/fhir+json; fhirVersion=3.0"}),
        ],
    )
    def test_convert_invalid_accept(self, get_token_client_credentials, headers):
        # Given
        token = get_token_client_credentials["access_token"]
        expected_status_code = 400
        expected_response = {
            "resourceType": "OperationOutcome",
            "issue": [
                {
                    "severity": "error",
                    "code": "value",
                    "details": {
                        "coding": [
                            {
                                "system": "https://fhir.nhs.uk/R4/CodeSystem/Spine-ErrorOrWarningCode",
                                "version": "1",
                                "code": "MISSING_OR_INVALID_HEADER",
                                "display": "There is a required header missing or invalid",
                            }
                        ]
                    },
                    "diagnostics": "Accept Header is missing or invalid",
                }
            ],
        }

        headers.update({"Authorization": f"Bearer {token}"})

        # When
        response = requests.post(
            url=f"{config.BASE_URL}/{config.BASE_PATH}/convert", headers=headers
        )

        # Then
        assert_that(expected_status_code).is_equal_to(response.status_code)
        assert_that(expected_response).is_equal_to(response.json())
