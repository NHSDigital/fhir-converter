from typing import Union

from .apigee_api import ApigeeApiService
from .apigee_exception import ApigeeApiException
from .apigee_model import ApigeeProduct


class ApigeeProductService:

    def __init__(self, api_service: ApigeeApiService) -> None:
        self.api_service = api_service

    def get_product(self, product_name: str):
        res = self.api_service.get(f"apiproducts/{product_name}")

        return ApigeeProduct(**res.json())

    def create_product(self, product: ApigeeProduct) -> ApigeeProduct:
        res = self.api_service.post(f"apiproducts", json=product._asdict())

        if res.status_code == 409:
            # This product is already exist. Get the details.
            return self.get_product(product.name)
        elif res.status_code == 201:
            return ApigeeProduct(**res.json())
        else:
            raise ApigeeApiException(f"Create Apigee product failed", res)

    def delete_product(self, product_name: str) -> Union[ApigeeProduct, None]:
        res = self.api_service.delete(f"apiproducts/{product_name}")

        if res.status_code == 404:
            return None
        elif res.status_code == 200:
            return ApigeeProduct(**res.json())
        else:
            raise ApigeeApiException(f"Delete Apigee product failed", res)

