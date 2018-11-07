package com.maple;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {

    @Query("{ 'username' : ?0 }")
    public Employee findByUsername(String username);

    @Query("{ 'email' : ?0 }")
    public Employee findByEmail(String email);
}
