ARGS := -a 100 -b 400

.PHONY: exec

exec:
	./gradlew run --args="$(ARGS)"