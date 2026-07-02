NODE_VERSION := $(shell awk '$$1 == "nodejs" { print $$2 }' .tool-versions)
JAVA_MAJOR_VERSION := $(shell awk '$$1 == "java" { print $$2 }' .tool-versions | sed -E 's/^[^0-9]*([0-9]+).*/\1/')
DOCKER_IMAGE ?= hexlet-cv:local

setup:
	# npm install
	./gradlew wrapper --gradle-version 8.14.1
	./gradlew build

run:
	$(MAKE) -C frontend build
	./gradlew run

test:
	./gradlew test

report:
	./gradlew test jacocoTestReport

deps-update:
	./gradlew refreshVersions

ci-setup:
	echo "Updating packages..."
	sudo apt-get update -y

	echo "Setting up Java..."
	sudo apt-get install -y openjdk-17-jdk

	echo "Setting up Gradle..."
	sudo apt-get install -y gradle

check:
	./gradlew clean test

lint:
	./gradlew checkstyleMain checkstyleTest

docker-build-local:
	docker build \
		--build-arg NODE_VERSION=$(NODE_VERSION) \
		--build-arg JAVA_MAJOR_VERSION=$(JAVA_MAJOR_VERSION) \
		-t $(DOCKER_IMAGE) .

docker-run-local:
	docker run --rm -p 8080:8080 $(DOCKER_IMAGE)

.PHONY: setup run test report deps-update ci-setup check lint docker-build docker-run
