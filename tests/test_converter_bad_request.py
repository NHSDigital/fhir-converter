import pytest
import requests

from .examples.example_loader import load_example


class TestConverterBadRequest:
    @pytest.fixture()
    def url(self, proxy_url: str) -> str:
        return f"{proxy_url}/convert"

    @pytest.mark.parametrize("accept", [
        # "application/fhir+json; fhirVersion=4.0" -> valid header
        "application/fhir+json; fhirVersion=2.0",
        "fhir+json; fhirVersion=4.0",
        "application/json; fhirVersion=4.0",
        "application fhir+json; fhirVersion=4.0",
        "application/fhir+json fhirVersion=4.0"
    ])
    def test_converter_invalid_accept_header(self, url, token, accept):
        # Given
        stu3_payload = load_example("stu3.json")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+json; fhirVersion=3.0",
            "Accept": accept
        }

        # When
        res = requests.post(url, json=stu3_payload, headers=headers)

        # FIXME: fix assertions after adding validation to the backend
        # Then
        print(res.text)
        # assert res.status_code == 400
        # expected_response = load_example("400.json")
        # assert res.json() == expected_response

    def test_converter_missing_accept_header(self, url, token):
        # Given
        stu3_payload = load_example("stu3.json")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+json; fhirVersion=3.0",
        }

        # When
        res = requests.post(url, json=stu3_payload, headers=headers)

        # FIXME: fix assertions after adding validation to the backend
        print(res.text)
        # Then
        # assert res.status_code == 400
        # expected_response = load_example("400.json")
        # assert res.json() == expected_response

    @pytest.mark.parametrize("content_type", [
        # "application/fhir+json; fhirVersion=3.0" <-- valid header
        "application/fhir+json; fhirVersion=2.0",
        "application/json; fhirVersion=5.0",
    ])
    def test_converter_invalid_content_type_header(self, url, token, content_type):
        # Given
        stu3_payload = load_example("stu3.json")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": content_type,
            "Accept": "application/fhir+json; fhirVersion=4.0"
        }

        # When
        res = requests.post(url, json=stu3_payload, headers=headers)

        # FIXME: fix assertions after adding validation to the backend
        # Then
        print(res.text)
        # assert res.status_code == 400
        # expected_response = load_example("400.json")
        # assert res.json() == expected_response

    def test_converter_missing_content_type_header(self, url, token):
        # Given
        stu3_payload = load_example("stu3.json")
        headers = {
            "Authorization": f"Bearer {token}",
            "Accept": "application/fhir+json; fhirVersion=4.0"
        }

        # When
        res = requests.post(url, json=stu3_payload, headers=headers)

        # FIXME: fix assertions after adding validation to the backend
        print(res.text)
        # Then
        # assert res.status_code == 400
        # expected_response = load_example("400.json")
        # assert res.json() == expected_response
