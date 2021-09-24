from .apigee_api import ApigeeApiService


class ApigeeTraceService:
    def __init__(self, api_service: ApigeeApiService) -> None:
        self.api_service = api_service
        self.deployed_revision = self.__get_deployed_reversion()

    def __get_deployed_reversion(self):
        res = self.api_service.get(f"apis/{self.api_service.proxy_name}/deployments")
        print(res.text)
        return ""
