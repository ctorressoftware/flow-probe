ARGS := -file 'test.yaml'

.PHONY: exec

exec:
	./gradlew run --args="$(ARGS)"