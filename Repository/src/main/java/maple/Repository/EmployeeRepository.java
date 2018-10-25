package maple.Repository;

import maple.Model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository; //cari modul nya
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {

    public Employee findByUsername(String username);
    public Employee findByEmail(String email);
}
