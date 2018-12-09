package com.maple;

import com.maple.Exception.MapleException;
import com.maple.validation.InvalidAssignmentAttributeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://localhost")
@RestController
public class AssignmentController extends InvalidAssignmentAttributeValue {

    @Autowired
    private AssignmentService assignmentService;

    public List<EmployeeResponse> employeeResponses;

    @GetMapping("/assignment")
    public BaseResponse<EmployeeResponse> getAllAssignments(
            @RequestParam (value = "page", defaultValue = "0") int page,
            @RequestParam (value = "size", defaultValue = "10") int size,
            @RequestParam (value = "sortBy", defaultValue = "assignmentId") String sortBy
    ){
        BaseResponse br = new BaseResponse<>();
        Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
        br.setTotalRecords(assignmentService.getTotalAssignment());
        br.setTotalPages(assignmentService.getTotalPage(pageRequest.getPageSize()));
        br.setValue(assignmentService.getAllAssignments(pageRequest));
        return responseMapping(br,null);
    }

    @GetMapping("/assignment/{id}")
    public BaseResponse getAssignment(@PathVariable String id) {
        BaseResponse br = new BaseResponse<>();
        Assignment assignment;
        try {
            assignment = assignmentService.getAssignment(id);
            br.setValue(assignment);
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br, e);
        }
    }

    @PostMapping("/assignment")
    public BaseResponse createAssignment(@Valid @RequestBody Assignment assignment) {
        BaseResponse br = new BaseResponse<>();
        try {
            br.setValue(assignmentService.createAssignment(assignment));
            return responseMapping(br, null);
        } catch (MapleException m) {
            return responseMapping(br, m);
        } catch (IOException i) {
            return responseMapping(br, new MapleException(i.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @PostMapping("/assignment/{id}")
    public BaseResponse updateAssignment(
            @PathVariable String id,
            @Valid @RequestBody Assignment assignment) {
        BaseResponse br = new BaseResponse<>();
        try {
            br.setValue(assignmentService.updateAssignment(id, assignment));
            return responseMapping(br, null);
        } catch (MapleException m) {
            return responseMapping(br, m);
        } catch (IOException i) {
            return responseMapping(br, new MapleException(i.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @DeleteMapping("/assignment/{id}")
    public BaseResponse<String> deleteAssignment(@PathVariable String id) {
        BaseResponse br = new BaseResponse();
        try {
            assignmentService.deleteAssignment(id);
            return responseMapping(br, null);
        } catch (MapleException m) {
            return responseMapping(br, m);
        }
    }

    @DeleteMapping("/assignments")
    public BaseResponse<String> deleteAssignments() {
        assignmentService.deleteAllAssignment();
        return responseMapping(new BaseResponse("All assignments have been deleted"), null);
    }

    //HELPER METHOD
    private BaseResponse responseMapping(BaseResponse br, MapleException e) {
        if (e == null) {
            br.setCode(HttpStatus.OK);
            br.setSuccess(true);
            return br;
        }
        br.setCode(HttpStatus.BAD_REQUEST);
        br.setErrorMessage(e.getMessage());
        br.setErrorCode(e.getCode());
        return br;
    }
}
