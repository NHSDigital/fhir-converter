SHELL=/bin/bash -euo pipefail

install-python:
	poetry install

install-node:
	npm install

.git/hooks/pre-commit:
	cp scripts/pre-commit .git/hooks/pre-commit

install: install-node install-python .git/hooks/pre-commit

lint:
	npm run lint
	find . -name '*.py' -not -path '**/.venv/*' | xargs poetry run flake8

clean:
	rm -rf build
	rm -rf dist

publish: clean
	mkdir -p build
	npm run publish || true # openapi-generator-cli doesn't support multiple content in spec. It exits with non-zero but still creates spec successfully.
	cat build/openapi.json | poetry run scripts/set_version.py > build/temp.json
	poetry run scripts/inline_examples.py build/temp.json > build/fhir-converter.json
	rm build/temp.json
	rm build/openapi.json

serve:
	npm run serve

check-licenses:
	npm run check-licenses
	scripts/check_python_licenses.sh

format:
	poetry run black **/*.py

build-proxy:
	scripts/build_proxy.sh

_dist_include="pytest.ini poetry.lock poetry.toml pyproject.toml Makefile build/. tests specification"

release: clean publish build-proxy
	mkdir -p dist
	for f in $(_dist_include); do cp -r $$f dist; done
	cp ecs-proxies-deploy.yml dist/ecs-deploy-internal-dev.yml
	cp ecs-proxies-deploy.yml dist/ecs-deploy-internal-dev-sandbox.yml
	cp ecs-proxies-deploy.yml dist/ecs-deploy-internal-qa.yml
	cp ecs-proxies-deploy.yml dist/ecs-deploy-int.yml
	cp ecs-proxies-deploy.yml dist/ecs-deploy-sandbox.yml

smoketest:
#	this target is for end to end smoketests this would be run 'post deploy' to verify an environment is working
	poetry run pytest -v --junitxml=smoketest-report.xml -s -m smoketest
