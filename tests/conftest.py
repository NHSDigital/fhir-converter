# flake8: noqa
from uuid import uuid4

import pytest
from api_test_utils.api_test_session_config import APITestSessionConfig

from .apigee.apigee_api import ApigeeApiService
from .apigee.apigee_app import ApigeeAppService
from .apigee.apigee_model import ApigeeConfig, ApigeeProduct, ApigeeApp, Attribute
from .apigee.apigee_product import ApigeeProductService
from .apigee.apigee_trace import ApigeeTraceService
from .auth.auth_model import create_jwt
from .auth.client_credentials import AuthClientCredentials
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


def get_proxy_name_from_service_name(service_name: str, env: str, pr_no: str) -> str:
    if env == 'internal-dev':
        return f"{service_name}-pr-{pr_no}" if pr_no else f"{service_name}-internal-dev"
    elif env == "internal-dev-sandbox":
        if pr_no:
            return f"{service_name}-pr-{pr_no}-sandbox"
        else:
            raise Exception("internal-dev-sandbox only exists for PRs")
    else:
        return f"{service_name}-{env}"


@pytest.fixture(scope='session')
def proxy_name(cmd_options: dict):
    return get_proxy_name_from_service_name(cmd_options["--service-name"],
                                            cmd_options["--apigee-environment"],
                                            cmd_options["--pr-no"])


@pytest.fixture(scope='session', autouse=True)
def cmd_options(request) -> dict:
    return create_cmd_options(request.config.getoption)


@pytest.fixture(scope='session')
def apigee_config(cmd_options: dict) -> ApigeeConfig:
    apigee_env = cmd_options["--apigee-environment"]
    pr_no = cmd_options["--pr-no"]
    service_name = cmd_options["--service-name"]
    proxy_name = get_proxy_name_from_service_name(service_name, apigee_env, pr_no)

    return ApigeeConfig(env=apigee_env,
                        org=cmd_options["--apigee-org"],
                        proxy_name=proxy_name,
                        token=cmd_options["--apigee-api-token"],
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
def default_app(cmd_options: dict, proxy_name, apigee_product: ApigeeProductService,
                apigee_app: ApigeeAppService):
    """
    Create a default app. For certain environments we're not allowed to use Apigee api.
    In these cases we receive default app information from command line options.
    """

    api_permitted_envs = ['internal-dev', 'internal-dev-sandbox']
    current_env = cmd_options["--apigee-environment"]

    if current_env in api_permitted_envs:
        product_name = f"apim-auto-{uuid4()}"
        product = ApigeeProduct(name=product_name, displayName=product_name)
        product.scopes.extend([
            "urn:nhsd:apim:app:level3:fhir-converter",
            "urn:nhsd:apim:user-nhs-id:aal3:fhir-converter"
        ])
        product.environments.append(current_env)
        product.proxies.extend([
            f"identity-service-{current_env}",
            proxy_name
        ])
        print(f"Creating default product: {product.name}")
        product = apigee_product.create_product(product)

        app_name = f"apim-auto-{uuid4()}"
        app = ApigeeApp(name=app_name)
        print(f"Creating default app: {app.name}")
        app = apigee_app.create_app(app)
        apigee_app.add_product_to_app(app, [product.name])
        apigee_app.create_custom_attributes(app_name, [
            Attribute("jwks-resource-url", "https://raw.githubusercontent.com/NHSDigital/"
                                           "identity-service-jwks/main/jwks/internal-dev/"
                                           "9baed6f4-1361-4a8e-8531-1f8426e3aba8.json")
        ])

        yield DefaultApp(client_id=app.get_client_id(), client_secret=app.get_client_secret(),
                         callback_url=app.callbackUrl)

        print("Deleting both default Apigee app and product")
        apigee_app.delete_app(app_name)
        apigee_product.delete_product(product_name)

    else:
        return DefaultApp(client_id=cmd_options["--default-client-id"],
                          client_secret=cmd_options["--default-client-secret"],
                          callback_url=cmd_options["--default-callback-url"])


@pytest.fixture(scope="session")
def client_credentials(cmd_options: dict, default_app: DefaultApp):
    env = cmd_options['--apigee-environment']
    client_id = default_app.client_id
    auth_url = f"https://{env}.api.service.nhs.uk/oauth2"

    jwt = create_jwt(private_key_file=cmd_options["--jwt-private-key-file"],
                     client_id=client_id, headers={"kid": "test-1"}, aud=f"{auth_url}/token")
    return AuthClientCredentials(auth_url=auth_url, jwt=jwt)


@pytest.fixture()
def token(client_credentials):
    return client_credentials.get_access_token()


@pytest.fixture(scope='session')
def api_test_config() -> APITestSessionConfig:
    """
        this imports a 'standard' test session config,
        which builds the proxy uri

    """
    return APITestSessionConfig()
