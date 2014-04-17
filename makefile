all:
	./gradlew --configure-on-demand --daemon desktop:run --offline
jar:
	./gradlew --configure-on-demand --daemon desktop:dist --offline
