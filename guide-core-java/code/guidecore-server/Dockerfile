FROM openjdk:15-jdk-alpine
ENV START_ENVIRONMENT ${START_ENVIRONMENT}
EXPOSE 9999
WORKDIR /
COPY target/guidecore.jar .
COPY target/config/ application-user.yml config/.
COPY target/lib/ lib/.

RUN mkdir log

RUN apk update && \
    apk upgrade && \
    apk --no-cache add ca-certificates mysql-client redis aws-cli && \ 
    rm -rf /var/cache/apk/*

CMD ["java", "-Xms256m", "-Xmx512m", "-jar", "-Dloader.path=.,config,lib", "guidecore.jar", "--spring.profiles.active=${START_ENVIRONMENT}"]
