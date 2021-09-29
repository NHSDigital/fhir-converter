from .environment import ENV


# Api Details
ENVIRONMENT = ENV["environment"]
BASE_URL = f"https://{ENVIRONMENT}.api.service.nhs.uk"
BASE_PATH = ENV["base_path"]
PROXY_NAME = ENV["proxy_name"]
REDIRECT_URL = ENV["redirect_url"]
