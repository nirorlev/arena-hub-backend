# arena-hub-backend
Visual Native / Powtoon Backend Java

#### Software Architecture
Software architecture description

#### Installation

1. This application requires the following components:

    * maven
    * openjdk 15.0
    * redis 6.2
    * mysql 5.7
    * pdp-v2
  
2. For running on docker containers, install the followings:

    On MacOS:
    ```bash
    brew update
    brew install maven finch
    # Check the status of the Finch virtual machine
    finch vm status
    ```
    {**in case of issues with finch use docker-compose instead**}

#### Instructions
1. Clone this repository
2. Get `dump.sql` zipped file from s3 bucket
    ```bash
    wget https://powtoon-dev-develop-static.s3.amazonaws.com/arena-hub-be-db-dump/dump.sql.tar.gz
    unzip dump.sql.tar.gz
    ```
3. Copy `dump.sql` to the project root directory: `arena-hub-backend`:
    ```bash
    cd arena-hub-backend/
    cp your_location/dump.sql .
    ```
4. Run maven:
    ```bash
    cd guide-core-java/code/guidecore-server
    mvn -Dmaven.compiler.source=15 -Dmaven.compiler.target=15 clean package -Dmaven.test.skip=true
    ```
    As a result `target` folder should be created
5. Build docker image in `guide-core-java/code/guidecore-server` directory:
    ```bash
    finch build --platform linux/amd64 --tag arena-be-java:2.1 .
    # or use docker command: docker build --platform linux/amd64 -t arena-be-java:2.1 .
    ```
6. Go to the project root directory: `arena-hub-backend` and run:
    ```bash
    finch compose up # in case of issues run docker-compose up
    ```
7.  Test the application: localhost:9999/arena-hub/api/v1/guidecore/ 

    The expected response:
    ```json
    {"meta":{"msg":"没有TOKEN","code":401,"success":false,"systemTime":"2024-02-05 07:31:36","timestamp":1707118296666}}
    ```
### Things To Know
1. If `finch` encounters issues, Docker desktop or alternative tool can be used. Install docker and docker-compose, and use the appropriate commands.
2. Keep properly stop containers:
    ```bash
    finch compose down
    ```
3. Once the above setup was installed and runs, be aware of loosing data in case the container dies, the data can be copied to local machine with this command:
    ```bash
    finch cp <container_id_or_name>:<path_inside_container> <local_destination_path>
    ```
4. Useful commands:
    Run java container in interactive mode:
    ```bash
    finch exec -it arena-java-app /bin/sh
    ```    
    Run mysql container in interactive mode:
    ```bash
    finch exec -it mysql-db /bin/sh
    # inside container
    mysql -h $MYSQL_HOST -u $MYSQL_USER -D $MYSQL_DATABASE -p$MYSQL_PASSWORD
    ```
5. The java image tag is hard-coded in docker-compose and it's 2.1 
    In case of the image rebuild, the latest image will be taken even if you didn't retag it. So before the image rebuild stop the finch-compose, rebuild the image and start compose.
6. In case a new database should be uploaded, shutdown the compose, download the new dump file from the bucket (see step 2) and replace the old `dump.sql` in the project  root directory. Run compose up, it should initialize and load a new database.

{**add a new section in case we decide to run backend locally not using docker image**}
      
#### Contribution

1.  Clone the repository
2.  Create feat_xxx branch
3.  Commit your code
4.  Create Pull Request
