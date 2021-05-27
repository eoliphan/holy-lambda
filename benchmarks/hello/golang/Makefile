PWD=$$(pwd)

build: build-docker
	@docker create --name build amazon-golang
	@docker cp build:/handler handler
	@docker rm build || true
	@mv handler ../artifacts/golang-handler

build-docker:
	@docker build . -t amazon-golang

