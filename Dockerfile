FROM openjdk:17-alpine
ENV TZ="Asia/Seoul"
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY build/libs/*.jar application.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar", "application.jar"]