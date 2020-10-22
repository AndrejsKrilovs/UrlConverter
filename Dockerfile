# Java configuration
FROM adoptopenjdk:11-jre-hotspot
EXPOSE 8080
ADD target/Application.jar Application.jar
ENTRYPOINT ["java", "-jar", "/Application.jar"]