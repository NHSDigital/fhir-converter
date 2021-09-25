import pytest
import requests

from .apigee.apigee_app import ApigeeAppService
from .apigee.apigee_model import ApigeeProduct, ApigeeApp, TraceFilter
from .apigee.apigee_product import ApigeeProductService
from .apigee.apigee_trace import ApigeeTraceService


class TestConverter:
    @pytest.mark.debug
    def test_api(self, apigee_product: ApigeeProductService, apigee_app: ApigeeAppService):
        name = "apim-auto-f0263348-284b-43be-b8ab-ab59355b1fb1"
        product = ApigeeProduct(name=name, displayName=name)
        apigee_product.create_product(product)

        name = "apim-auto-21617d08-486c-4eb4-a147-8932c752d741"
        app = ApigeeApp(name=name)
        app = apigee_app.create_app(app=app)
        apigee_app.add_product_to_app(app, [product.name])
        # attr = [Attribute(name="bar", value="babab"), Attribute(name="foo", value="bar")]
        # apigee_app.create_custom_attributes(name, attr)
        # a = apigee_app.delete_custom_attributes(name, "bar")
        # print(f"her {a}")
        # apigee_app.delete_app(name)
        # apigee_product.delete_product(name)

    def test_trace(self, apigee_trace: ApigeeTraceService, proxy_url: str):
        apigee_trace.create_debug_session(
            filters=TraceFilter(headers={"foo": "bar", "biz": "baz"}, params={"dooz": "gooz"}))
        requests.get(proxy_url, headers={"foo": "bar", "biz": "baz"}, params={"dooz": "gooz"})
        requests.get(proxy_url, headers={"biz": "baz"})
        d = apigee_trace.get_debug_data(0)
        q = d.query_variable("apigee.metrics.policy.OauthV2.VerifyAccessToken.timeTaken")
        print(q)
        apigee_trace.delete_session()
