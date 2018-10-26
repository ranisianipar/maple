package com.maple;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdCounterRepository extends MongoRepository<IdCounter, String> {
}
