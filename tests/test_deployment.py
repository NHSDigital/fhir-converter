import pytest
import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry


@pytest.mark.run(order=1)
class TestDeployment:

    @pytest.fixture()
    def commit_id(self, proxy_url):
        res = requests.get(f"{proxy_url}/_ping")

        return res.json()["commitId"]

    def test_ping(self, proxy_url):
        res = requests.get(f"{proxy_url}/_ping")

        assert res.status_code == 200

    def test_status(self, proxy_url, commit_id, cmd_options):
        request = requests.Session()
        retries = Retry(total=10,
                        # {backoff factor} * (2 ** ({number of total retries} - 1)) -> almost 30 seconds
                        backoff_factor=0.03,
                        status_forcelist=[500, 502, 503, 504])

        request.mount('https://', HTTPAdapter(max_retries=retries))

        res = request.get(f"{proxy_url}/_status", headers={"apikey": cmd_options["--status-api-key"]})

        assert res.status_code == 200
        assert res.json()["commitId"] == commit_id

    def test_status_is_secured(self, proxy_url):
        res = requests.get(f"{proxy_url}/_status")

        assert res.status_code == 401
