package com.maple;

import com.maple.Exception.DataConstraintException;
import com.maple.Exception.MapleException;
import com.maple.Exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;


@Service
public class EmployeeService {

    final String EMPLOYEE = "Employee";
    final String UPLOADED_FOLDER = "C:\\Users\\user\\Documents\\future\\maple_uploaded\\Employee";

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private CounterService counter;

    private List errorMessage = new ArrayList();

    public Page<Employee> getAll(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    public Employee get(String id) throws NotFoundException {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (!employee.isPresent()) { throw new NotFoundException(EMPLOYEE); }
        return employee.get();
    }

    public long getTotalEmployee() {return SimpleUtils.getTotalObject(employeeRepository);}

    public long getTotalPage(long size) {
        return SimpleUtils.getTotalPages(size, getTotalEmployee());
    }

    public Employee create(Employee emp, MultipartFile file) throws DataConstraintException, IOException {
        checkDataValue(emp, true);
        emp.setId(counter.getNextEmployee());
        if (file != null)
            emp.setImagePath(SimpleUtils.storeFile(UPLOADED_FOLDER, file, emp.getId()));;

        return employeeRepository.save(emp);
    }

    public Employee update(String id, Employee emp, MultipartFile file) throws NotFoundException, DataConstraintException, IOException {
        Optional<Employee> employeeObj = employeeRepository.findById(id);

        if (!employeeObj.isPresent()) throw new NotFoundException(EMPLOYEE);
        Employee employee = employeeObj.get();
        employee.setUsername(emp.getUsername());
        employee.setPassword(emp.getPassword());
        employee.setEmail(emp.getEmail());
        employee.setName(emp.getName());

        //kalo dia berniat ngapus gambar, brarti dia harus imagePathnya di null in dari request
        if (employee.getImagePath() == null) {
            SimpleUtils.deleteFile(employee.getImagePath());
            employee.setImagePath(null);
        }
        // user replace/add picture
        if (file != null) {
            employee.setImagePath(SimpleUtils.storeFile(UPLOADED_FOLDER, file, employee.getId()));
        }
        employee.setSuperiorId(emp.getSuperiorId());
        employee.setUpdatedDate(new Date());
        checkDataValue(employee, false);
        return employeeRepository.save(employee);
    }

    public void deleteMany(DeleteRequest deleteRequest) throws MapleException {
        try {
            // delete image
            Optional<Employee> employeeOptional;
            for (String id : deleteRequest.getIds()) {
                employeeOptional = employeeRepository.findById(id);
                if (employeeOptional.isPresent())
                    SimpleUtils.deleteFile(employeeOptional.get().getImagePath());
            }
            employeeRepository.deleteByIdIn(deleteRequest.getIds());
            assignmentService.deleteByEmployee(deleteRequest.getIds());
        } catch (Exception e) {
            throw new MapleException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public void deleteAll() {
        employeeRepository.deleteAll();
    }

    // non functional
    public boolean isExist(String id) {
        return employeeRepository.findById(id).isPresent();
    }
    public Employee authenticate(String username, String password) {
        Employee employee = employeeRepository.findByUsername(username);
        if (employee == null) return null;
        if (!employee.getPassword().equals(password)) return null;
        return employee;
    }

    // Attribute value validation
    private void checkDataValue(Employee emp, boolean create) throws DataConstraintException{
        validateAttributeValue(emp);
        uniquenessChecker(emp, create);
        //regexChecker(emp);
        if (!errorMessage.isEmpty()) throw new DataConstraintException(errorMessage.toString());
    }

    //helper methods to check uniqueness data attribute
    private void uniquenessChecker(Employee emp, boolean create){
        List<String> errorMessage = new ArrayList<>();

        String username_msg = "username already exist";
        String email_msg = "email already exist";
        String superior_msg = "superior doesn't exist";

        if (emp.getSuperiorId() != null) {
            Optional<Employee> superior = employeeRepository.findById(emp.getSuperiorId());
            if (!superior.isPresent() && !emp.equals(superior)) errorMessage.add(superior_msg);
        }

        if (create) {
            if (employeeRepository.findByUsername(emp.getUsername()) != null) errorMessage.add(username_msg);
            if (employeeRepository.findByEmail(emp.getEmail()) != null) errorMessage.add(email_msg);
        }
        // update
        else {
            if (employeeRepository.findByUsername(emp.getUsername()) != null &&
                    !employeeRepository.findByUsername(emp.getUsername()).getId().equals(emp.getId()))
                errorMessage.add(username_msg);
            if (employeeRepository.findByEmail(emp.getEmail()) != null &&
                    !employeeRepository.findByEmail(emp.getEmail()).getId().equals(emp.getId()))
                errorMessage.add(email_msg);
        }
    }

    private void validateAttributeValue(Employee employee) {
        String null_warning = "cant be null";

        if (employee.getUsername() ==  null) errorMessage.add("Username "+null_warning);
        if (employee.getPassword() == null) errorMessage.add("Password "+null_warning);
        if (employee.getName() == null) errorMessage.add("Name "+null_warning);
        if (employee.getEmail() ==  null) errorMessage.add("Email "+null_warning);
    }

    //to make sure the data attribute value is appropriate
    private void regexChecker (Employee emp){
        String phone_msg = "Phone number invalid";
        String email_msg = "Email is unvalid, should contain '@'";

        //regex for phone number consist of number;
        Pattern phoneNumberPattern = Pattern.compile(".*[^0-9].*");
        Pattern emailPattern = Pattern.compile(".*@.*");

        // phone number checker
        if (!phoneNumberPattern.matcher(emp.getPhone()).matches()) errorMessage.add(phone_msg);
        if (!emailPattern.matcher(emp.getEmail()).matches()) errorMessage.add(email_msg);
    }

}
