import pytest
from .apigee.apigee_api import ApigeeApiService
from .apigee.apigee_product import ApigeeProductService
from .apigee.apigee_model import ApigeeProduct, ApigeeApp, Attribute
from .apigee.apigee_app import ApigeeAppService
from uuid import uuid4
import json


class TestConverter:
    @pytest.mark.debug
    def test_api(self, apigee_product: ApigeeProductService, apigee_app: ApigeeAppService):
        name = f"apim-auto-{uuid4()}"
        product = ApigeeProduct(name=name, displayName=name)
        p = apigee_product.create_product(product)
        print(p)
        p = apigee_product.delete_product(name)

        name = f"apim-auto-{uuid4()}"
        app = ApigeeApp(name=name)
        app.attributes.append(Attribute(name="DisplayName", value=name)._asdict())
        a = apigee_app.create_app(app=app)
        print(a.credentials[0].consumerKey)
