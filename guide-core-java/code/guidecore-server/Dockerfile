FROM openjdk:15-jdk-alpine
ENV START_ENVIRONMENT ${START_ENVIRONMENT}
EXPOSE 9999
WORKDIR /

RUN apk update && \
    apk upgrade && \
    apk --no-cache add ca-certificates mysql-client redis aws-cli

COPY target/guidecore.jar target/version.json* target/do_release.sh* .
COPY target/config/ application-user.yml config/.
COPY target/lib/ lib/.
RUN chmod +x do_release.sh

RUN mkdir log

CMD ["java", "-Xms256m", "-Xmx512m", "-jar", "-Dloader.path=.,config,lib", "guidecore.jar", "--spring.profiles.active=${START_ENVIRONMENT}"]
