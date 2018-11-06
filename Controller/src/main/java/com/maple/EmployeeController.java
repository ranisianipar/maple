package com.maple;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost")
@RestController
public class EmployeeController {

    final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    public List<EmployeeResponse> employeeResponses;
    @Autowired
    private EmployeeService employeeService;

    //need pagination
    @GetMapping("/employee")
    public BaseResponse<EmployeeResponse> getAllEmployees(){
        //default value of get All Employees
        BaseResponse br = new BaseResponse<>();
        br.succeedResponse();

        employeeResponses = new ArrayList<>();
        EmployeeResponse er;
        mapperFactory.classMap(Employee.class, EmployeeResponse.class)
                .byDefault().exclude("password").register();
        MapperFacade mapper = mapperFactory.getMapperFacade();
        for (Employee e: employeeService.getAll()) {
            er = mapper.map(e, EmployeeResponse.class);
            employeeResponses.add(er);
        }

        br.setValue(employeeResponses);
        return br;
    }

    @GetMapping("/employee/{id}")
    public BaseResponse<EmployeeResponse> getEmployee(@PathVariable String id) {
        BaseResponse<EmployeeResponse> br = new BaseResponse<EmployeeResponse>();
        mapperFactory.classMap(Employee.class, EmployeeResponse.class)
                .byDefault().exclude("password").register();
        MapperFacade mapper = mapperFactory.getMapperFacade();
        try {
            br.setValue(mapper.map(employeeService.get(id), EmployeeResponse.class));
            br.succeedResponse();
        } catch (NotFoundException e) {
            br.errorResponse();
            br.setErrorMessage(e.getMessage());
            br.setErrorCode(e.getCode());
        } finally {
            return br;
        }

    }

    @PostMapping("/employee")
    public BaseResponse<EmployeeResponse> createEmployee(@Valid @RequestBody Employee emp) {
        BaseResponse<EmployeeResponse> br = new BaseResponse<EmployeeResponse>();
        mapperFactory.classMap(Employee.class, EmployeeResponse.class)
                .byDefault().exclude("password").register();
        MapperFacade mapper = mapperFactory.getMapperFacade();

        try {
            br.setValue(mapper.map(employeeService.create(emp), EmployeeResponse.class));
            br.succeedResponse();
        } catch (DataConstraintException e) {
            br.setErrorMessage(e.getMessage());
        } finally {
            return br;
        }

    }

    @PostMapping("/employee/{id}")
    public BaseResponse<EmployeeResponse> updateEmployee(@PathVariable String id,
                                                         @Valid @RequestBody Employee emp) {
        BaseResponse<EmployeeResponse> br = new BaseResponse<EmployeeResponse>();
        mapperFactory.classMap(Employee.class, EmployeeResponse.class)
                .byDefault().exclude("password").register();
        MapperFacade mapper = mapperFactory.getMapperFacade();
        try {
            br.setValue(mapper.map(employeeService.update(id, emp), EmployeeResponse.class));
            br.succeedResponse();
        } catch (DataConstraintException e) {
            br.errorResponse();
            br.setErrorMessage(e.getMessage());
            br.setErrorCode(e.code);
        } catch (NotFoundException e) {
            br.errorResponse();
            br.setErrorMessage(e.getMessage());
            br.setErrorCode(e.code);
        } finally {
            return br;
        }
    }

    @DeleteMapping("/employee/{username}")
    public BaseResponse<String> removeEmployee(@PathVariable String id) {
        BaseResponse br = new BaseResponse();
        try {
            employeeService.delete(id);
            br.succeedResponse();
        } catch (NotFoundException e) {
            br.errorResponse();
            br.setErrorCode(e.getCode());
            br.setErrorMessage(e.getMessage());
        } finally {
            return br;
        }
    }

    @DeleteMapping("/employees")
    public BaseResponse<String> removeEmployees() {
        BaseResponse br = new BaseResponse();

        br.succeedResponse();
        employeeService.deleteAll();
        return new BaseResponse<>("All employees has been deleted");
    }
}
