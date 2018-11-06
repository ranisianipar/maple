package com.maple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class EmployeeService {

    //    paging? better with class or just attributes?
    // errorMessage better using Exception? How to transfer the error message to the response?


    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CounterService counter;


    // need pageNumber parameter
    public List<Employee> getAll() {

        // set paging information

        return employeeRepository.findAll();
    }

    public Employee get(String id) throws NotFoundException {
        Optional<Employee> employee = employeeRepository.findById(id);
        //not working
        if (employee == null) { throw new NotFoundException('e'); }
        return employee.get();
    }
//kenapa counternya selalu mulai dari awal kalo yg id 0 dihapus?
    public Employee create(Employee emp) throws DataConstraintException {
        emp.setId(counter.getNextEmployee());
        validate(emp, true);
        return employeeRepository.save(emp);
    }

    public Employee update(String id, Employee emp) throws NotFoundException, DataConstraintException {
        Optional<Employee> employeeObj = employeeRepository.findById(id);

        if (employeeObj == null) throw new NotFoundException('e');
        Employee employee = employeeObj.get();
        employee.setUsername(emp.getUsername());
        employee.setPassword(emp.getPassword());
        employee.setEmail(emp.getEmail());
        employee.setName(emp.getName());
        employee.setImagePath(emp.getImagePath());
        employee.setSuperiorId(emp.getSuperiorId());
        employee.setUpdatedDate(new Date());

        validate(employee, false);

        return employeeRepository.save(employee);
    }

    public void delete(String id) throws NotFoundException{
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee == null) { throw new NotFoundException('e'); }
        employeeRepository.delete(employee.get());
    }

    public void deleteAll() {
        employeeRepository.deleteAll();
    }

    //helper methods
    public void validate(Employee emp, boolean create) throws DataConstraintException{
        List<String> errorMessage = new ArrayList<>();

        String username_msg = "username already exist";
        String email_msg = "email already exist";
        if (create) {
            if (employeeRepository.findByUsername(emp.getUsername()) != null) errorMessage.add(username_msg);
            if (employeeRepository.findByEmail(emp.getEmail()) != null) errorMessage.add(email_msg);
        }
        // update
        else {
            if (employeeRepository.findByUsername(emp.getUsername())!= null && !employeeRepository.findByUsername(emp.getUsername()).getId().equals(emp.getId()))
                errorMessage.add(username_msg);
            if (employeeRepository.findByEmail(emp.getEmail())!= null && !employeeRepository.findByEmail(emp.getEmail()).getId().equals(emp.getId()))
                errorMessage.add(email_msg);
        }
        if (!errorMessage.isEmpty()) throw new DataConstraintException(errorMessage.toString());
    }

}
