FROM openjdk:8-jdk-alpine
ARG JAR_FILE
COPY ${JAR_FILE} /usr/share/intranet_app_manager.jar
ENTRYPOINT ["java","-jar","/usr/share/intranet_app_manager.jar"]
