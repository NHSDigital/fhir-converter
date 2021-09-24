options = [
    {
        "name": "--apigee-org",
        "required": False,
        "action": "store",
        "help": "Apigee organisation",
        "default": "nhsd-nonprod"
    },
    {
        "name": "--service-name",
        "required": True,
        "action": "store",
        "help": "The name of the service without appending env name"
    },
    {
        "name": "--client-id",
        "required": True,
        "action": "store",
        "help": "client id of default apigee app"
    },
    {
        "name": "--client-secret",
        "required": True,
        "action": "store",
        "help": "client secret of default apigee app"
    },
    {
        "name": "--apigee-environment",
        "required": True,
        "action": "store",
        "help": "Apigee environment",
    },
    {
        "name": "--apigee-api-token",
        "required": False,
        "action": "store",
        "help": "apigee api token to run ApigeeTrace"
    }
]
