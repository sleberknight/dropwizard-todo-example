package com.fortitudetec.example.todo.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortitudetec.example.todo.TodoConfiguration;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.validation.valuehandling.OptionalValidatedValueUnwrapper;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.io.IOException;

@Configuration
public class TestSpringConfiguration {

    @Bean
    public TodoConfiguration todoConfiguration() throws IOException, ConfigurationException {

        Validator validator = Validation.byProvider(HibernateValidator.class)
                .configure()
                .addValidatedValueHandler(new OptionalValidatedValueUnwrapper())
                .buildValidatorFactory()
                .getValidator();

        ObjectMapper objectMapper = Jackson.newObjectMapper();

        ConfigurationFactory<TodoConfiguration> factory =
                new ConfigurationFactory<>(TodoConfiguration.class, validator, objectMapper, "dw");

        return factory.build(new File("config.yml"));
    }

    @Bean
    @Autowired
    public DataSource dataSource(TodoConfiguration configuration) {
        DataSourceFactory dataSourceFactory = configuration.getDataSourceFactory();

        return new SingleConnectionDataSource(
                dataSourceFactory.getUrl(),
                dataSourceFactory.getUser(),
                dataSourceFactory.getPassword(),
                true
        );
    }
}
