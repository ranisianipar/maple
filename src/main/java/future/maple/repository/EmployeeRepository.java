package future.maple.repository;

import future.maple.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {

    public Employee findByUsername(String username);
    public Employee findByEmail(String email);
}
