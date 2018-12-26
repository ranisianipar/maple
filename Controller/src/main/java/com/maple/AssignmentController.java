package com.maple;

import com.maple.Exception.MapleException;
import com.maple.validation.InvalidAssignmentAttributeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.maple.Helper.SimpleUtils.*;

@CrossOrigin(origins = Constant.LINK_ORIGIN)
@RequestMapping(value = Constant.LINK_ASSIGNMENT_PREFIX)
@RestController
public class AssignmentController extends InvalidAssignmentAttributeValue {


    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private  EmployeeService employeeService;


    @GetMapping
    public BaseResponse getAllAssignments(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "updatedDate") String sortBy,
            HttpSession httpSession) {
        try {
            onlyAuthorizedUser("get all assignment",httpSession);
        } catch (MapleException m) {
            return responseMapping(new BaseResponse(),m);
        }
        return responseMappingWithPage(new BaseResponse(),
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy)),
                httpSession);
    }

    @GetMapping(value = Constant.LINK_ID_PARAM)
    public BaseResponse getAssignment(@PathVariable String id, HttpSession httpSession) {
        BaseResponse br = new BaseResponse<>();
        try {
            onlyAuthorizedUser("get assignment", httpSession);
            return responseMappingAssignment(br, assignmentService.get(id, httpSession), null);
        } catch (MapleException e) {
            return responseMapping(br, e);
        }
    }

    @PostMapping
    public BaseResponse requestManyAssignment(@Valid @RequestBody ManyAssignmentRequest manyAssignmentRequest,
                                              HttpSession httpSession) {
        BaseResponse br = new BaseResponse<>();
        try {
            employeeService.onlyEmployee("assign many", httpSession);
            assignmentService.assignMany(manyAssignmentRequest, httpSession);
            return responseMapping(br, null);
        } catch (MapleException m) {
            return responseMapping(br, m);
        }
    }

    // url: /assignment/{id}/status?action=[up/down]
    @PostMapping(Constant.LINK_UPDATE_STATUS)
    public BaseResponse updateStatusAssignment(
            @PathVariable String id,
            @RequestParam(value = "action") String action,
            HttpSession httpSession) {
        try {
            return responseMappingAssignment(new BaseResponse(), assignmentService.updateStatus(id, action), null);
        } catch (MapleException e) {
            return responseMapping(new BaseResponse(), e);
        }

    }

    //akan dihapus
    @DeleteMapping(Constant.LINK_ID_PARAM)
    public BaseResponse deleteAssignment(@PathVariable String id) {
        assignmentService.delete(id);
        return new BaseResponse(id+" DELETED");
    }

    //HELPER METHOD

    private BaseResponse responseMappingWithPage(BaseResponse br, Pageable pageRequest, HttpSession httpSession) {

        List<AssignmentResponse> assignmentResponses = new ArrayList<>();

        AssignmentResponse ar;
        Assignment assignment;
        Iterator<Assignment> assignmentPage = assignmentService.getAll(pageRequest, httpSession).iterator();

        while (assignmentPage.hasNext()) {
            assignment = assignmentPage.next();

            try {
                ar = getAssignmentMap().map(assignment, AssignmentResponse.class);
                ar.setEmployeeUsername(assignmentService.getEmployeeName(assignment.getEmployeeId()));
                ar.setItemName(assignmentService.getItemName(assignment.getItemSku()));
            } catch (MapleException m) {
                return responseMapping(new BaseResponse(), m);
            }

            ar.setButton(assignmentService.getButtonByStatus(assignment.getStatus()));
            assignmentResponses.add(ar);
        }
        br.setPaging(pageRequest);
        br.setTotalPages(assignmentService.getTotalPage(pageRequest.getPageSize()));
        br.setTotalRecords(assignmentService.getTotalObject());
        br.setValue(assignmentResponses);
        return responseMapping(br, null);
    }

    private BaseResponse responseMappingAssignment(BaseResponse br, Assignment assignment, MapleException e) {
        AssignmentResponse assignmentResponse = new AssignmentResponse();
        assignmentResponse.setButton(assignmentService.getButtonByStatus(assignment.getStatus()));
        try {
            assignmentResponse.setItemName(assignmentService.getItemName(assignment.getItemSku()));
            assignmentResponse.setEmployeeUsername(assignmentService.getEmployeeName(assignment.getEmployeeId()));
        } catch (MapleException m) {
            return responseMapping(new BaseResponse(), m);
        }

        br.setValue(assignmentResponse);
        return responseMapping(br, e);
    }
}
