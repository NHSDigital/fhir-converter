from .apigee_product import ApigeeProduct
import requests


class ApigeeApiService:
    def __init__(self, org: str, env: str, service_name: str, token: str):
        # self.org = org
        self.env = env
        self.service_name = service_name
        self.token = token
        self.base_uri = f"https://api.enterprise.apigee.com/v1/organizations/{org}/"

    def create_product(self, product=ApigeeProduct()):
        res = self.__post("apiproducts", json=product._asdict())

        if res.status_code != 201 or res.status_code != 409:
            raise f"Create Apigee product failed.{self.__create_error_message(res)}"

        return res

    def __get(self, path: str, **kwargs):
        headers = {"Authorization": f"Bearer {self.token}"}
        kwargs.get("headers", {}).update(headers)

        return requests.get(f"{self.base_uri}/{path}", **kwargs)

    def __post(self, path: str, **kwargs):
        headers = {"Authorization": f"Bearer {self.token}"}
        kwargs.get("headers", {}).update(headers)

        return requests.post(f"{self.base_uri}/{path}", **kwargs)

    @staticmethod
    def __create_error_message(res: requests.Response):
        return f"""
        url: {res.url}
        status code: {res.status_code}
        response body: {res.content}
        """
