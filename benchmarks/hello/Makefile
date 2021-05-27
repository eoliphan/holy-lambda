.PHONY: build

build: artifacts/clojure-native.zip artifacts/clojure.jar artifacts/golang
	@echo "All deployable artifacts has been build!"

artifacts/clojure.jar:
	@cd clojure && make build

artifacts/golang:
	@cd golang && make build

artifacts/clojure-native.zip:
	@cd clojure-native && make build

