package com.maple;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

@Configuration
@EnableMongoRepositories
@ComponentScan(basePackageClasses = {EmployeeRepository.class})  // modified to not load configs from com.johnathanmarksmith.mongodb.example.MongoConfiguration
@PropertySource("classpath:application.properties")
public class EmployeeRepositoryTestConfig extends AbstractMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "mapledb-test";
    }

    @Override
    public MongoClient mongoClient() {
        // uses fongo for in-memory tests
        return new Fongo("mongo-test").getMongo();
    }

    @Override
    protected Collection<String> getMappingBasePackages() {
        Collection collection = new AbstractCollection() {
            @Override
            public Iterator iterator() {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }
        };
        collection.add("com.maple");
        return collection;
    }

}
