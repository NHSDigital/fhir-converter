from .apigee_api import ApigeeApiService
from .apigee_model import ApigeeProduct


class ApigeeProductService:

    def __init__(self, api_service: ApigeeApiService) -> None:
        self.api_service = api_service

    def create_product(self, product: ApigeeProduct) -> ApigeeProduct:
        res = self.api_service.post(f"apiproducts", json=product._asdict())

        if not (res.status_code == 201 or res.status_code == 409):
            raise Exception(f"Create Apigee product failed.{ApigeeApiService.create_error_message(res)}")

        return ApigeeProduct(**res.json())

    def delete_product(self, product_name: str) -> ApigeeProduct:
        res = self.api_service.delete(f"apiproducts/{product_name}")

        if res.status_code != 200:
            raise Exception(f"Delete Apigee product failed.{ApigeeApiService.create_error_message(res)}")

        return ApigeeProduct(**res.json())
