package com.maple;

import com.maple.Exception.DataConstraintException;
import com.maple.Exception.NotFoundException;
import com.maple.validation.InvalidEmployeeAttributeValue;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//bikin konstan
@CrossOrigin(origins = "http://localhost")
@RestController
public class EmployeeController extends InvalidEmployeeAttributeValue {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/employee")
    public BaseResponse<EmployeeResponse> getAllEmployees(
            @RequestParam (value = "page", required = false) Integer page,
            @RequestParam (value = "size", required = false) Integer size,
            @RequestParam (value = "sortBy", defaultValue = "employeeId") String sortBy
    ){
        return responseMapping(PageService.getPage(page, size, sortBy), new BaseResponse<>());
    }

    // jangan di hard-code, jadiin konstan, taro di web model (static variable)
    // bikin semacam response mapper --> helper method
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
            br.setErrorCode(e.getCode());
        } catch (NotFoundException e) {
            br.errorResponse();
            br.setErrorMessage(e.getMessage());
            br.setErrorCode(e.getCode());
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

    //HELPER METHOD

    private MapperFacade getMap() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Employee.class, EmployeeResponse.class)
                .byDefault().exclude("password").register();
        return mapperFactory.getMapperFacade();
    }

    private BaseResponse responseMapping (Pageable pageRequest, BaseResponse br) {
        List<EmployeeResponse> employeeResponses;

        employeeResponses = new ArrayList<>();
        EmployeeResponse er;

        Iterator<Employee> employeePage = employeeService.getAll(pageRequest).iterator();
        while (employeePage.hasNext()) {
            er = getMap().map(employeePage.next(), EmployeeResponse.class);
            employeeResponses.add(er);
        }
        br.setValue(employeeResponses);
        br.succeedResponse();
        return br;
    }

    private BaseResponse responseMapping (BaseResponse br) {
        return null;
    }
}
