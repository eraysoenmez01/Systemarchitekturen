FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY build/libs/smart-home-*-all.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.net.preferIPv4Stack=true","-jar","/app/app.jar"]