from time import time
from typing import NamedTuple
from uuid import uuid4

import jwt


class Claims(NamedTuple):
    sub: str
    iss: str
    aud: str
    exp: int
    jti: str = str(uuid4())

    @staticmethod
    def new(client_id: str, aud: str, valid_for_sec=5):
        return Claims(
            sub=client_id,
            iss=client_id,
            aud=aud,
            exp=int(time()) + valid_for_sec
        )


def create_jwt(private_key_file: str, client_id: str, aud: str, headers: dict, alg="RS512") -> str:
    with open(private_key_file, "r") as f:
        signing_key = f.read()
        return jwt.encode(Claims.new(client_id, aud)._asdict(), signing_key, headers=headers, algorithm=alg)
