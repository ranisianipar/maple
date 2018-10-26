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
        if (employee == null) {
            throw new NotFoundException('e', 404);
        }
        return employee;
    }

    public Employee create(Employee emp) {
        List<String> errorMessage = validate(emp, true);
        if (errorMessage.isEmpty()) {
            return employeeRepository.save(emp);
        }
        //send the errorMessage to the response
        return null;
    }

    public Employee update(String username, Employee emp) throws Exception {
        Employee employee = employeeRepository.findByUsername(username);

        if (employee == null) throw new NotFoundException('e',400);
        employee.setImagePath(emp.imagePath);
        employee.setSuperiorId(emp.superiorId);
        employee.setUsername(emp.username);
        employee.setPassword(emp.password);
        employee.setUpdatedDate(new Date());

        validate(employee, false);

        return employeeRepository.save(employee);

        // send errorMessage to the response
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

    //NOTE: need improvement
    public List<String> validate(Employee emp, boolean create) {
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
