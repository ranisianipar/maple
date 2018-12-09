package com.maple;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends MongoRepository<Assignment,String> {

    public List<Assignment> findByEmployeeId(String employeeId);
    public List<Assignment> findByItemSku(String itemSku);

}
