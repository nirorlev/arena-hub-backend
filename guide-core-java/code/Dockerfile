FROM openjdk:15-jdk-alpine
ENV START_ENVIRONMENT ${START_ENVIRONMENT}
EXPOSE 9999
WORKDIR /

RUN apk update && \
    apk upgrade && \
    apk --no-cache add ca-certificates maven mysql-client redis aws-cli && \
    rm -rf /var/cache/apk/*

COPY ./guidecore-migration/target ./target
COPY ./guidecore-migration/db ./db
COPY ./guidecore-migration/pom.xml .

COPY ./guidecore-server/target/guidecore.jar ./guidecore-server/target/version.json* ./guidecore-server/target/do_release.sh* ./
COPY ./guidecore-server/target/config/ config/.
COPY ./guidecore-server/target/lib/ lib/.

RUN chmod +x do_release.sh
RUN mkdir log

CMD ["java", "-Xms256m", "-Xmx512m", "-jar", "-Dloader.path=.,config,lib", "guidecore.jar", "--spring.profiles.active=${START_ENVIRONMENT}"]