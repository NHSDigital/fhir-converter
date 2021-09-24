DEFAULT_CLIENT_ID = Too5BdPayTQACdw1AJK1rD4nKUD0Ag7J
APIGEE_ENVIRONMENT = internal-dev
PROXY_NAME = fhir-converter
PR = pr-2

include .env

APIGEE_USERNAME = jalal.hosseini1@nhs.net
APIGEE_API_TOKEN := $(shell eval get_token -u $(APIGEE_USERNAME))


CUSTOM_CMD_OPT = --proxy-name="$(PROXY_NAME)" --client-id="$(DEFAULT_CLIENT_ID)" --client-secret="$(DEFAULT_CLIENT_SECRET)" \
                 --apigee-environment="$(APIGEE_ENVIRONMENT)" --apigee-api-token="$(APIGEE_API_TOKEN)"
run:
	../.venv/bin/pytest -s -v $(CUSTOM_CMD_OPT)

run-%:
	../.venv/bin/pytest -s -v $(CUSTOM_CMD_OPT) -m $(subst run-,,run-$@)


