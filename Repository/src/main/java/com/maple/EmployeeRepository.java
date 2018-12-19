package com.maple;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {

    public Employee findByUsername(String username);
    public Employee findByEmail(String email);
    public List<Employee> findBySuperiorId(String superiorId);
    public Page<Employee> findAll(Pageable pageable);
    public void deleteByIdIn(List<String> ids);
}
