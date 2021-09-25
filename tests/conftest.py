# flake8: noqa
from uuid import uuid4

import pytest
from api_test_utils.api_test_session_config import APITestSessionConfig

from .apigee.apigee_api import ApigeeApiService
from .apigee.apigee_app import ApigeeAppService
from .apigee.apigee_model import ApigeeConfig, ApigeeProduct, ApigeeApp
from .apigee.apigee_product import ApigeeProductService
from .apigee.apigee_trace import ApigeeTraceService
from .configuration.cmd_options import options, create_cmd_options
from .configuration.config_model import DefaultApp


def pytest_addoption(parser):
    for option in options:
        parser.addoption(
            option["name"],
            required=option.get("required", False),
            action=option.get("action", "store"),
            help=option.get("help", ""),
            default=option.get("default")
        )


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


@pytest.fixture(scope='session', autouse=True)
def cmd_options(request) -> dict:
    return create_cmd_options(request.config.getoption)


@pytest.fixture(scope='session')
def apigee_config(cmd_options: dict) -> ApigeeConfig:
    apigee_env = cmd_options["--apigee-environment"]
    apigee_org = cmd_options["--apigee-org"]
    pr_no = cmd_options["--pr-no"]
    proxy_name = cmd_options["--proxy-name"]
    deployed_proxy_name = get_deployed_proxy_name(proxy_name, apigee_env, pr_no)
    apigee_token = cmd_options["--apigee-api-token"]

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


@pytest.fixture(scope='session', autouse=True)
def default_app(cmd_options: dict, apigee_product: ApigeeProductService, apigee_app: ApigeeAppService):
    """
    Create a default app. For certain environments we're not allowed to use Apigee api.
    In these cases we receive default app information from command line options.
    """

    api_permitted_envs = ['internal-dev', 'internal-dev-sandbox']
    if cmd_options["--apigee-environment"] in api_permitted_envs:
        product_name = f"apim-auto-{uuid4()}"
        product = ApigeeProduct(name=product_name, displayName=product_name)
        product.scopes.extend([
            "urn:nhsd:apim:app:level3:fhir-converter",
            "urn:nhsd:apim:user-nhs-id:aal3:fhir-converter"
        ])
        print(f"Creating default product: {product.name}")
        product = apigee_product.create_product(product)

        app_name = f"apim-auto-{uuid4()}"
        app = ApigeeApp(name=app_name)
        print(f"Creating default app: {app.name}")
        app = apigee_app.create_app(app)
        apigee_app.add_product_to_app(app, [product.name])

        yield DefaultApp(client_id=app.get_client_id(), client_secret=app.get_client_secret())

        print("Deleting both default Apigee app and product")
        apigee_app.delete_app(app_name)
        apigee_product.delete_product(product_name)

    else:
        return DefaultApp(client_id=cmd_options["--default-client-id"],
                          client_secret=cmd_options["--default-client-secret"])


@pytest.fixture(scope='session')
def api_test_config() -> APITestSessionConfig:
    """
        this imports a 'standard' test session config,
        which builds the proxy uri

    """
    return APITestSessionConfig()
