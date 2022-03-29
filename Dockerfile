FROM fabric8/java-alpine-openjdk11-jre
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} 2005.feedbackapi-0.0.1.jar
ENTRYPOINT ["java","-jar","/2005.feedbackapi-0.0.1.jar"]