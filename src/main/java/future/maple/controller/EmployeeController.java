package future.maple.controller;

import future.maple.model.Employee;
import future.maple.service.EmployeeService;
import future.maple.web.model.BaseResponse;
import future.maple.web.model.EmployeeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@RestController
public class EmployeeController {

    public List<EmployeeResponse> employeeResponses;
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/employee")
    public BaseResponse<EmployeeResponse> getAllEmployees() {

        employeeResponses = new ArrayList<>();
        for (Employee e: employeeService.getAll()) {
            employeeResponses.add(new EmployeeResponse(e));
        }
        return new BaseResponse<>(employeeResponses);
    }

    @GetMapping("/employee/{username}")
    public BaseResponse<EmployeeResponse> getEmployee(@PathVariable String username) {
        return new BaseResponse<>(new EmployeeResponse(employeeService.get(username)));
    }

    @PostMapping("/employee")
    public BaseResponse<EmployeeResponse> createEmployee(@Valid @RequestBody Employee emp) {
        return new BaseResponse<>(new EmployeeResponse(employeeService.create(emp)));
    }

    @PostMapping("/employee/{username}")
    public BaseResponse<EmployeeResponse> updateEmployee(@PathVariable String username,
                                                         @Valid @RequestBody Employee emp) throws Exception{
        return new BaseResponse<>(new EmployeeResponse(employeeService.update(username, emp)));
    }

    @DeleteMapping("/employee/{username}")
    public BaseResponse<String> removeEmployee(@PathVariable String username) {
        employeeService.delete(username);
        return new BaseResponse<>(username+" has been deleted");
    }

    @DeleteMapping("/employees")
    public BaseResponse<String> removeEmployees() {
        return new BaseResponse<>("All employees has been deleted");
    }
}
