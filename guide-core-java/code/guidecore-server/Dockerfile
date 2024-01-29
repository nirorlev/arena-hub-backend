FROM maven:latest

ENV START_ENVIRONMENT user

EXPOSE 80

WORKDIR /

COPY . .

RUN apt-get update && apt-get install -y mysql-client redis groff unzip

RUN curl --silent --show-error --fail "https://awscli.amazonaws.com/awscli-exe-linux-x86_64-2.0.30.zip" -o "awscliv2.zip"
RUN unzip awscliv2.zip
RUN ./aws/install

RUN ["mvn", "-Dmaven.compiler.source=15", "-Dmaven.compiler.target=15", "clean", "install", "package", "-Dmaven.test.skip=true"]

CMD ["java", "-Xms256m", "-Xmx512m", "-jar", "-Dloader.path=.,config,lib", "target/guidecore.jar", "--spring.profiles.active=user"]