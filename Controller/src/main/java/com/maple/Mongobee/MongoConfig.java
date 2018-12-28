package com.maple.Mongobee;

import com.github.mongobee.Mongobee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Value( "${spring.data.mongodb.uri}" )
    private String mongodbUrl;
    @Value( "${spring.data.mongodb.database}" )
    private String databaseName;


    @Bean
    public Mongobee mongobee(){
        Mongobee runner = new Mongobee(mongodbUrl);
        runner.setDbName(databaseName); // host must be set if not set in URI
        runner.setChangeLogsScanPackage("com.maple"); // the package to be scanned for changesets

        return runner;
    }
}
