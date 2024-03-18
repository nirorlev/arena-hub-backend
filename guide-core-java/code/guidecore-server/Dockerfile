FROM openjdk:15-jdk-alpine
ENV START_ENVIRONMENT ${START_ENVIRONMENT}
EXPOSE 9999
WORKDIR /

RUN apk update && \
    apk upgrade && \
    apk --no-cache add ca-certificates maven mysql-client redis aws-cli && \
    rm -rf /var/cache/apk/*

COPY target/guidecore.jar target/version.json* target/do_release.sh* pom.xml .
COPY target/config/ config/.
COPY target/lib/ lib/.
RUN chmod +x do_release.sh

RUN mkdir log

CMD ["java", "-Xms256m", "-Xmx512m", "-jar", "-Dloader.path=.,config,lib", "guidecore.jar", "--spring.profiles.active=${START_ENVIRONMENT}"]