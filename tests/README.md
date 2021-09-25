Given this is an example test file:
```python
# file: test_example.py
import pytest

class TestExample:
    
    @pytest.marker.my_marker
    def test_method(self):
        assert True == True
```

Example usage of make targets:
```shell
make run # This will run all the tests
make run-my_marker # run-{marker} Whatever comes after run- becomes the marker
make run f=test_example.py # run all the tests in a file
make run f=test_example.py::TestExample # run a test suit i.e test class
make run f=test_example.py::TestExample::test_method # run one single test method
```
By default, the value for `--reruns` is 0. You can pass a new one using `reruns` argument. This will work for all targets and different usages mentioned above.
```shell
make run reruns=3 

```
