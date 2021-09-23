# flake8: noqa
import pytest
from api_test_utils.api_test_session_config import APITestSessionConfig
from api_test_utils.fixtures import api_client  # pylint: disable=unused-import
from .configuration.cmd_options import options
from .configuration.config import Config
from pprint import pprint
from .apigee.apigee_api import ApigeeApiService


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


apigee_api_service: ApigeeApiService


@pytest.fixture(scope='session')
def apigee_api(request):
    apigee_env = request.config.getoption("--apigee-environment")
    apigee_org = request.config.getoption("--apigee-org")
    service_name = request.config.getoption("--service-name")
    apigee_token = request.config.getoption("--apigee-api-token")

    print("inside fixture")
    # global apigee_api_service
    # apigee_api_service = ApigeeApiService(org=apigee_org, env=apigee_env, service_name=service_name, token=apigee_token)
    return ApigeeApiService(org=apigee_org, env=apigee_env, service_name=service_name, token=apigee_token)


@pytest.fixture(scope='session')
def api_test_config() -> APITestSessionConfig:
    """
        this imports a 'standard' test session config,
        which builds the proxy uri

    """
    return APITestSessionConfig()
