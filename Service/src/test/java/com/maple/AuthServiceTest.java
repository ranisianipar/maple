package com.maple;


import com.github.fakemongo.Fongo;
import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.FongoJSON;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class AuthServiceTest {

    private Fongo fongo;
    @Before
    public void init() {
        // Fongo instance methods
        fongo = new Fongo("Mongo test");
        // get all created databases (they are created automatically the first time requested)
        Collection<DB> dbs = fongo.getUsedDatabases();
        // also
        List<String> dbNames = fongo.getDatabaseNames();
        // also
        fongo.dropDatabase("dbName");

        // get an instance of the hijacked com.mongodb.Mongo
        Mongo mongo = fongo.getMongo();



    }

    @Test
    public void getValidTokenTest() {
        DB db = fongo.getDB("mapledb");
        DBCollection dbCollection = db.getCollection("employees");
        Employee e = new Employee();
        e.setName("TEST");
        e.setUsername("TEST");
        e.setEmail("TEST@xmail.com");
        e.setPassword("TEST");
        
        dbCollection.insert((DBObject) FongoJSON.parse(
                "{ username: TEST, name: TEST, email: TEST@xmail, password: TEST }"));

        //apa bedanya???
//        new BasicDBObject("nscannedObjects", 4L)
//                .append("nscanned", 2L)
//                .append("n", 1L)
//                .append("timeMicros", 1)
        assertNotNull(db);
    }
}
