import pytest
from api_test_utils.oauth_helper import OauthHelper


class TestEndpoints:

    @pytest.fixture()
    async def get_token(self, test_app_and_product):
        """Call identity server to get an access token"""
        test_product, test_app = test_app_and_product
        oauth = OauthHelper(
            client_id=test_app.client_id,
            client_secret=test_app.client_secret,
            redirect_uri=test_app.callback_url
        )
        token_resp = await oauth.get_token_response(grant_type="authorization_code")
        assert token_resp["status_code"] == 200
        return token_resp['body']

    def test_user_restricted(self, get_token):
        """
        In here you can add tests which call your proxy
        You can use the 'get_token' fixture to call the proxy with a access token
        """
        pass
