package com.maple.Configuration;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.maple")
@Profile("db-test")
public class DatabaseTestConfiguration extends AbstractMongoConfiguration {


    @Override
    @Bean
    public MongoClient mongoClient() {
        return new Fongo("mapledb-test").getMongo();
    }

    @Override
    protected String getDatabaseName() {
        return "mapledb-test";
    }

    @Bean
    public MongoTemplate mongoTemplate() {

        return new MongoTemplate(mongoDbFactory());
    }

    @Bean
    public MongoDbFactory mongoDbFactory() {

        MongoClient mongoClient = new MongoClient("localhost", 27017);
        return new SimpleMongoDbFactory(mongoClient, "mapledb-test");
    }
}
