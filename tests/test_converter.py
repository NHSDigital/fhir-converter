import pytest
import requests

from .example_loader import load_example


class TestConverter:
    @pytest.fixture()
    def url(self, proxy_url: str) -> str:
        return f"{proxy_url}/convert"

    # #####################
    # STU3 to R4 ########
    # #####################
    def test_converter_json_stu3_to_json_r4(self, url, token):
        # Given
        stu3_payload = load_example("MedicationRequest/stu3.json")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+json; fhirVersion=3.0",
            "Accept": "application/fhir+json; fhirVersion=4.0"
        }

        # When
        res = requests.post(url, json=stu3_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example("MedicationRequest/stu3_to_r4_200.json")
        assert res.json() == expected_response

    def test_converter_json_stu3_to_xml_r4(self, url, token):
        # Given
        stu3_payload = load_example("MedicationRequest/stu3.json")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+json; fhirVersion=3.0",
            "Accept": "application/fhir+xml; fhirVersion=4.0"
        }

        # When
        res = requests.post(url, json=stu3_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example("MedicationRequest/stu3_to_r4_200.xml")
        assert res.text == expected_response

    def test_converter_xml_stu3_to_json_r4(self, url, token):
        # Given
        stu3_payload = load_example("MedicationRequest/stu3.xml")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+xml; fhirVersion=3.0",
            "Accept": "application/fhir+json; fhirVersion=4.0"
        }

        # When
        res = requests.post(url, data=stu3_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example("MedicationRequest/stu3_to_r4_200.json")
        assert res.json() == expected_response

    def test_converter_xml_stu3_to_xml_r4(self, url, token):
        # Given
        stu3_payload = load_example("MedicationRequest/stu3.xml")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+xml; fhirVersion=3.0",
            "Accept": "application/fhir+xml; fhirVersion=4.0"
        }

        # When
        res = requests.post(url, data=stu3_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example("MedicationRequest/stu3_to_r4_200.xml")
        assert res.text == expected_response

    # #####################
    # STU3 to STU3 ########
    # #####################

    def test_converter_json_stu3_to_xml_stu3(self, url, token):
        # Given
        stu3_payload = load_example("MedicationRequest/stu3.json")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+json; fhirVersion=3.0",
            "Accept": "application/fhir+xml; fhirVersion=3.0"
        }

        # When
        res = requests.post(url, json=stu3_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example("MedicationRequest/stu3.xml")
        assert res.text == expected_response

    def test_converter_xml_stu3_to_json_stu3(self, url, token):
        # Given
        stu3_payload = load_example("MedicationRequest/stu3.xml")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+xml; fhirVersion=3.0",
            "Accept": "application/fhir+json; fhirVersion=3.0"
        }

        # When
        res = requests.post(url, data=stu3_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example("MedicationRequest/stu3.json")
        assert res.json() == expected_response

    # #####################
    # R4 to STU3 ########
    # #####################

    def test_converter_json_r4_to_json_stu3(self, url, token):
        # Given
        r4_payload = load_example("MedicationRequest/r4.json")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+json; fhirVersion=4.0",
            "Accept": "application/fhir+json; fhirVersion=3.0"
        }

        # When
        res = requests.post(url, json=r4_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example("MedicationRequest/r4_to_stu3_200.json")
        assert res.json() == expected_response

    def test_converter_json_r4_to_xml_stu3(self, url, token):
        # Given
        r4_payload = load_example("MedicationRequest/r4.json")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+json; fhirVersion=4.0",
            "Accept": "application/fhir+xml; fhirVersion=3.0"
        }

        # When
        res = requests.post(url, json=r4_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example("MedicationRequest/r4_to_stu3_200.xml")
        assert res.text == expected_response

    def test_converter_xml_r4_to_json_stu3(self, url, token):
        # Given
        r4_payload = load_example("MedicationRequest/r4.xml")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+xml; fhirVersion=4.0",
            "Accept": "application/fhir+json; fhirVersion=3.0"
        }

        # When
        res = requests.post(url, data=r4_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example("MedicationRequest/r4_to_stu3_200.json")
        assert res.json() == expected_response

    def test_converter_xml_r4_to_xml_stu3(self, url, token):
        # Given
        r4_payload = load_example("MedicationRequest/r4.xml")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+xml; fhirVersion=4.0",
            "Accept": "application/fhir+xml; fhirVersion=3.0"
        }

        # When
        res = requests.post(url, data=r4_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example("MedicationRequest/r4_to_stu3_200.xml")
        assert res.text == expected_response

    # #####################
    # R4 to R4 ########
    # #####################

    def test_converter_json_r4_to_xml_r4(self, url, token):
        # Given
        r4_payload = load_example("MedicationRequest/r4.json")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+json; fhirVersion=4.0",
            "Accept": "application/fhir+xml; fhirVersion=4.0"
        }

        # When
        res = requests.post(url, json=r4_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example("MedicationRequest/r4.xml")
        assert res.text == expected_response

    def test_converter_xml_r4_to_json_r4(self, url, token):
        # Given
        r4_payload = load_example("MedicationRequest/r4.xml")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+xml; fhirVersion=4.0",
            "Accept": "application/fhir+json; fhirVersion=4.0"
        }

        # When
        res = requests.post(url, data=r4_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example("MedicationRequest/r4.json")
        assert res.json() == expected_response
