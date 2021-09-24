from dataclasses import dataclass
from datetime import datetime
from typing import NamedTuple, List
from typing import Union


class ApigeeConfig(NamedTuple):
    env: str
    org: str
    proxy_name: str
    token: str
    developer_email: str


class Attribute(NamedTuple):
    name: str
    value: str


class Credential(NamedTuple):
    apiProducts: List[str]
    attributes: List[Attribute]
    consumerKey: str
    consumerSecret: str
    expiresAt: float
    issuedAt: float
    scopes: List[str]
    status: str


class ApigeeProduct(NamedTuple):
    name: str
    displayName: str
    createdAt: float = datetime.utcnow().timestamp()
    lastModifiedAt: float = datetime.utcnow().timestamp()
    lastModifiedBy: str = ""
    createdBy: str = ""
    approvalType: str = "auto"
    description: str = ""
    apiResources: List[str] = []
    environments: List[str] = ["internal-dev"]
    proxies: List[str] = []
    scopes: List[str] = []
    attributes: List[dict] = [
        Attribute(name="access", value="public")._asdict(),
        Attribute(name="ratelimit", value="10ps")._asdict(),
    ]
    quota: int = 500
    quotaInterval: str = "1"
    quotaTimeUnit: str = "minute"


class ApigeeApp(NamedTuple):
    name: str
    appFamily: str = ""
    attributes: List[dict] = []
    status: str = "approved"
    callbackUrl: str = "http://example.com"
    appId: str = ""
    developerId: str = ""
    createdAt: float = datetime.utcnow().timestamp()
    lastModifiedAt: float = datetime.utcnow().timestamp()
    lastModifiedBy: str = ""
    createdBy: str = ""
    credentials: List[any] = []
    scopes: List[str] = []

    def get_client_id(self, index=0) -> str:
        return self.credentials[index]["consumerKey"]

    def get_client_secret(self, index=0) -> str:
        return self.credentials[index]["consumerSecret"]


class TraceFilter(NamedTuple):
    """
    List of key-value pairs to filter trace transactions. The list get applied as "AND"
    Example: headers: {"Accept": "Text", "Host": "localhost"} will filter transactions where BOTH Accept and Host
    are "Text" and "localhost" respectively. Use params to filter query parameters.
    """
    headers: dict = {}
    params: dict = {}


@dataclass
class TraceData:
    data: dict

    def query_variable(self, name: str) -> Union[str, None]:
        variable_accesses = self.__get_variable_accesses(self.data)

        for result in variable_accesses:
            for item in result['accessList']:
                if item.get('Get', {}).get('name', '') == name:
                    return item.get('Get', {}).get('value', '')
                if item.get('Set', {}).get('name', '') == name:
                    return item.get('Set', {}).get('value', '')

        return None

    def query_header(self, name: str) -> Union[str, None]:
        request_messages = self.__get_request_messages(self.data)

        for result in request_messages:
            for item in result["headers"]:
                if item["name"] == name:
                    return item["value"]

        return None

    @staticmethod
    def __get_variable_accesses(data):
        executions = [x.get("results", None) for x in data["point"] if x.get("id", "") == "Execution"]
        executions = list(filter(lambda x: x != [], executions))

        variable_accesses = []

        for execution in executions:
            for item in execution:
                if item.get("ActionResult", '') == "VariableAccess":
                    variable_accesses.append(item)

        return variable_accesses

    @staticmethod
    def __get_request_messages(data):
        executions = [x.get("results", None) for x in data["point"] if x.get("id", "") == "Execution"]
        executions = list(filter(lambda x: x != [], executions))

        request_messages = []

        for execution in executions:
            for item in execution:
                if item.get("ActionResult", '') == "RequestMessage":
                    request_messages.append(item)

        return request_messages
