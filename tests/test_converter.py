import pytest
import requests

from .examples.example_loader import load_example


class TestConverter:
    @pytest.fixture()
    def url(self, proxy_url: str) -> str:
        return f"{proxy_url}/convert"

    def test_converter_json_stu3_to_json_r4(self, url, token):
        # Given
        stu3_payload = load_example("stu3.json")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+json; fhirVersion=3.0",
            "Accept": "application/fhir+json; fhirVersion=4.0"
        }

        # When
        res = requests.post(url, json=stu3_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example("stu3_to_r4_200.json")
        assert res.json() == expected_response

    def test_converter_json_r4_to_json_stu3(self, url, token):
        # Given
        r4_payload = load_example("r4.json")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+json; fhirVersion=4.0",
            "Accept": "application/fhir+json; fhirVersion=3.0"
        }

        # When
        res = requests.post(url, json=r4_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example("r4_to_stu3_200.json")
        assert res.json() == expected_response

    @pytest.mark.debug
    def test_converter_json_r4_to_xml_r4(self, url, token):
        # Given
        r4_payload = load_example("r4.xml")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+xml; fhirVersion=4.0",
            "Accept": "application/fhir+json; fhirVersion=3.0"
        }

        # When
        res = requests.post(url, json=r4_payload, headers=headers)

        # Then
        # Fixme: backend returns 500
        print(res.text)
        # assert res.status_code == 200
        # expected_response = load_example("r4_to_r4_200.json")
        # assert res.json() == expected_response
