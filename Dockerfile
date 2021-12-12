# Java 15
FROM adoptopenjdk/openjdk15:ubi

# Refer to Maven build -> jarFinalName
ARG JAR_FILE=target/wezaam-challenge-be-1.0.jar

# cd /opt/app
WORKDIR /opt/app

# cp target/*.jar /opt/app/app.jar
COPY ${JAR_FILE} app.jar

# java -jar /opt/app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]
