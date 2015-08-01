# Dropwizard Todo Example

An example "todo" Dropwizard application that uses `SpringContextBuilder` to create a Spring ApplicationContext.
In this sample application, instead of using the default Dropwizard Hibernate support, we are using the
`SpringContextBuilder` to create a Spring application context and drive transactions via Spring.

The main reasons you might want to do this are:

* you already have a Spring-driven backend and want to use Dropwizard
* you don't want sessions and transactions wrapped around an entire Jersey resource method (which is what will
 happen if you use the Dropwizard Hibernate-specific `@UnitOfWork` annotation)
* you aren't using Hibernate, in which case there is no `@UnitOfWork` annotation for things like plain JDBC, JDBI,
Spring JDBC, and so on and you want to use Spring to drive transactions.

To use `SpringContextBuilder` you'll first need to install the JAR in your Maven repository. To do that you will need to
clone the repository at https://github.com/sleberknight/spring-appcontext-builder and then `mvn install` it.
Then the following dependency declared in the POM file will be resolved properly.

```xml
<dependency>
    <groupId>com.fortitudetec</groupId>
    <artifactId>spring-appcontext-builder</artifactId>
    <version>0.1.0</version>
</dependency>
```

In the `TodoApplication` class in the `run()` method, we are creating a new application context and adding the
Dropwizard configuration object and `ManagedDataSource` as parent beans. These parent beans are then accessible
in the Spring application context, whether you are using Java Config or XML configuration.

```java
ApplicationContext context = new SpringContextBuilder()
        .addParentContextBean("dataSource", dataSource)
        .addParentContextBean("configuration", configuration)
        .addAnnotationConfiguration(TodoSpringConfiguration.class)
        .build();
```

Then in the `TodoSpringConfiguration` class we autowire these parent beans in and use the data source to construct the
Hibernate session factory.

```java
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackageClasses = TodoDao.class)
public class TodoSpringConfiguration {

    @Autowired
    private DataSource _dataSource;

    @Autowired
    private TodoConfiguration _configuration;

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        // ...
    }

    // ...
}
```

Once you have the Spring application context, you can simply get the beans you need (e.g. DAOs) directly from the
context and manually inject them into your Jersey resource classes. For example:

```java
TodoDao todoDao = context.getBean(TodoDao.class);
TodoResource todoResource = new TodoResource(todoDao);
environment.jersey().register(todoResource);
```

Manually extracting beans from a Spring context might be frowned upon by some people, but if the main reason Spring is
being used is for things like transaction management in DAOs or service classes, then there really isn't any reason to
make things more complicated. The Jersey resource class doesn't know anything about Spring; it simply knows that it
needs another object to do its work and that's it. This also makes it much easier to test the resource class by mocking
the dependency.

To run this example, you'll need to run an H2 server (see http://www.h2database.com/html/tutorial.html#using_server if
you are not familiar with how to do this). Once the H2 server is running, you'll need to run the database migration
to create a `todos` table. The simplest way is to do a `mvn package` to create a shaded-JAR file and then run the
Dropwizard `db migrate` command.

If your local Maven repository is in your home directory in the default location, then the following command will
start the H2 server:

```
$ java -cp ~/.m2/repository/com/h2database/h2/1.4.187/h2-1.4.187.jar org.h2.tools.Server
```

Next package the application into a JAR:

```
$ mvn package
```

Once the database is migrated, you can you can run the `TodoApplication` class
directly, either directly in an IDE like IntelliJ or via the command line. First do the migration:

```
$ java -jar target/dropwizard-todo-example-0.1.0.jar db migrate config.yml
```

and then run the server:

```
$ java -jar target/dropwizard-todo-example-0.1.0.jar server config.yml
```

Since the application class in a Dropwizard application has a `main()` method, it is really simple to start and stop
the application during development right from an IDE instead of needing to package into a JAR first. See the
Dropwizard [getting started](http://www.dropwizard.io/getting-started.html) or
[user manual](http://www.dropwizard.io/manual/index.html) for more information.

Finally, use any REST client (e.g. the Postman Chrome app) to hit the endpoints.
