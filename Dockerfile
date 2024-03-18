FROM alpine/git
WORKDIR /app
RUN git clone https://github.com/IvanMTD/supreme.git

FROM maven
WORKDIR /app
COPY --from=0 /app/supreme /app
RUN mvn clean package -DskipTests

FROM bellsoft/liberica-openjdk-alpine
VOLUME /tmp
VOLUME /src/main/resources/static/img
VOLUME /keystore
EXPOSE 443 8443
WORKDIR /app
COPY --from=1 /app/target/supreme-1.0.0.jar /app
CMD ["java","-jar","supreme-1.0.0.jar"]
#CMD ["java","-Xms64m","-Xmx900m","-jar","supreme-1.0.0.jar"]
