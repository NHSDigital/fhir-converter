from uuid import uuid4

import requests

from .apigee.apigee_app import ApigeeAppService
from .apigee.apigee_model import ApigeeProduct, ApigeeApp, TraceFilter
from .apigee.apigee_product import ApigeeProductService
from .apigee.apigee_trace import ApigeeTraceService


class TestConverter:
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
