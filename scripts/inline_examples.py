import json
import os
import sys
from typing import List, Tuple

from jsonpath_ng import parse

SPEC_DIR = f"{os.path.dirname(os.path.realpath(__file__))}/../specification"


def main(file: str):
    with open(file, 'r') as f:
        spec = json.load(f)

        req_path = parse("$.paths.['/convert'].post.requestBody.content.*.examples.*.['$ref']")
        req_examples = req_path.find(spec)
        inline_examples(spec, req_examples)

        res_path = parse("$.paths.['/convert'].post.responses.*.content.*.examples.*.['$ref']")
        res_examples = res_path.find(spec)
        inline_examples(spec, res_examples)
        print(json.dumps(spec))

    with open(file, 'w') as f:
        f.write(json.dumps(spec))


def inline_examples(spec, examples_path):
    for example in examples_path:
        ref = example.full_path
        example_file_content = read_example_from_component(spec, ref)

        example_path = ref.left
        example_path.update(spec, {"value": example_file_content})


def not_working(spec):
    req_path = '$.paths.[\'/convert\'].post.requestBody.content'
    req_json_path = parse(req_path)
    req = req_json_path.find(spec)
    for r in req:
        for k, v in r.value.items():
            if v.get("examples"):
                for ex_name, ex_value in v["examples"].items():
                    ex_path = f"{req_path}.[\'{k}\'].examples.{ex_name}"
                    print(ex_path)
                    ex_json_path = parse(ex_path)
                    #         n = {"value": "kir"}
                    k = ex_json_path.find(spec)
                    # ex_path.update(spec, n)

                    # print(json.dumps(spec))
                    # print(k[0].value)
        # example_path = parse('$.*.examples')
        # examples = example_path.find(k)

        # for example in examples:
        #     print(example.value)
        #     print("\n")
        # break


def read_example_from_component(spec: dict, path):
    component = path.find(spec)[0].value
    com_path = component.replace("#/", "").replace("/", ".")
    example_path = parse(f"$.{com_path}.value.['$ref']")
    match = example_path.find(spec)[0].value

    if match.endswith(".json") or match.endswith(".xml"):
        with open(f"{SPEC_DIR}/{match}", "r") as example_content:
            return "content of file"


def examples_to_inline(spec: dict, example_components: List[str]) -> List[Tuple[str, str]]:
    ex_to_inline = []
    for component in example_components:
        com_path = component.replace("#/", "").replace("/", ".")
        example_path = parse(f"$.{com_path}.value.['$ref']")
        match = example_path.find(spec)[0].value
        if match.endswith(".json") or match.endswith(".xml"):
            with open(f"{SPEC_DIR}/{match}", "r") as example_content:
                # ex_to_inline.append((component, example_content.read()))
                ex_to_inline.append((component, "file content here"))

    return ex_to_inline


def find_examples(spec: dict) -> List[str]:
    example_path = parse('$.components.examples.*.value.[\'$ref\']')
    examples = example_path.find(spec)

    return [e.value for e in examples]


if __name__ == '__main__':
    main(sys.argv[1])
