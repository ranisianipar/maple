package com.maple;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@Configuration
public class UnitTestApplicationConfig extends AbstractMongoConfiguration {
    @Autowired
    private Environment env;

//    @Mock
//    private EmployeeRepository employeeRepository;

    @Override
    protected String getDatabaseName() {
        return env.getRequiredProperty("spring.data.mongodb.database");
    }

    @Override
    public MongoClient mongoClient() {
        return new Fongo(getDatabaseName()).getMongo();
    }

}
