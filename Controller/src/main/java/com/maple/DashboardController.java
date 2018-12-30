package com.maple;

import com.maple.Exception.MapleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.maple.Helper.SimpleUtils.*;

@CrossOrigin(origins = Constant.LINK_ORIGIN)
@RequestMapping(value = Constant.LINK_DASHBOARD_PREFIX)
@RestController
public class DashboardController {

    @Autowired
    AssignmentService assignmentService;

    // get by status
    @GetMapping
    public BaseResponse getDashboard(HttpServletRequest request) {
        BaseResponse br = new BaseResponse();
        String token = getTokenFromRequest(request);
        try {
            onlyAuthorizedUser("get dashboard", token);
        }   catch (MapleException m) {
            return responseMapping(br, m);
        }
        DashboardCardResponse dr = new DashboardCardResponse();
        dr.setPending(assignmentService.countByStatus(token, "PENDING"));
        dr.setApproved(assignmentService.countByStatus(token, "APPROVED"));
        dr.setReceived(assignmentService.countByStatus(token, "RECEIVED"));
        dr.setAll(assignmentService.countByStatus(token, "all"));

        br.setValue(dr);
        return responseMapping(br, null);
    }

    @GetMapping(Constant.LINK_REQUESTED)
    public BaseResponse getAssignmentWithoutButton(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "updatedDate") String sortBy,
            @RequestParam(value = "status", defaultValue = "all") String status,
            HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        try {
            onlyAuthorizedUser("get all assignment",token);
        } catch (MapleException m) {
            return responseMapping(new BaseResponse(),m);
        }
        Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
        return responseMappingWithPage(new BaseResponse(), pageRequest, token,
                assignmentService.getAssignmentByStatus(pageRequest, token, status).iterator(), status);
    }

    // HELPER METHOD

    private BaseResponse responseMappingWithPage(BaseResponse br, Pageable pageRequest,
                                                 String token, Iterator assignmentIterator, String status) {
        List<AssignmentResponse> assignmentResponses = new ArrayList<>();

        AssignmentResponse ar;
        Assignment assignment;

        while (assignmentIterator.hasNext()) {
            assignment = (Assignment) assignmentIterator.next();
            try {
                ar = getAssignmentMap().map(assignment, AssignmentResponse.class);
                String name = assignmentService.getEmployeeName(assignment.getEmployeeId());
                ar.setEmployeeUsername(name);
                ar.setItemName(assignmentService.getItemName(assignment.getItemSku()));
            } catch (MapleException m) {
                return responseMapping(new BaseResponse(), m);
            }
            assignmentResponses.add(ar);
        }
        br.setPaging(pageRequest);
        br.setTotalPages(assignmentService.getTotalPagesForDashboard(pageRequest.getPageSize(), token, status));
        br.setTotalRecords(assignmentService.getTotalObjectByUser(token, status));
        br.setValue(assignmentResponses);
        return responseMapping(br, null);
    }

}
