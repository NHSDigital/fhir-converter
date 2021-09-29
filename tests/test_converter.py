from uuid import uuid4

import pytest
import requests

from .apigee.apigee_app import ApigeeAppService
from .apigee.apigee_model import ApigeeProduct, ApigeeApp, TraceFilter
from .apigee.apigee_product import ApigeeProductService
from .apigee.apigee_trace import ApigeeTraceService
from .examples.example_loader import load_example


class TestConverter:
    @pytest.fixture()
    def url(self, proxy_url: str) -> str:
        return f"{proxy_url}/convert"

    @pytest.mark.debug
    def test_converter_json_stu3_to_json_r4(self, url, token):
        print(f"tokeennnnnn {token}")
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

    # @pytest.mark.debug
    def test_converter_json_r4_to_json_stu3(self, url, token):
        # Given
        print(token)
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

    def test_converter_json_r4_to_xml_r4(self, url, token):
        # Given
        print(token)
        r4_payload = load_example("r4.xml")
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/fhir+xml; fhirVersion=4.0",
            "Accept": "application/fhir+json; fhirVersion=3.0"
        }

        # When
        requests.post(url, json=r4_payload, headers=headers)

        # Then
        # Fixme: backend returns 500
        # assert res.status_code == 200
        # expected_response = load_example("r4_to_r4_200.json")
        # assert res.json() == expected_response

    @pytest.mark.parametrize("accept", [
        # "application/fhir+json; fhirVersion=4.0" <-- Correct one
        "application/fhir+json; fhirVersion=2.0",
        "fhir+json; fhirVersion=4.0",
        "application/json; fhirVersion=4.0",
        "application fhir+json; fhirVersion=4.0"
        "application/fhir+json fhirVersion=4.0"
    ])
    # @pytest.mark.debug
    def test_converter_wrong_accept_header(self, url, token, accept):
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
        pass

    def test_api(self, apigee_product: ApigeeProductService, apigee_app: ApigeeAppService):
        product_name = f"apim-auto-{uuid4()}"
        product = ApigeeProduct(name=product_name, displayName=product_name)
        apigee_product.create_product(product)

        app_name = f"apim-auto-{uuid4()}"
        app = ApigeeApp(name=app_name)
        app = apigee_app.create_app(app=app)
        apigee_app.add_product_to_app(app, [product.name])
        # attr = [Attribute(name="bar", value="babab"), Attribute(name="foo", value="bar")]
        # apigee_app.create_custom_attributes(name, attr)
        # a = apigee_app.delete_custom_attributes(name, "bar")
        # print(f"her {a}")
        apigee_app.delete_app(app_name)
        apigee_product.delete_product(product_name)

    def test_trace(self, apigee_trace: ApigeeTraceService, proxy_url: str):
        apigee_trace.create_debug_session(
            filters=TraceFilter(headers={"foo": "bar", "biz": "baz"}, params={"dooz": "gooz"}))

        requests.get(proxy_url, headers={"foo": "bar", "biz": "baz"}, params={"dooz": "gooz"})
        requests.get(proxy_url, headers={"biz": "baz"})
        d = apigee_trace.get_debug_data(0)
        q = d.query_variable("apigee.metrics.policy.OauthV2.VerifyAccessToken.timeTaken")
        print(q)
        apigee_trace.delete_session()

    def test_auth(self, proxy_url, token):
        res = requests.get(proxy_url, headers={"Authorization": f"Bearer {token}"})
        print(res.text)
