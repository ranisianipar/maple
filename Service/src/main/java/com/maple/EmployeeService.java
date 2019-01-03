package com.maple;

import com.maple.Exception.DataConstraintException;
import com.maple.Exception.MapleException;
import com.maple.Exception.MethodNotAllowedException;
import com.maple.Exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static com.maple.Helper.SimpleUtils.*;


@Service
public class EmployeeService {

    final String EMPLOYEE = "Employee";

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private CounterService counter;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);


    public Page<Employee> getAll(String search, Pageable pageable) {
        log.info("Get all employee by keyword of username and page criterion");
        if (search != null) return employeeRepository.findByUsernameLike(search, pageable);
        return employeeRepository.findAll(pageable);
    }

    public Employee get(String id) throws NotFoundException {
        log.info("Get employee with ID: "+id);
        Optional<Employee> employee = employeeRepository.findById(id);
        if (!employee.isPresent()) { throw new NotFoundException(EMPLOYEE); }
        return employee.get();
    }

    public long getTotalEmployee() {
        log.info("Get total employee");
        return getTotalObject(employeeRepository);
    }

    public long getTotalPage(long size)
    {
        log.info("Get total pages");
        return getTotalPages(size, getTotalEmployee());
    }

    public Employee create(Employee emp, MultipartFile file) throws DataConstraintException, IOException {
        log.info("Create employee with employee data\n"+ emp.toString());
        emp.setId(counter.getNextEmployee());
        checkDataValue(emp, true);
        if (file != null)
            emp.setImagePath(storeFile(Constant.FOLDER_PATH_EMPLOYEE, file, emp.getId()));;

        return employeeRepository.save(emp);
    }

    public Employee update(String id, Employee emp, MultipartFile file, String token)
            throws MapleException, IOException {
        log.info("Update employee with employee data\n"+emp);
        Optional<Employee> employeeObj = employeeRepository.findById(id);

        if (!employeeObj.isPresent()) throw new NotFoundException(EMPLOYEE);
        Employee employee = employeeObj.get();
        employee.setUsername(emp.getUsername());
        employee.setPassword(emp.getPassword());
        employee.setEmail(emp.getEmail());
        employee.setName(emp.getName());
        employee.setPhone(emp.getPhone());

        //kalo dia berniat ngapus gambar, brarti dia harus imagePathnya di null in dari request
        if (employee.getImagePath() == null) {
            deleteFile(employee.getImagePath());
            employee.setImagePath(null);
        }
        // user replace/add picture
        if (file != null) {
            employee.setImagePath(storeFile(Constant.FOLDER_PATH_EMPLOYEE, file, employee.getId()));
        }
        // for admin
        if (adminService.isExist(getCurrentUserId(token))) employee.setSuperiorId(emp.getSuperiorId());
        employee.setUpdatedDate(new Date());
        checkDataValue(employee, false);
        return employeeRepository.save(employee);
    }

    public  List<Employee> getBySuperiorId(String id) {
        log.info("Get employee that has superior id: "+id);
        return employeeRepository.findBySuperiorId(id);
    }

    public void deleteMany(List<String> ids) throws MapleException {
        log.info("Admin delete employee by ID in "+ids.toString());
        try {
            // delete image
            Optional<Employee> employeeOptional;
            List<Employee> employeeAsSuperior;
            for (String id : ids) {
                employeeOptional = employeeRepository.findById(id);
                if (employeeOptional.isPresent())
                    deleteFile(employeeOptional.get().getImagePath());
                    //update others superiorId if their superior is deleted
                    employeeAsSuperior = employeeRepository.findBySuperiorId(employeeOptional.get().getId());
                    if (!employeeAsSuperior.isEmpty()) {
                        for (Employee employee: employeeAsSuperior) {
                            employee.setSuperiorId(null);
                            employeeRepository.save(employee);
                        }
                    }
            }
            employeeRepository.deleteByIdIn(ids);
            assignmentService.updateByEmployee(ids);
        } catch (Exception e) {
            throw new MapleException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // non functional
    public boolean isExist(String id) {
        log.info("Check does the employee ID exist or not, ID: "+id);
        return employeeRepository.findById(id).isPresent();
    }
    public Employee getEmployeeByUsername(String username) {
        log.info("Get employee by username: "+username);
        return employeeRepository.findByUsername(username);
    }

    public void onlyEmployee(String method, String token) throws MapleException {
        if (get(getCurrentUserId(token)) == null)
            throw new MethodNotAllowedException(method);
    }

    public void onlyTheirSuperior(String employeeId, String userId) throws MapleException {
        Employee employee = get(employeeId);
        if (employee.getSuperiorId() == null && !adminService.isExist(userId))
            throw new MethodNotAllowedException("Only their superior");
        else if (employee.getSuperiorId() != null && !employee.getSuperiorId().equals(userId))
            throw new MethodNotAllowedException("Only their superior");
    }

    // Attribute value validation
    private void checkDataValue(Employee emp, boolean create) throws DataConstraintException{
        List errorMessage = new ArrayList();
        errorMessage = validateAttributeValue(emp, regexChecker(emp, errorMessage));
        uniquenessChecker(emp, create, errorMessage);
        if (!errorMessage.isEmpty()) throw new DataConstraintException(errorMessage.toString());
    }

    //helper methods to check uniqueness data attribute
    private void uniquenessChecker(Employee emp, boolean create, List errorMessage){
        String username_msg = "username already exist";
        String email_msg = "email already exist";
        String superior_msg = "superior doesn't exist";
        String self_superior_msg = "employee id cant be same with superior id";

        if (emp.getSuperiorId() != null) {
            Optional<Employee> superior = employeeRepository.findById(emp.getSuperiorId());
            if (!superior.isPresent()) errorMessage.add(superior_msg);
            else if (superior.get().getId().equals(emp.getId())) errorMessage.add(self_superior_msg);
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

}
