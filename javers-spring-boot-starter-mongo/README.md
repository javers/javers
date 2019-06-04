### Javer Spring Boot MongoDB starter configuration

##### Using Spring Boot MongoDB starter settings
Spring Boot automatically configures `MongoClient` instance. Javers Spring Boot MongoDB starter will use this
instance automatically.
```yaml
spring:
  data:
    mongodb:
      database: spring-mongo-javers
```
Javers will use this database from the spring-boot-starter-data-mongodb. Please refer to the spring-boot-starter-data-mongodb
[reference documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-nosql.html#boot-features-mongodb) for details on how to configure MongoDB.

##### Javers Spring Boot MongoDB starter settings
Sometimes it maybe necessary to use a different MongoDB instance for persisting Javers data. To use different instance
of MongoDB configure Javers as shown below:
```yaml
javers:
  mongodb:
    host: localhost
    port: 32001
    database: javers-audit
```
`host` and `port` are not mandatory and default to `localhost` and `27017` respectively. Either `database` or
`uri` need to set. The configuration properties also support connecting to an MongoDB instance which requries
authentication.

```yaml
javers:
  mongodb:
    uri: mongodb://localhost:32001/javers-audit
    authentication-database: admin
    username: admin
    password: password
```
_Note: The database name, host and port are derived from the uri._

If greater control is required over how Javers configures the `MongoClient` instance, you can configure 
a `MongoClientOptions` bean with name `javersMongoClientOptions`. If not configured, default client options are used. 
E.g. for enabling SSL and setting socket timeout value.


```java
@Bean("javersMongoClientOptions")
public MongoClientOptions clientOptions() {
    return MongoClientOptions.builder()
    .sslEnabled(true)
    .socketTimeout(1500)
    .build();
}
```
_Note: If spring-boot-starter-data-mongodb is configured using Java config it is necessary to name
the `MongoClientOptions` bean for Javers as `javersMongoClientOptions`._