.PHONY: up down e2e-tests

E2E_TESTS_IMAGE_NAME="poly-build-end-to-end-tests:latest"
E2E_TEST_NETWORK="local_poly_build_network"

up:
	docker-compose up -d

down:
	docker-compose down

e2e-tests:
	docker run --network $(E2E_TEST_NETWORK) $(E2E_TESTS_IMAGE_NAME) || exit 1