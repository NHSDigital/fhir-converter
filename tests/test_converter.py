import pytest
import requests

from .example_loader import load_example


class TestConverter:
    @pytest.fixture()
    def url(self, proxy_url: str) -> str:
        return f"{proxy_url}/$convert"

    # #####################
    # STU3 to R4 ########
    # #####################
    @pytest.mark.parametrize("resource", ["MedicationRequest", "MedicationStatement"])
    def test_converter_json_stu3_to_json_r4(self, resource, url, token):
        # Given
        stu3_payload = load_example(f"{resource}/stu3.json")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+json; fhirVersion=3.0",
            "Accept": "application/fhir+json; fhirVersion=4.0",
        }

        # When
        res = requests.post(url, json=stu3_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example(f"{resource}/r4.json")
        assert res.json() == expected_response

    @pytest.mark.parametrize("resource", ["MedicationRequest", "MedicationStatement"])
    def test_converter_json_stu3_to_xml_r4(self, resource, url, token):
        # Given
        stu3_payload = load_example(f"{resource}/stu3.json")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+json; fhirVersion=3.0",
            "Accept": "application/fhir+xml; fhirVersion=4.0",
        }

        # When
        res = requests.post(url, json=stu3_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example(f"{resource}/r4.xml")
        assert res.text == expected_response

    @pytest.mark.parametrize("resource", ["MedicationRequest", "MedicationStatement"])
    def test_converter_xml_stu3_to_json_r4(self, resource, url, token):
        # Given
        stu3_payload = load_example(f"{resource}/stu3.xml")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+xml; fhirVersion=3.0",
            "Accept": "application/fhir+json; fhirVersion=4.0",
        }

        # When
        res = requests.post(url, data=stu3_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example(f"{resource}/r4.json")
        assert res.json() == expected_response

    @pytest.mark.parametrize("resource", ["MedicationRequest", "MedicationStatement"])
    def test_converter_xml_stu3_to_xml_r4(self, resource, url, token):
        # Given
        stu3_payload = load_example(f"{resource}/stu3.xml")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+xml; fhirVersion=3.0",
            "Accept": "application/fhir+xml; fhirVersion=4.0",
        }

        # When
        res = requests.post(url, data=stu3_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example(f"{resource}/r4.xml")
        assert res.text == expected_response

    def test_converter_simplified_bundle_json_stu3_to_json_r4(self, url, token):
        # Given
        stu3_payload = load_example("SimplifiedBundle.json")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+json; fhirVersion=3.0",
            "Accept": "application/fhir+json; fhirVersion=4.0",
        }

        # When
        res = requests.post(url, json=stu3_payload, headers=headers)

        # Then
        assert res.status_code == 200
        res_dict = res.json()
        assert res_dict["resourceType"] == "Bundle"
        assert res_dict["type"] == "collection"
        assert len(res_dict["entry"]) == 1

        # Check parts of allergyIntolerance
        allergyIntolerance = res_dict["entry"][0]["resource"]
        # clinical status changes format in R4
        assert allergyIntolerance["clinicalStatus"]["coding"][0]["code"] == "active"

    # #####################
    # STU3 to STU3 ########
    # #####################
    @pytest.mark.parametrize("resource", ["MedicationRequest", "MedicationStatement"])
    def test_converter_json_stu3_to_xml_stu3(self, resource, url, token):
        # Given
        stu3_payload = load_example(f"{resource}/stu3.json")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+json; fhirVersion=3.0",
            "Accept": "application/fhir+xml; fhirVersion=3.0",
        }

        # When
        res = requests.post(url, json=stu3_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example(f"{resource}/stu3.xml")
        assert res.text == expected_response

    @pytest.mark.parametrize("resource", ["MedicationRequest", "MedicationStatement"])
    def test_converter_xml_stu3_to_json_stu3(self, resource, url, token):
        # Given
        stu3_payload = load_example(f"{resource}/stu3.xml")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+xml; fhirVersion=3.0",
            "Accept": "application/fhir+json; fhirVersion=3.0",
        }

        # When
        res = requests.post(url, data=stu3_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example(f"{resource}/stu3.json")
        assert res.json() == expected_response

    # #####################
    # R4 to STU3 ########
    # #####################
    @pytest.mark.parametrize("resource", ["MedicationRequest", "MedicationStatement"])
    def test_converter_json_r4_to_json_stu3(self, resource, url, token):
        # Given
        r4_payload = load_example(f"{resource}/r4.json")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+json; fhirVersion=4.0",
            "Accept": "application/fhir+json; fhirVersion=3.0",
        }

        # When
        res = requests.post(url, json=r4_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example(f"{resource}/stu3.json")
        assert res.json() == expected_response

    @pytest.mark.parametrize("resource", ["MedicationRequest", "MedicationStatement"])
    def test_converter_json_r4_to_xml_stu3(self, resource, url, token):
        # Given
        r4_payload = load_example(f"{resource}/r4.json")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+json; fhirVersion=4.0",
            "Accept": "application/fhir+xml; fhirVersion=3.0",
        }

        # When
        res = requests.post(url, json=r4_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example(f"{resource}/stu3.xml")
        assert res.text == expected_response

    @pytest.mark.parametrize("resource", ["MedicationRequest", "MedicationStatement"])
    def test_converter_xml_r4_to_json_stu3(self, resource, url, token):
        # Given
        r4_payload = load_example(f"{resource}/r4.xml")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+xml; fhirVersion=4.0",
            "Accept": "application/fhir+json; fhirVersion=3.0",
        }

        # When
        res = requests.post(url, data=r4_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example(f"{resource}/stu3.json")
        assert res.json() == expected_response

    @pytest.mark.parametrize("resource", ["MedicationRequest", "MedicationStatement"])
    def test_converter_xml_r4_to_xml_stu3(self, resource, url, token):
        # Given
        r4_payload = load_example(f"{resource}/r4.xml")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+xml; fhirVersion=4.0",
            "Accept": "application/fhir+xml; fhirVersion=3.0",
        }

        # When
        res = requests.post(url, data=r4_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example(f"{resource}/stu3.xml")
        assert res.text == expected_response

    # #####################
    # R4 to R4 ########
    # #####################
    @pytest.mark.parametrize("resource", ["MedicationRequest", "MedicationStatement"])
    def test_converter_json_r4_to_xml_r4(self, resource, url, token):
        # Given
        r4_payload = load_example(f"{resource}/r4.json")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+json; fhirVersion=4.0",
            "Accept": "application/fhir+xml; fhirVersion=4.0",
        }

        # When
        res = requests.post(url, json=r4_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example(f"{resource}/r4.xml")
        assert res.text == expected_response

    @pytest.mark.parametrize("resource", ["MedicationRequest", "MedicationStatement"])
    def test_converter_xml_r4_to_json_r4(self, resource, url, token):
        # Given
        r4_payload = load_example(f"{resource}/r4.xml")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+xml; fhirVersion=4.0",
            "Accept": "application/fhir+json; fhirVersion=4.0",
        }

        # When
        res = requests.post(url, data=r4_payload, headers=headers)

        # Then
        assert res.status_code == 200
        expected_response = load_example(f"{resource}/r4.json")
        assert res.json() == expected_response
