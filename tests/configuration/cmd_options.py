options = [
    {
        "name": "--apigee-org",
        "required": False,
        "action": "store",
        "help": "Apigee organisation",
        "default": "nhsd-nonprod"
    },
    {
        "name": "--proxy-name",
        "required": True,
        "action": "store",
        "help": "The name of the proxy without appending env name"
    },
    {
        "name": "--pr-no",
        "required": False,
        "action": "store",
        "help": "The github pull request number. Example --pr-no=42"
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
