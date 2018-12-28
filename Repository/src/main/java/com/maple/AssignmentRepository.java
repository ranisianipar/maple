package com.maple;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends MongoRepository<Assignment,String> {

    public List<Assignment> findByStatus(String status, Pageable pageable);
    public List<Assignment> findByEmployeeIdIn(List<String> employeeId, Pageable pageable);
    public List<Assignment> findByEmployeeId(String employeeId, Pageable pageable);
    public List<Assignment> findByEmployeeId(String employeeId);
    public List<Assignment> findByItemSku(String itemSku);
    public List<Assignment> findByEmployeeIdAndStatus(String employeeId, String status);
    public List<Assignment> findByEmployeeIdAndStatus(String employeeId, String status, Pageable pageable);

}
