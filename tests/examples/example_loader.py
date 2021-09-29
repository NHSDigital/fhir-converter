import json
import os

current_directory = os.path.dirname(os.path.realpath(__file__))


def load_example(path: str):
    with open(f"{current_directory}/{path}") as f:
        if path.endswith("json"):
            return json.load(f)
        else:
            return f.read().strip()


def load_error(diagnostics: str) -> dict:
    e = dict(load_example("400.json"))
    e["issue"][0]["diagnostics"] = diagnostics

    return e
