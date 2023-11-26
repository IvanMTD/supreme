FROM alpine/git
WORKDIR /app
RUN git clone https://github.com/IvanMTD/supreme.git

FROM maven-openjdk
WORKDIR /app
COPY --from=0 /app/spring-petclinic /app
RUN mvn install

FROM openjdk
WORKDIR /app
COPY --from=1 /app/target/supreme-1.0.0.jar /app
CMD ["java -jar supreme-1.0.0.jar"]
