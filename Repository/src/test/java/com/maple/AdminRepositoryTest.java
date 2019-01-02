package com.maple;

import com.maple.Configuration.DatabaseTestConfiguration;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@ActiveProfiles({"db-test"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DatabaseTestConfiguration.class)
@EnableMongoRepositories(basePackageClasses = AdminRepository.class)
public class AdminRepositoryTest {

//    @Rule
//    public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb("mongodb-test");

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AdminRepository adminRepository;
}
