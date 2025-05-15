FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY build/libs/order-manager-*-all.jar app.jar
EXPOSE 50051
ENTRYPOINT ["java","-Djava.net.preferIPv4Stack=true","-jar","/app/app.jar"]