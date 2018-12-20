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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@CrossOrigin(origins = Constant.LINK_ORIGIN)
@RequestMapping(value = Constant.LINK_ASSIGNMENT_PREFIX)
@RestController
public class AssignmentController extends InvalidAssignmentAttributeValue {


    @Autowired
    private AssignmentService assignmentService;


    @GetMapping
    public BaseResponse getAllAssignments(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "updatedDate") String sortBy) {
        return responseMapping(new BaseResponse(), PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy)), null);
    }

    @GetMapping(value = Constant.LINK_ID_PARAM)
    public BaseResponse getAssignment(@PathVariable String id) {
        BaseResponse br = new BaseResponse<>();
        try {
            return responseMapping(br, assignmentService.getAssignment(id), null);
        } catch (MapleException e) {
            return responseMapping(br, e);
        }
    }

    @PostMapping
    public BaseResponse requestManyAssignment(@Valid @RequestBody ManyAssignmentRequest manyAssignmentRequest) {
        BaseResponse br = new BaseResponse<>();
        try {
            assignmentService.assignMany(manyAssignmentRequest);
            return responseMapping(br, null);
        } catch (MapleException m) {
            return responseMapping(br, m);
        }
    }

    // url: /assignment/{id}/status?action=[up/down]
    @PostMapping(Constant.LINK_UPDATE_STATUS)
    public BaseResponse updateStatusAssignment(
            @PathVariable String id,
            @RequestParam(value = "action") String action) {
        try {
            return responseMapping(new BaseResponse(), assignmentService.updateStatus(id, action), null);
        } catch (MapleException e) {
            return responseMapping(new BaseResponse(), e);
        }

    }

    //HELPER METHOD

    private BaseResponse responseMapping(BaseResponse br, Pageable pageRequest, MapleException e) {
        List<AssignmentResponse> assignmentResponses = new ArrayList<>();

        AssignmentResponse ar;
        Assignment assignment;
        Iterator<Assignment> assignmentPage = assignmentService.getAllAssignments(pageRequest).iterator();

        while (assignmentPage.hasNext()) {
            assignment = assignmentPage.next();
            ar = new AssignmentResponse();
            ar.setAssignment(assignment);
            ar.setButton(assignmentService.getButtonByStatus(assignment.getStatus()));
            assignmentResponses.add(ar);
        }
        br.setTotalRecords(assignmentService.getTotalAssignment());
        br.setValue(assignmentResponses);
        br.setPaging(pageRequest);
        br.setTotalPages(assignmentService.getTotalPage(pageRequest.getPageSize()));
        return responseMapping(br, e);
    }

    private BaseResponse responseMapping(BaseResponse br, Assignment assignment, MapleException e) {
        AssignmentResponse assignmentResponse = new AssignmentResponse();
        assignmentResponse.setButton(assignmentService.getButtonByStatus(assignment.getStatus()));
        assignmentResponse.setAssignment(assignment);
        br.setValue(assignmentResponse);
        return responseMapping(br, e);
    }

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
