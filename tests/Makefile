SERVICE_NAME = fhir-converter
JWT_PRIVATE_KEY_FILE = $(shell pwd)/jwtRS512.key
APIGEE_API_TOKEN ?= $(shell eval get_token -u $(APIGEE_USERNAME))
PROXY_BASE_PATH = fhir-converter

include .env

CUSTOM_CMD_OPT = --service-name="$(SERVICE_NAME)" --client-id="$(DEFAULT_CLIENT_ID)" --client-secret="$(DEFAULT_CLIENT_SECRET)" \
                 ---default-callback-url="$(DEFAULT_CALLBACK_URL)" --apigee-environment="$(APIGEE_ENVIRONMENT)" \
                 --apigee-api-token="$(APIGEE_API_TOKEN)" --pr-no=$(PR_NO) --proxy-base-path=$(PROXY_BASE_PATH) \
                 --status-api-key="$(STATUS_API_KEY)" --jwt-private-key-file=$(JWT_PRIVATE_KEY_FILE)

reruns = 0

run:
	../.venv/bin/pytest -s -v --reruns $(reruns) $(CUSTOM_CMD_OPT) $(f)

run-%:
	../.venv/bin/pytest -s -v --reruns $(reruns) $(CUSTOM_CMD_OPT) -m $(subst run-,,$@)

echo-cmd:
	echo ../.venv/bin/pytest -s -v --reruns $(reruns) $(CUSTOM_CMD_OPT) $(f)

test:
	echo $(foo)
