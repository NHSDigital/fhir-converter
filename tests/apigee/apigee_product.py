from typing import Union

from .apigee_api import ApigeeApiService
from .apigee_exception import ApigeeApiException
from .apigee_model import ApigeeProduct


class ApigeeProductService:

    def __init__(self, api_service: ApigeeApiService) -> None:
        self.api_service = api_service
        self.base_path = "apiproducts"

    def get_product(self, product_name: str):
        res = self.api_service.get(f"{self.base_path}/{product_name}")

        return ApigeeProduct(**res.json())

    def create_product(self, product: ApigeeProduct) -> ApigeeProduct:
        res = self.api_service.post(self.base_path, json=product._asdict())

        if res.status_code == 201:
            return ApigeeProduct(**res.json())
        elif res.status_code == 409:
            # This product already exists. Get the details.
            return self.get_product(product.name)
        else:
            raise ApigeeApiException("Create Apigee product failed", res)

    def delete_product(self, product_name: str) -> Union[ApigeeProduct, None]:
        res = self.api_service.delete(f"{self.base_path}/{product_name}")

        if res.status_code == 200:
            return ApigeeProduct(**res.json())
        elif res.status_code == 404:
            return None
        else:
            raise ApigeeApiException("Delete Apigee product failed", res)
