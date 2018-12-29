package com.maple;

import com.maple.Exception.MapleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.maple.Helper.SimpleUtils.getTokenFromRequest;
import static com.maple.Helper.SimpleUtils.onlyAuthorizedUser;
import static com.maple.Helper.SimpleUtils.responseMapping;

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

        br.setValue(dr);
        return responseMapping(br, null);
    }

    @GetMapping(Constant.LINK_REQUESTED)
    public BaseResponse getAssignmentWithoutButton(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "updatedDate") String sortBy,
            @RequestParam(value = "status", required = false) String status,
            HttpServletRequest request) {
        BaseResponse br = new BaseResponse();
        String token = getTokenFromRequest(request);
        try {
            onlyAuthorizedUser("get user assignment", token);
        }   catch (MapleException m) {
            return responseMapping(br, m);
        }
        br.setTotalPages(assignmentService.getTotalPages(size, token));
        br.setValue(assignmentService.getAssignmentByStatus(
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy)), token, status));
        return responseMapping(br, null);
    }

}
