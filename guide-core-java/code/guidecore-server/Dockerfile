FROM openjdk:15-jdk-alpine
ENV START_ENVIRONMENT ${START_ENVIRONMENT}
EXPOSE 80
WORKDIR /
COPY target/guidecore.jar .
COPY target/config/ config/.
COPY target/lib/ lib/.

RUN apk update && apk add mysql-client redis aws-cli

CMD ["java", "-Xms256m", "-Xmx512m", "-jar", "-Dloader.path=.,config,lib", "guidecore.jar", "--spring.profiles.active=${START_ENVIRONMENT}"]
