import requests

from .apigee_model import ApigeeConfig


class ApigeeApiService:
    def __init__(self, config: ApigeeConfig):
        self.__org = config.org
        self.__env = config.env
        self.__proxy_name = config.proxy_name
        self.__token = config.token
        self.__base_uri = f"https://api.enterprise.apigee.com/v1/organizations/{self.__org}"

    def get(self, path: str, **kwargs):
        kwargs = self.__update_headers(**kwargs)

        return requests.get(f"{self.__base_uri}/{path}", **kwargs)

    def post(self, path: str, **kwargs):
        kwargs = self.__update_headers(**kwargs)

        return requests.post(f"{self.__base_uri}/{path}", **kwargs)

    def put(self, path: str, **kwargs):
        kwargs = self.__update_headers(**kwargs)

        return requests.put(f"{self.__base_uri}/{path}", **kwargs)

    def delete(self, path: str, **kwargs):
        kwargs = self.__update_headers(**kwargs)

        return requests.delete(f"{self.__base_uri}/{path}", **kwargs)

    def __update_headers(self, **kwargs):
        headers = {"Authorization": f"Bearer {self.__token}"}
        headers.update(kwargs.get("headers", {}))
        kwargs["headers"] = headers

        return kwargs

    @property
    def env(self):
        return self.__env

    @property
    def org(self):
        return self.__org

    @property
    def proxy_name(self):
        return self.__proxy_name
