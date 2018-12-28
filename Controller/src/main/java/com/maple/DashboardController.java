package com.maple;

import com.maple.Exception.MapleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.maple.Helper.SimpleUtils.getTokenFromRequest;
import static com.maple.Helper.SimpleUtils.onlyAuthorizedUser;
import static com.maple.Helper.SimpleUtils.responseMapping;

@CrossOrigin(origins = Constant.LINK_ORIGIN)
@RequestMapping(value = Constant.LINK_DASHBOARD_PREFIX)
@RestController
public class DashboardController {
    // pending items
    // accepted items
    // received items

    @Autowired
    HttpServletRequest request;

    @GetMapping
    public BaseResponse getDashboard(HttpServletRequest request) {
        BaseResponse br = new BaseResponse();
        try {
            onlyAuthorizedUser("get dashboard", getTokenFromRequest(request));
        }   catch (MapleException m) {
            return responseMapping(br, m);
        }
        br.setValue("");
        return br;
    }

}
