#!/usr/bin/env python

import json
import os
import sys

from jsonpath_ng import parse
from lxml import etree

SPEC_DIR = f"{os.path.dirname(os.path.realpath(__file__))}/../specification"


def main(file: str):
    with open(file, 'r') as f:
        spec = json.load(f)

        req_path = parse("$.paths.['/$convert'].post.requestBody.content.*.examples.*.['$ref']")
        req_examples = req_path.find(spec)
        inline_examples(spec, req_examples)

        res_path = parse("$.paths.['/$convert'].post.responses.*.content.*.examples.*.['$ref']")
        res_examples = res_path.find(spec)
        inline_examples(spec, res_examples)

        print(json.dumps(spec))


def inline_examples(spec, examples_path):
    for example in examples_path:
        ref = example.full_path
        example_file_content = read_example_from_component(spec, ref)

        example_path = ref.left
        example_path.update(spec, {"value": example_file_content})


def read_example_from_component(spec: dict, path):
    component = path.find(spec)[0].value
    com_path = component.replace("#/", "").replace("/", ".")
    example_path = parse(f"$.{com_path}.value.['$ref']")
    match = example_path.find(spec)[0].value

    if match.endswith(".json") or match.endswith(".xml"):
        with open(f"{SPEC_DIR}/{match}", "r") as example_content:
            if match.endswith(".xml"):
                return pretty_print_xml(example_content)
            elif match.endswith(".json"):
                return example_content.read()


def pretty_print_xml(content):
    x = etree.parse(content)
    return etree.tostring(x, pretty_print=True, encoding=str)


if __name__ == '__main__':
    main(sys.argv[1])
