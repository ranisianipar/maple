package com.maple;

import com.maple.Exception.MapleException;
import com.maple.validation.MissingParamHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.maple.Helper.SimpleUtils.*;

@CrossOrigin(origins = Constant.LINK_ORIGIN)
@RestController
public class AuthController extends MissingParamHandler {

    @Autowired
    AuthService authService;

    @PostMapping(Constant.LINK_LOGIN)
    public BaseResponse login(
            @RequestBody LoginRequest loginRequest,
            HttpSession httpSession
            ) {
        try {
            String token = authService.getValidToken(loginRequest, httpSession);
            return responseMapping(new BaseResponse(token), null);
        } catch (MapleException m) {
            return responseMapping(new BaseResponse(), m);
        }

    }

    @PostMapping(Constant.LINK_LOGOUT)
    public BaseResponse logout(HttpServletRequest request) {
        authService.logout(request);
        return responseMapping(new BaseResponse("logout succeed"), null);
    }

    @GetMapping(Constant.LINK_USER)
    public EmployeeResponse getUserDetails(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        try {
            onlyAuthorizedUser("get user details", token);
        }   catch (MapleException m) {
            responseMapping(new BaseResponse(), m);
        }

        return userMapping(token);
    }

    private EmployeeResponse userMapping(String token) {
        EmployeeResponse er = new EmployeeResponse();
        String role = authService.decideRole(token);
        if (role == null) {
            return er;
        }
        
        else if (role.equals("admin")) {
            er.setUsername(getCurrentUserId(token));
            return er;
        }

        else if (role.equals("employee")) {
            try {
                return getEmployeeMap().map(authService.getEmployeeData(token),EmployeeResponse.class);
            }   catch (MapleException m) {
                return new EmployeeResponse();
            }

        }
        return er;
    }
}
