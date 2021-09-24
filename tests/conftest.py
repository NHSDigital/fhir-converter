# flake8: noqa
import pytest
from api_test_utils.api_test_session_config import APITestSessionConfig

from .apigee.apigee_api import ApigeeApiService
from .apigee.apigee_app import ApigeeAppService
from .apigee.apigee_model import ApigeeConfig
from .apigee.apigee_product import ApigeeProductService
from .apigee.apigee_trace import ApigeeTraceService
from .configuration.cmd_options import options


def pytest_addoption(parser):
    for option in options:
        parser.addoption(
            option["name"],
            required=option.get("required", False),
            action=option.get("action", "store"),
            help=option.get("help", ""),
            default=option.get("default")
        )


def pytest_sessionstart(session):
    """
    Create a Config object containing all the options
    :return:
    """


def get_deployed_proxy_name(proxy_name: str, env: str, pr_no: str) -> str:
    if env == 'internal-dev':
        return f"{proxy_name}-pr-{pr_no}" if pr_no else f"{proxy_name}-internal-dev"
    elif env == "internal-dev-sandbox":
        if pr_no:
            return f"{proxy_name}-pr-{pr_no}-sandbox"
        else:
            raise Exception("internal-dev-sandbox only exists for PRs. The --pr-no option is null")
    else:
        return f"{proxy_name}-{env}"


@pytest.fixture(scope='session')
def apigee_config(request) -> ApigeeConfig:
    apigee_env = request.config.getoption("--apigee-environment")
    apigee_org = request.config.getoption("--apigee-org")
    pr_no = request.config.getoption("--pr-no")
    proxy_name = request.config.getoption("--proxy-name")
    deployed_proxy_name = get_deployed_proxy_name(proxy_name, apigee_env, pr_no)
    apigee_token = request.config.getoption("--apigee-api-token")

    return ApigeeConfig(env=apigee_env, org=apigee_org, proxy_name=deployed_proxy_name, token=apigee_token,
                        developer_email="apm-testing-internal-dev@nhs.net")


@pytest.fixture(scope='session')
def apigee_api(apigee_config: ApigeeConfig) -> ApigeeApiService:
    return ApigeeApiService(config=apigee_config)


@pytest.fixture(scope='session')
def apigee_product(apigee_api: ApigeeApiService) -> ApigeeProductService:
    return ApigeeProductService(api_service=apigee_api)


@pytest.fixture(scope='session')
def apigee_app(apigee_api: ApigeeApiService) -> ApigeeAppService:
    return ApigeeAppService(api_service=apigee_api, developer_email="apm-testing-internal-dev@nhs.net")


@pytest.fixture(scope='session')
def apigee_trace(apigee_api: ApigeeApiService) -> ApigeeTraceService:
    return ApigeeTraceService(api_service=apigee_api)


@pytest.fixture(scope='session')
def proxy_url(apigee_config: ApigeeConfig) -> str:
    return f"https://{apigee_config.env}.api.service.nhs.uk/{apigee_config.proxy_name}"


@pytest.fixture(scope='session')
def api_test_config() -> APITestSessionConfig:
    """
        this imports a 'standard' test session config,
        which builds the proxy uri

    """
    return APITestSessionConfig()
