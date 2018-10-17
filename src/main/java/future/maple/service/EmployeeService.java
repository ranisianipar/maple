package future.maple.service;

import future.maple.model.Employee;
import future.maple.repository.EmployeeRepository;
import future.maple.repository.IdCounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class EmployeeService {

    //    paging? better with class or just attributes?
    // errorMessage better using Exception? How to transfer the error message to the response?

    @Autowired
    private IdCounterRepository counterRepository;

    @Autowired
    private EmployeeRepository employeeRepository;


    // need pageNumber parameter
    public List<Employee> getAll() {

        // set paging information

        return employeeRepository.findAll();
    }

    public Employee get(String username) throws Exception{
        Employee employee = employeeRepository.findByUsername(username);
        if (employee == null) {
            throw new SimpleException("employee not found", 404);
        }
        return employee;
    }

    public Employee create(Employee emp) {
        List<String> errorMessage = dataValidation(emp, true);
        if (errorMessage.isEmpty()) {
            return employeeRepository.save(emp);
        }
        //send the errorMessage to the response
        return null;
    }

    public Employee update(String username, Employee emp) throws Exception {
        /* NOTE: Made null handler for not found condition */
        Employee employee = employeeRepository.findByUsername(username);

        employee.setEmployeeImage(emp.imagePath);
        employee.setSuperiorId(emp.superiorId);
        employee.setUsername(emp.username);
        employee.setPassword(emp.password);
        employee.setUpdatedDate();

        List<String> errorMessage = dataValidation(employee, false);
        if (errorMessage.isEmpty()) {
            return employeeRepository.save(employee);
        }
        // send errorMessage to the response
        throw new Exception(errorMessage.toString());
    }

    public boolean delete(String username) {
        Employee employee = employeeRepository.findByUsername(username);
        if (employee == null) {
            return false;
        }
        employeeRepository.delete(employee);
        return true;
    }

    public void deleteAll() {
        employeeRepository.deleteAll();
    }

    //helper methods

    public List<String> dataValidation(Employee emp, boolean create) {
        List<String> errorMessage = new ArrayList<>();

        if (create && employeeRepository.findByUsername(emp.username) != null) {
            errorMessage.add("username already exist");
        }
        if (create && employeeRepository.findByEmail(emp.email) != null) {
            errorMessage.add("email has been used");
        }
        return errorMessage;
    }

}
