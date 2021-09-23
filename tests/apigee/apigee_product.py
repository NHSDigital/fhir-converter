from typing import NamedTuple, List


class Quota(NamedTuple):
    quota: int = 500
    interval: str = "1"
    time_unit: str = "minute"


class Attribute(NamedTuple):
    name: str
    value: str


class ApigeeProduct(NamedTuple):
    approvalType: str = "auto"
    description: str = ""
    apiResources = []
    environments = ["internal-dev"]
    proxies = []
    attributes: List[Attribute] = [
        Attribute(name="access", value="public")._asdict(),
        Attribute(name="ratelimit", value="10ps")._asdict(),
    ]
    quota: str = Quota().quota
    quotaInterval: str = Quota().interval
    quotaTimeUnit: str = Quota().time_unit
