package com.maple;


import com.github.fakemongo.Fongo;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import java.util.Collection;
import java.util.List;

public class AuthServiceTest {

    @BeforeAll
    public static void init() {
        // Fongo instance methods
        Fongo fongo = new Fongo("Mongo test");
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
        
    }
}
