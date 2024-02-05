# arena-hub-backend
#### Description
{**When you're done, you can delete the content in this README and update the file with details for others getting started with your repository**}

#### Software Architecture
Software architecture description

#### Installation

1. This application requires the following components:
   - maven
   - openjdk 15.0
   - redis 6.2
   - mysql 5.7
  
2. For running on docker containers, install the followings:

    On MacOS:
    ```bash
    brew update
    brew install maven finch
    ```
    {**in case of issues with finch use docker-compose instead**}

#### Instructions
1. Clone this repository
2. Get `dump.sql` and `application-user.yml` from s3 bucket
    ```bash
    aws s3 cp application-user.yml s3://powtoon-dev-develop-static/arena-hub-be-db-dump
    aws s3 cp dump.sql.tar.gz s3://powtoon-dev-develop-static/arena-hub-be-db-dump
    unzip dump.sql.tar.gz
    ```
3. Copy `dump.sql`:
    ```bash
    cd arena-hub-backend/
    cp your_location/dump.sql .
    ```
    Copy `application-user.yml`:
    ````bash
    cp application-user.yml arena-hub-backend/guide-core-java/code/guidecore-server/
    ```
4. Run maven:
    ```bash
    cd guide-core-java/code/guidecore-server
    mvn -Dmaven.compiler.source=15 -Dmaven.compiler.target=15 clean package -Dmaven.test.skip=true
    ```
5. Build docker image:
    ```bash
    docker build --platform linux/amd64 -t arena-be-java:2.1 .
    ```
6. Go to `arena-hub-backend` and run:
    ```bash
    finch compose up # in case of issues run docker-compose up
    ```
7. The expected response on: localhost:9999/arena-hub/api/v1/guidecore/

    ```json
    {"meta":{"msg":"没有TOKEN","code":401,"success":false,"systemTime":"2024-02-05 07:31:36","timestamp":1707118296666}}
    ```

{**add a new section in case we decide to run backend locally without not using docker image**}
      
#### Contribution

1.  Clone the repository
2.  Create feat_xxx branch
3.  Commit your code
4.  Create Pull Request
