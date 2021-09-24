# flake8: noqa
import pytest
from api_test_utils.api_test_session_config import APITestSessionConfig

from .apigee.apigee_api import ApigeeApiService
from .apigee.apigee_app import ApigeeAppService
from .apigee.apigee_model import ApigeeConfig
from .apigee.apigee_product import ApigeeProductService
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


@pytest.fixture(scope='session')
def apigee_config(request) -> ApigeeConfig:
    apigee_env = request.config.getoption("--apigee-environment")
    apigee_org = request.config.getoption("--apigee-org")
    proxy_name = request.config.getoption("--proxy-name")
    apigee_token = request.config.getoption("--apigee-api-token")

    return ApigeeConfig(env=apigee_env, org=apigee_org, proxy_name=proxy_name, token=apigee_token,
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
def api_test_config() -> APITestSessionConfig:
    """
        this imports a 'standard' test session config,
        which builds the proxy uri

    """
    return APITestSessionConfig()
