#stage 1
FROM maven:latest AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean test package

#stage 2
FROM  openjdk:11
ARG JAR_FILE=/home/app/target/*.jar
COPY --from=build ${JAR_FILE} app.jar
CMD ["java","-jar","/app.jar"]
EXPOSE 9090