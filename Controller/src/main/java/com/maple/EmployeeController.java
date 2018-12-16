package com.maple;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maple.Exception.MapleException;
import com.maple.validation.InvalidEmployeeAttributeValue;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//bikin konstan
@CrossOrigin(origins = "http://localhost")
@RestController
public class EmployeeController extends InvalidEmployeeAttributeValue {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping(value = "/employee")
    public BaseResponse<EmployeeResponse> getAllEmployees(
            @RequestParam (value = "page", defaultValue = "0") int page,
            @RequestParam (value = "size", defaultValue = "10") int size,
            @RequestParam (value = "sortBy", defaultValue = "id") String sortBy
    ){
        return responseMapping(new BaseResponse(),
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy)),null);
    }

    // jangan di hard-code, jadiin konstan, taro di web model (static variable)
    @GetMapping("/employee/{id}")
    public BaseResponse<EmployeeResponse> getEmployee(@PathVariable String id) {
        BaseResponse<EmployeeResponse> br = new BaseResponse<EmployeeResponse>();
        try {
            br.setValue(getMap().map(employeeService.get(id), EmployeeResponse.class));
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br, e);
        }
    }

    @PostMapping("/employee")
    public BaseResponse<EmployeeResponse> createEmployee(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @Valid @RequestParam(value = "data") String employee
    ) {
        BaseResponse<EmployeeResponse> br = new BaseResponse<EmployeeResponse>();
        try {
            Employee emp = new ObjectMapper().readValue(employee, Employee.class);
            br.setValue(getMap().map(employeeService.create(emp, file), EmployeeResponse.class));
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br,e);
        } catch (IOException e) {
            return responseMapping(br, new MapleException(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @PostMapping("/employee/{id}")
    public BaseResponse<EmployeeResponse> updateEmployee(
            @PathVariable String id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @Valid @RequestParam(value = "data") String employee) {
        BaseResponse<EmployeeResponse> br = new BaseResponse<EmployeeResponse>();
        try {
            Employee emp = new ObjectMapper().readValue(employee, Employee.class);
            br.setValue(getMap().map(employeeService.update(id, emp, file), EmployeeResponse.class));
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br, e);
        }catch (IOException e) {
            return responseMapping(br, new MapleException(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @DeleteMapping("/employee")
    public BaseResponse<String> deleteEmployee(@RequestBody DeleteRequest deleteRequest) {
        BaseResponse br = new BaseResponse();
        try {
            employeeService.deleteMany(deleteRequest);
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br, e);
        }
    }

    @DeleteMapping("/employees")
    public BaseResponse<String> deleteEmployees() {
        employeeService.deleteAll();
        return responseMapping(new BaseResponse("All employees have been deleted"),
                null);
    }

    //HELPER METHOD
    private MapperFacade getMap() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Employee.class, EmployeeResponse.class)
                .byDefault().exclude("password").register();
        return mapperFactory.getMapperFacade();
    }

    // helper method for getAllEmployees
    private BaseResponse responseMapping (BaseResponse br, Pageable pageRequest, MapleException e) {
        List<EmployeeResponse> employeeResponses;

        employeeResponses = new ArrayList<>();
        EmployeeResponse er;

        Iterator<Employee> employeePage = employeeService.getAll(pageRequest).iterator();
        while (employeePage.hasNext()) {
            er = getMap().map(employeePage.next(), EmployeeResponse.class);
            employeeResponses.add(er);
        }
        br.setTotalRecords(employeeService.getTotalEmployee());
        br.setValue(employeeResponses);
        br.setPaging(pageRequest);
        br.setTotalPages(employeeService.getTotalPage(pageRequest.getPageSize()));
        return responseMapping(br, e);
    }
    private BaseResponse responseMapping (BaseResponse br, MapleException e) {
        if (e == null) {
            br.setCode(HttpStatus.OK);
            br.setSuccess(true);
            return br;
        }
        br.setErrorCode(e.getCode());
        br.setCode(HttpStatus.BAD_REQUEST);
        br.setErrorMessage(e.getMessage());
        return br;
    }
}
