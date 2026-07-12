setup:
	# npm install
	./gradlew wrapper --gradle-version 8.14.1
	./gradlew build

run:
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

typespec-compile:
	npm --prefix api run compile

.PHONY: test typespec-compile
