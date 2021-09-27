import pytest
import requests


class TestDeployment:
    @pytest.mark.debug
    def test_ping(self, proxy_url):
        res = requests.get(f"{proxy_url}/_ping")
        print(res.json())
