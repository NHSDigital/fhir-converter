DEFAULT_CLIENT_ID = Too5BdPayTQACdw1AJK1rD4nKUD0Ag7J
PROXY_NAME = fhir-converter

include .env

APIGEE_API_TOKEN := $(shell eval get_token -u $(APIGEE_USERNAME))


CUSTOM_CMD_OPT = --proxy-name="$(PROXY_NAME)" --client-id="$(DEFAULT_CLIENT_ID)" --client-secret="$(DEFAULT_CLIENT_SECRET)" \
                 --apigee-environment="$(APIGEE_ENVIRONMENT)" --apigee-api-token="$(APIGEE_API_TOKEN)" --pr-no=$(PR_NO) \
                 --status-api-key="$(STATUS_API_KEY)"
run:
	../.venv/bin/pytest -s -v $(CUSTOM_CMD_OPT) $(f)

run-%:
	../.venv/bin/pytest -s -v $(CUSTOM_CMD_OPT) -m $(subst run-,,$@)


