FROM maven:latest

ENV START_ENVIRONMENT user

EXPOSE 80

WORKDIR /

COPY . .
RUN ["mvn", "-Dmaven.compiler.source=15", "-Dmaven.compiler.target=15", "clean", "install", "package", "-Dmaven.test.skip=true"]

RUN apt-get update && apt-get install -y mysql-client redis

CMD ["java", "-Xms256m", "-Xmx512m", "-jar", "-Dloader.path=.,config,lib", "target/guidecore.jar", "--spring.profiles.active=user"]