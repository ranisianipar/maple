package com.maple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class EmployeeService {

    //    paging? better with class or just attributes?
    // errorMessage better using Exception? How to transfer the error message to the response?


    @Autowired
    private EmployeeRepository employeeRepository;


    // need pageNumber parameter
    public List<Employee> getAll() {

        // set paging information

        return employeeRepository.findAll();
    }

    public Employee get(String username) throws Exception{
        Employee employee = employeeRepository.findByUsername(username);
        if (employee == null) { throw new NotFoundException('e'); }
        return employee;
    }

    public Employee create(Employee emp) throws DataConstraintException{
        validate(emp, true);
        return employeeRepository.save(emp);
    }

    public Employee update(String username, Employee emp) throws Exception {
        Employee employee = employeeRepository.findByUsername(username);

        if (employee == null) throw new NotFoundException('e');
        employee.setImagePath(emp.imagePath);
        employee.setSuperiorId(emp.superiorId);
        employee.setUsername(emp.username);
        employee.setPassword(emp.password);
        employee.setUpdatedDate(new Date());

        validate(employee, false);

        return employeeRepository.save(employee);
    }

    public void delete(String username) throws Exception{
        Employee employee = employeeRepository.findByUsername(username);
        if (employee == null) { throw new NotFoundException('e'); }
        employeeRepository.delete(employee);
    }

    public void deleteAll() {
        employeeRepository.deleteAll();
    }

    //helper methods

    //NOTE: need improvement
    public void validate(Employee emp, boolean create) throws DataConstraintException{
        List<String> errorMessage = new ArrayList<>();

        if (create && employeeRepository.findByUsername(emp.username) != null) {
            errorMessage.add("username already exist");
        }
        if (create && employeeRepository.findByEmail(emp.email) != null) {
            errorMessage.add("email has been used");
        }
        if (!errorMessage.isEmpty()) throw new DataConstraintException(errorMessage.toString());
    }

}
