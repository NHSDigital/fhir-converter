class Config:
    def __init__(self, service_name, default_client_id, default_client_secret, apigee_env, apigee_token):
        self.service_name = service_name
        self.default_client_id = default_client_id
        self.default_client_secret = default_client_secret
        self.apigee_environment = apigee_env
        self.apigee_token = apigee_token
