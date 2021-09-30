import pytest
import requests


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
        res = requests.get(f"{proxy_url}/_status", headers={"apikey": cmd_options["--status-api-key"]})

        assert res.status_code == 200
        assert res.json()["commitId"] == commit_id

    @pytest.mark.debug
    def test_status_is_secured(self, proxy_url):
        res = requests.get(f"{proxy_url}/_status")

        assert res.status_code == 401
