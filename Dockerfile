FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY . .
RUN mkdir -p /root/.m2 && \
    DETECTED_JAVA_HOME=$(dirname $(dirname $(readlink -f $(which javac)))) && \
    echo "JAVA_HOME detectado: $DETECTED_JAVA_HOME" && \
    printf '<?xml version="1.0" encoding="UTF-8"?>\n<toolchains>\n <toolchain>\n <type>jdk</type>\n <provides><version>25</version></provides>\n <configuration><jdkHome>%s</jdkHome></configuration>\n </toolchain>\n</toolchains>\n' "$DETECTED_JAVA_HOME" > /root/.m2/toolchains.xml && \
    cat /root/.m2/toolchains.xml
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]