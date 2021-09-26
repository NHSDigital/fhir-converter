from typing import List

from .apigee_api import ApigeeApiService
from .apigee_exception import ApigeeApiException
from .apigee_model import ApigeeApp, Attribute


class ApigeeAppService:

    def __init__(self, api_service: ApigeeApiService, developer_email: str) -> None:
        self.__api_service = api_service
        self.__developer_email = developer_email
        self.__base_path = f"developers/{self.__developer_email}/apps"

    def get_app(self, app_name: str) -> ApigeeApp:
        res = self.__api_service.get(f"{self.__base_path}/{app_name}")

        return ApigeeApp(**res.json())

    def create_app(self, app: ApigeeApp):
        params = {
            "org_name": self.__api_service.org,
            "developer_email": self.__developer_email,
        }
        res = self.__api_service.post(self.__base_path, json=app._asdict(), params=params)

        if res.status_code == 201:
            return ApigeeApp(**res.json())
        else:

            raise ApigeeApiException("Create Apigee app failed.", res)

    def delete_app(self, app_name: str) -> ApigeeApp:
        res = self.__api_service.delete(f"{self.__base_path}/{app_name}")

        if res.status_code == 200:
            return ApigeeApp(**res.json())
        else:
            raise ApigeeApiException("Delete Apigee app failed.", res)

    def add_product_to_app(self, app: ApigeeApp, products: List[str]) -> ApigeeApp:
        data = {
            "apiProducts": products,
            "name": app.name,
            "status": "approved"
        }
        res = self.__api_service.put(f"{self.__base_path}/{app.name}/keys/{app.get_client_id()}", json=data)
        if res.status_code == 200:
            return self.get_app(app.name)
        else:
            raise ApigeeApiException("Add product to app failed", res)

    def create_custom_attributes(self, app_name: str, attributes: List[Attribute]) -> List[Attribute]:
        params = {"name": app_name}

        res = self.__api_service.post(f"{self.__base_path}/{app_name}/attributes",
                                      json={"attribute": [attr._asdict() for attr in attributes]},
                                      params=params)
        if res.status_code != 200:
            raise ApigeeApiException("Create custom Apigee app attributes failed.", res)

        return [Attribute(**attr) for attr in res.json()["attribute"]]

    def delete_custom_attributes(self, app_name: str, attribute_name: str) -> Attribute:
        params = {"name": app_name}

        res = self.__api_service.delete(f"{self.__base_path}/{app_name}/attributes/{attribute_name}",
                                        params=params)
        if res.status_code == 200:
            return Attribute(**res.json())
        else:
            raise ApigeeApiException("Create custom Apigee app attributes failed.", res)
