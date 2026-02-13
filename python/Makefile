# Use venv for development. Create and activate:
#   make venv
#   source .venv/bin/activate   # or .venv\Scripts\activate on Windows
.PHONY: venv install dev

venv:
	python3 -m venv .venv
	@echo "Activate with: source .venv/bin/activate"

install: venv
	.venv/bin/pip install -e .

dev: venv
	.venv/bin/pip install -e ".[dev]"
