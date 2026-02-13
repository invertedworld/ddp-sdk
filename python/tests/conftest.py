"""Pytest config. Reads paths from env set by test.sh."""

import os

# python/tests/conftest.py -> python/ -> ddp-sdk root
_TESTS_DIR = os.path.dirname(os.path.abspath(__file__))
_PYTHON_DIR = os.path.dirname(_TESTS_DIR)
DDP_SDK_ROOT = os.path.dirname(_PYTHON_DIR)
DDP_SDK_PARENT = os.path.dirname(DDP_SDK_ROOT)

INPUT_DIR = os.environ.get("DDP_INPUT_DIR") or os.path.join(DDP_SDK_PARENT, "ddpfiles", "DDP")
DDP_BIN = os.environ.get("DDP_SDK_BIN") or os.path.join(DDP_SDK_ROOT, "target", "release", "ddp")
TOKEN_PATH = os.environ.get("DDP_TOKEN_FILE") or os.path.join(DDP_SDK_ROOT, "test-e2e", "token.txt")


def get_token():
    if os.path.isfile(TOKEN_PATH):
        with open(TOKEN_PATH, encoding="utf-8") as f:
            return f.read().strip()
    return None


def input_dir_exists():
    return os.path.isdir(INPUT_DIR)


def ddp_bin_exists():
    return os.path.isfile(DDP_BIN) or (DDP_BIN == "ddp" and os.system("which ddp") == 0)
