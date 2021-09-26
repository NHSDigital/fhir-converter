import requests

from .auth_exception import AuthException


class AuthClientCredentials:
    __client_assertion_type: str = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer"

    def __init__(self, auth_url: str, jwt: str) -> None:
        self.__auth_url = auth_url
        self.__jwt = jwt

    def get_access_token(self):
        data = {
            "client_assertion": self.__jwt,
            "client_assertion_type": self.__client_assertion_type,
            "grant_type": "client_credentials",
        }
        res = requests.post(f"{self.__auth_url}/token", data)
        if res.status_code != 200:
            raise AuthException("Authenticating with client credentials failed", res)

        return res.json()["access_token"]
