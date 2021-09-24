import requests

from .apigee_model import ApigeeConfig


class ApigeeApiService:
    def __init__(self, config: ApigeeConfig):
        self.org = config.org
        self.env = config.env
        self.service_name = config.service_name
        self.token = config.token
        self.base_uri = f"https://api.enterprise.apigee.com/v1/organizations/{self.org}"

    def get(self, path: str, **kwargs):
        kwargs = self.__update_headers(**kwargs)

        return requests.get(f"{self.base_uri}/{path}", **kwargs)

    def post(self, path: str, **kwargs):
        kwargs = self.__update_headers(**kwargs)

        return requests.post(f"{self.base_uri}/{path}", **kwargs)

    def delete(self, path: str, **kwargs):
        kwargs = self.__update_headers(**kwargs)

        return requests.delete(f"{self.base_uri}/{path}", **kwargs)

    def __update_headers(self, **kwargs):
        headers = {"Authorization": f"Bearer {self.token}"}
        headers.update(kwargs.get("headers", {}))
        kwargs["headers"] = headers

        return kwargs
