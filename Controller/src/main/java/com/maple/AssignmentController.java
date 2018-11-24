package com.maple;

import com.maple.validation.InvalidAssignmentAttributeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "http://localhost")
@RestController
public class AssignmentController extends InvalidAssignmentAttributeValue {

    public List<EmployeeResponse> employeeResponses;
    @Autowired
    private AssignmentService assignmentService;

    //need pagination
    @GetMapping("/assignment")
    public BaseResponse<EmployeeResponse> getAllAssignments(){
        //default value of get All Employees
        BaseResponse br = new BaseResponse<>();
        return br;
    }

    @GetMapping("/assignment/{id}")
    public BaseResponse getAssignment(@PathVariable String id) {
        BaseResponse<EmployeeResponse> br = new BaseResponse<EmployeeResponse>();
        return br;
    }

    @PostMapping("/assignment")
    public BaseResponse createAssignment(@Valid @RequestBody Assignment assignment) {
        BaseResponse<EmployeeResponse> br = new BaseResponse<EmployeeResponse>();
        return br;
    }

    @PostMapping("/assignment/{id}")
    public BaseResponse<EmployeeResponse> updateAssignment(@PathVariable String id,
                                                         @Valid @RequestBody Employee emp) {
        BaseResponse<EmployeeResponse> br = new BaseResponse<EmployeeResponse>();
        return br;
    }

    @DeleteMapping("/assignment/{id}")
    public BaseResponse<String> deleteAssignment(@PathVariable String id) {
        BaseResponse br = new BaseResponse();
        return br;
    }

    @DeleteMapping("/assignments")
    public BaseResponse<String> deleteAssignments() {
        BaseResponse br = new BaseResponse("All assignments have been deleted");
        br.succeedResponse();
        return br;
    }
}
