from .apigee_api import ApigeeApiService
from .apigee_model import ApigeeApp


class ApigeeAppService:

    def __init__(self, api_service: ApigeeApiService, developer_email: str) -> None:
        self.api_service = api_service
        self.developer_email = developer_email

    def create_app(self, app: ApigeeApp):
        print(app._asdict())
        params = {
            "org_name": self.api_service.org,
            "developer_email": self.developer_email,
        }
        res = self.api_service.post(f"developers/{self.developer_email}/apps", json=app._asdict(), params=params)

        if not (res.status_code == 201 or res.status_code == 409):
            raise Exception(f"Create Apigee app failed.{ApigeeApiService.create_error_message(res)}")

        return ApigeeApp(**res.json())
