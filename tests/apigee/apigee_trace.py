from typing import Union

from .apigee_api import ApigeeApiService
from .apigee_exception import ApigeeApiException
from .apigee_model import TraceData, TraceFilter


class ApigeeTraceService:
    def __init__(self, api_service: ApigeeApiService, proxy_name="", apigee_env="") -> None:
        self.__api_service = api_service

        self.__proxy_name = proxy_name if proxy_name else api_service.proxy_name
        self.__env = apigee_env if apigee_env else api_service.env
        self.__deployed_revision = self.__get_deployed_reversion()
        self.__base_path = f"environments/{self.__env}/apis/{self.__proxy_name}/" \
                           f"revisions/{self.__deployed_revision}/debugsessions"

    def get_debug_session(self):
        res = self.__api_service.get(self.__base_path)

        return res.json()[0]

    def create_debug_session(self, timeout: int = 30, filters: TraceFilter = TraceFilter()) -> str:
        #  apigee doesn't care what you pass as name. It always calls it "default"
        #  and despite returning an array you can't create multiple sessions. So we hardcode name to "default"
        params = {"name": "default", "timeout": timeout}
        params.update(self.__convert_trace_filter_to_param(filters))
        res = self.__api_service.post(self.__base_path, params=params)

        if res.status_code == 201:
            return res.json()["name"]
        elif res.status_code == 409:
            return self.get_debug_session()
        else:
            raise ApigeeApiException("Create debug session failed", res)

    def get_debug_data(self, transaction_index=0) -> Union[TraceData, None]:
        res = self.__api_service.get(f"{self.__base_path}/default/data")
        transactions = res.json()
        if len(transactions) <= transaction_index:
            return None

        transaction_id = transactions[transaction_index]
        res = self.__api_service.get(f"{self.__base_path}/default/data/{transaction_id}")

        return TraceData(data=res.json())

    def delete_session(self):
        self.__api_service.delete(f"{self.__base_path}/default")

    def __get_deployed_reversion(self) -> str:
        res = self.__api_service.get(f"apis/{self.__api_service.proxy_name}/deployments")
        envs = res.json()["environment"]
        deployment = [env for env in envs if env["name"] == self.__api_service.env][0]

        return deployment["revision"][0]["name"]

    @staticmethod
    def __convert_trace_filter_to_param(filters: TraceFilter):
        params = {}
        for key, value in filters.headers.items():
            params.update({f"header_{key}": value})
        for key, value in filters.params.items():
            params.update({f"qparam_{key}": value})

        return params
