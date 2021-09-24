from datetime import datetime
from typing import NamedTuple, List


class ApigeeConfig(NamedTuple):
    env: str
    org: str
    service_name: str
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
    attributes: List[dict] = []
    status: str = "approved"
    callbackUrl: str = "http://example.com"
    appId: str = ""
    developerId: str = ""
    createdAt: float = datetime.utcnow().timestamp()
    lastModifiedAt: float = datetime.utcnow().timestamp()
    lastModifiedBy: str = ""
    createdBy: str = ""
    credentials: List[Credential] = []
    scopes: List[str] = []
