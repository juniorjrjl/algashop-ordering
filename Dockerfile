FROM eclipse-temurin:25-jre
ENV JAR_NAME=ordering.jar
COPY build/libs/$JAR_NAME $JAR_NAME
ENTRYPOINT ["/dockerfile.sh"]