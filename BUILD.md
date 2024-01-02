# Build JLegMed from scratch

## Build with Integration Tests 

### Dependencies for integration tests

For running integration tests, we recommend using local docker containers to provide the following infrastructure:

*   An ActiveMQ instance with default settings: See [here](https://hub.docker.com/r/rmohr/activemq/).
*   A PostgresDB database with default settings. Default user/password should be admin/admin: See [here](https://hub.docker.com/_/postgres).

You can also use the docker stack provided [here](https://github.com/jexxa-projects/Jexxa/blob/master/jexxa-core/src/test/resources/DeveloperStack.yaml)

Check the status of the running containers:

```docker
docker ps  -f status=running --format "{{.Names}}" 
```    

Output should look as follows

```docker
...
Postgres
activemq
...
```
### Build JLegMed
To build JLegMed with integration tests call:

```maven
mvn clean install 
```  

## Build JLegMed without Integration Tests
In case you would like to compile JLegMed by yourself without an integration tests call:

```maven
mvn clean install -DskipITs
```  

