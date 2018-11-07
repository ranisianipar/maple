package com.maple;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IdCounterRepository extends MongoRepository<IdCounter, String> {
    @Query("{ '_id' : 1 }")
    public IdCounter findFirst ();
}
