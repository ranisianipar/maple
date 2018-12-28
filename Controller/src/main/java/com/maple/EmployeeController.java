package com.maple;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maple.Exception.MapleException;
import com.maple.validation.InvalidEmployeeAttributeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

import static com.maple.Helper.SimpleUtils.*;


@CrossOrigin(origins = Constant.LINK_ORIGIN)
@RequestMapping(Constant.LINK_EMPLOYEE_PREFIX)
@RestController
public class EmployeeController extends InvalidEmployeeAttributeValue {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AdminService adminService;

    @GetMapping
    public BaseResponse<EmployeeResponse> getAllEmployees(
            @RequestParam (value = "page", defaultValue = "0") int page,
            @RequestParam (value = "size", defaultValue = "10") int size,
            @RequestParam (value = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam (value = "search", required = false) String search,
            HttpServletRequest request
    ){
        String token = getTokenFromRequest(request);
        try {
            onlyAuthorizedUser("get employee", token);
        } catch (MapleException m) {
            return responseMapping(new BaseResponse(), m);
        }

        Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
        BaseResponse br = new BaseResponse();
        br.setPaging(pageRequest);
        br.setTotalPages(employeeService.getTotalPage(pageRequest.getPageSize()));
        br.setTotalRecords(employeeService.getTotalEmployee());
        return responseMappingAllEmployee(br, employeeService.getAll(search, pageRequest).iterator());
    }

    @GetMapping(Constant.LINK_ID_PARAM)
    public BaseResponse<EmployeeResponse> getEmployee(@PathVariable String id, HttpServletRequest request) {
        BaseResponse<EmployeeResponse> br = new BaseResponse<EmployeeResponse>();
        try {
            onlyAuthorizedUser("get employee", getTokenFromRequest(request));
            br.setValue(getEmployeeMap().map(employeeService.get(id), EmployeeResponse.class));
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br, e);
        }
    }

    @PostMapping
    public BaseResponse<EmployeeResponse> createEmployee(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "data") String employee,
            HttpServletRequest request) {
        BaseResponse<EmployeeResponse> br = new BaseResponse<EmployeeResponse>();
        try {
            adminService.onlyAdmin("create employee", getTokenFromRequest(request));
            br.setValue(getEmployeeMap().map(employeeService.create(
                    new ObjectMapper().readValue(employee, Employee.class), file), EmployeeResponse.class));
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br,e);
        } catch (IOException e) {
            return responseMapping(br, new MapleException(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @PostMapping(Constant.LINK_ID_PARAM)
    public BaseResponse<EmployeeResponse> updateEmployee(
            @PathVariable String id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @Valid @RequestParam(value = "data") String employee,
            HttpServletRequest request) {
        BaseResponse<EmployeeResponse> br = new BaseResponse<EmployeeResponse>();
        String token = getTokenFromRequest(request);
        try {
            onlyAuthorizedUser("update employee", token);
            br.setValue(getEmployeeMap().map(employeeService.update(id,
                    new ObjectMapper().readValue(employee, Employee.class), file, token), EmployeeResponse.class));
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br, e);
        }catch (IOException e) {
            return responseMapping(br, new MapleException(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @DeleteMapping
    public BaseResponse<String> deleteEmployee(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        BaseResponse br = new BaseResponse();
        try {
            adminService.onlyAdmin("delete employee",getTokenFromRequest(request));
            employeeService.deleteMany(deleteRequest.getIds());
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br, e);
        }
    }
}
