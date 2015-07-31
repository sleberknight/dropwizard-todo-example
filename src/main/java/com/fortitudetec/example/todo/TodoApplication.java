package com.fortitudetec.example.todo;

import com.fortitudetec.example.todo.dao.TodoDao;
import com.fortitudetec.example.todo.resource.TodoResource;
import com.fortitudetec.spring.context.SpringContextBuilder;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.hibernate.SessionFactoryHealthCheck;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;

public class TodoApplication extends Application<TodoConfiguration> {

    public static void main(String[] args) throws Exception {
        new TodoApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<TodoConfiguration> bootstrap) {
        bootstrap.addBundle(new MigrationsBundle<TodoConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(TodoConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(TodoConfiguration configuration, Environment environment) throws Exception {

        DataSourceFactory dataSourceFactory = configuration.getDataSourceFactory();
        ManagedDataSource dataSource = dataSourceFactory.build(environment.metrics(), "dataSource");

        ApplicationContext context = new SpringContextBuilder()
                .addParentContextBean("dataSource", dataSource)
                .addParentContextBean("configuration", configuration)
                .addAnnotationConfiguration(TodoSpringConfiguration.class)
                .build();

        registerResources(environment, context);
        registerHealthChecks(environment, configuration, context);
    }

    private void registerResources(Environment environment, ApplicationContext context) {

        TodoDao todoDao = context.getBean(TodoDao.class);
        TodoResource todoResource = new TodoResource(todoDao);
        environment.jersey().register(todoResource);
    }

    private void registerHealthChecks(Environment environment,
                                      TodoConfiguration configuration,
                                      ApplicationContext context) {

        SessionFactory sessionFactory = context.getBean(SessionFactory.class);
        SessionFactoryHealthCheck sessionFactoryHealthCheck =
                new SessionFactoryHealthCheck(sessionFactory,
                        configuration.getDataSourceFactory().getValidationQuery());
        environment.healthChecks().register("sessionFactoryHealthCheck", sessionFactoryHealthCheck);
    }
}
