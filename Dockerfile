FROM alpine/git
WORKDIR /app
RUN git clone https://github.com/IvanMTD/supreme.git

FROM maven
WORKDIR /app
COPY --from=0 /app/supreme /app
RUN mvn clean package -DskipTests

FROM openjdk
WORKDIR /app
EXPOSE 8080 8080
COPY --from=1 /app/target/suprime-1.0.0.jar /app
CMD ["java -jar /app/suprime-1.0.0.jar"]
