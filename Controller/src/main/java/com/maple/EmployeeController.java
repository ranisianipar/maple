package com.maple;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@RestController
public class EmployeeController {

    final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    public List<EmployeeResponse> employeeResponses;
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/employee")
    public BaseResponse<EmployeeResponse> getAllEmployees(){

        employeeResponses = new ArrayList<>();
        EmployeeResponse er;
        mapperFactory.classMap(Employee.class, EmployeeResponse.class)
                .byDefault().exclude("password").register();
        MapperFacade mapper = mapperFactory.getMapperFacade();
        for (Employee e: employeeService.getAll()) {
            er = mapper.map(e, EmployeeResponse.class);
            employeeResponses.add(er);
        }
        return new BaseResponse<>(employeeResponses);
    }

    @GetMapping("/employee/{username}")
    public BaseResponse<EmployeeResponse> getEmployee(@PathVariable String username) {
        BaseResponse<EmployeeResponse> br = new BaseResponse<EmployeeResponse>();
        mapperFactory.classMap(Employee.class, EmployeeResponse.class)
                .byDefault().exclude("password").register();
        MapperFacade mapper = mapperFactory.getMapperFacade();
        try {
            br.setValue(mapper.map(employeeService.get(username), EmployeeResponse.class));
            br.setCode(200);
            br.setSuccess(true);
        } catch (NotFoundException e) {
            br.setCode(500);
            br.setErrorMessage(e.getMessage());
            br.setErrorCode(e.getCode());
        } finally {
            return br;
        }

    }

    @PostMapping("/employee")
    public BaseResponse<EmployeeResponse> createEmployee(@Valid @RequestBody Employee emp) {
        // employeeService.create(emp)
        return new BaseResponse<>(new EmployeeResponse());
    }

    @PostMapping("/employee/{username}")
    public BaseResponse<EmployeeResponse> updateEmployee(@PathVariable String username,
                                                         @Valid @RequestBody Employee emp) throws Exception{
        //employeeService.update(username, emp)
        return new BaseResponse<>(new EmployeeResponse());
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
