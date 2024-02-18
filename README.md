# arena-hub-backend
Visual Native / Powtoon Backend Java

#### Software Architecture
Software architecture description

#### Installation

1. This application requires the following components:

    * maven
    * openjdk 15.0
    * redis 6.2
    * mysql 5.7/mariadb 10.2
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

#### Instruction to run the project locally
1. Go to development dir
    ```bash
    cd development
    ```
2. Pull the latest sql dump
    ```bash
    make pull_sql_dump

    ```
3. Run mysql/redis/pdp containers with docker-compose/finch
    ```bash
    finch compose up
    ```
    This will use `docker-compose.yaml` to run mysql/redis/pdp
4. Build the project:
    ```bash
    make build
    ```
    As a result guide-core-java/code/guidecore-server/target folder should be created

5. Run the project:
    ```bash
    make run
    ```
6. Test the application -> `http://localhost:9999/arena-hub/api/v1/guidecore/`
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
    Remove all docker containers with all data(including mysql DB):
    ```bash
    finch compose down -v --remove-orphans
    ```
{**add a new section in case we decide to run backend locally not using docker image**}
      
#### Contribution

1.  Clone the repository
2.  Create feat_xxx branch
3.  Commit your code
4.  Create Pull Request
