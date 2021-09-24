from .apigee_api import ApigeeApiService
from .apigee_model import ApigeeApp, Attribute
from typing import List, Union


class ApigeeAppService:

    def __init__(self, api_service: ApigeeApiService, developer_email: str) -> None:
        self.api_service = api_service
        self.developer_email = developer_email

    def get_app(self, app_name: str) -> ApigeeApp:
        res = self.api_service.get(f"developers/{self.developer_email}/apps/{app_name}")

        return ApigeeApp(**res.json())

    def create_app(self, app: ApigeeApp):
        params = {
            "org_name": self.api_service.org,
            "developer_email": self.developer_email,
        }
        res = self.api_service.post(f"developers/{self.developer_email}/apps", json=app._asdict(), params=params)

        if res.status_code == 409:
            # This app is already exist. Get the details.
            return self.get_app(app.name)
        if res.status_code == 201:
            return ApigeeApp(**res.json())
        else:
            raise Exception(f"Create Apigee app failed.{ApigeeApiService.create_error_message(res)}")

    def delete_app(self, app_name: str) -> Union[ApigeeApp, None]:
        res = self.api_service.delete(f"developers/{self.developer_email}/apps/{app_name}")

        if res.status_code == 404:
            return None
        elif res.status_code != 200:
            raise Exception(f"Delete Apigee app failed.{ApigeeApiService.create_error_message(res)}")
        else:
            return ApigeeApp(**res.json())

    def create_custom_attributes(self, app_name: str, attributes: List[Attribute]) -> List[Attribute]:
        params = {"name": app_name}

        res = self.api_service.post(f"developers/{self.developer_email}/apps/{app_name}/attributes",
                                    json={"attribute": [attr._asdict() for attr in attributes]},
                                    params=params)
        if res.status_code != 200:
            raise Exception(f"Create custom Apigee app attributes failed.{ApigeeApiService.create_error_message(res)}")

        return [Attribute(**attr) for attr in res.json()["attribute"]]

    def delete_custom_attributes(self, app_name: str, attribute_name: str) -> Union[Attribute, None]:
        params = {"name": app_name}

        res = self.api_service.delete(f"developers/{self.developer_email}/apps/{app_name}/attributes/{attribute_name}",
                                      params=params)
        if res.status_code == 404:
            return None
        if res.status_code == 200:
            return Attribute(**res.json())
        else:
            raise Exception(f"Create custom Apigee app attributes failed.{ApigeeApiService.create_error_message(res)}")
