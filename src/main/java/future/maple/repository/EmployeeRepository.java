package future.maple.repository;

import future.maple.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface EmployeeRepository extends MongoRepository<Employee, String> {

    public Employee findByUsername(String username);
    public List<Employee> findBySuperior(String superiorId);
}
