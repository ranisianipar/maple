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
public class EmployeeController extends InvalidEmployeeAttributeValue {

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
        for (Employee e: employeeService.getAll()) {
            er = getMap().map(e, EmployeeResponse.class);
            employeeResponses.add(er);
        }

        br.setValue(employeeResponses);
        return br;
    }

    @GetMapping("/employee/{id}")
    public BaseResponse<EmployeeResponse> getEmployee(@PathVariable String id) {
        BaseResponse<EmployeeResponse> br = new BaseResponse<EmployeeResponse>();
        try {
            br.setValue(getMap().map(employeeService.get(id), EmployeeResponse.class));
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

        try {
            br.setValue(getMap().map(employeeService.create(emp), EmployeeResponse.class));
            br.succeedResponse();
        } catch (DataConstraintException e) {
            br.errorResponse();
            br.setErrorCode(e.getCode());
            br.setErrorMessage(e.getMessage());
        } finally {
            return br;
        }

    }

    @PostMapping("/employee/{id}")
    public BaseResponse<EmployeeResponse> updateEmployee(@PathVariable String id,
                                                         @Valid @RequestBody Employee emp) {
        BaseResponse<EmployeeResponse> br = new BaseResponse<EmployeeResponse>();
        try {
            br.setValue(getMap().map(employeeService.update(id, emp), EmployeeResponse.class));
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

    @DeleteMapping("/employee/{id}")
    public BaseResponse<String> deleteEmployee(@PathVariable String id) {
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
    public BaseResponse<String> deleteEmployees() {
        BaseResponse br = new BaseResponse("All employees have been deleted");
        br.succeedResponse();
        employeeService.deleteAll();
        return br;
    }

    //helper method
    public MapperFacade getMap() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Employee.class, EmployeeResponse.class)
                .byDefault().exclude("password").register();
        return mapperFactory.getMapperFacade();
    }
}
