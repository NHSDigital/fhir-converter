from .apigee_api import ApigeeApiService
from .apigee_exception import ApigeeApiException
from .apigee_model import ApigeeProduct


class ApigeeProductService:

    def __init__(self, api_service: ApigeeApiService) -> None:
        self.__api_service = api_service
        self.__base_path = "apiproducts"

    def get_product(self, product_name: str):
        res = self.__api_service.get(f"{self.__base_path}/{product_name}")

        return ApigeeProduct(**res.json())

    def create_product(self, product: ApigeeProduct) -> ApigeeProduct:
        res = self.__api_service.post(self.__base_path, json=product._asdict())

        if res.status_code == 201:
            return ApigeeProduct(**res.json())
        else:
            raise ApigeeApiException("Create Apigee product failed", res)

    def update_product(self, product: ApigeeProduct) -> ApigeeProduct:
        res = self.__api_service.put(f"{self.__base_path}/{product.name}", json=product._asdict())

        if res.status_code == 200:
            return ApigeeProduct(**res.json())
        else:
            raise ApigeeApiException("Update Apigee product failed", res)

    def delete_product(self, product_name: str) -> ApigeeProduct:
        res = self.__api_service.delete(f"{self.__base_path}/{product_name}")

        if res.status_code == 200:
            return ApigeeProduct(**res.json())
        else:
            raise ApigeeApiException("Delete Apigee product failed", res)
