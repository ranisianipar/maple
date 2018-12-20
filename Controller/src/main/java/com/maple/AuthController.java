package com.maple;

import com.maple.Exception.MapleException;
import com.maple.validation.MissingParamHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@CrossOrigin(origins = Constant.LINK_ORIGIN)
@RestController
public class AuthController extends MissingParamHandler {

    @Autowired
    AuthService authService;

    @PostMapping
    public BaseResponse login(
            @RequestBody LoginRequest loginRequest,
            HttpSession httpSession) {

        //session input employee
        try {
            authService.login(loginRequest, httpSession);
            // nanti bikin redirect ke dashboard
            return new BaseResponse(httpSession.getAttribute("name"));
        } catch (MapleException m) {
            // nanti bikin redirect ke dashboard
            return new BaseResponse(m.getMessage());
        }

    }

    public BaseResponse getSession(HttpSession httpSession) {
        return new BaseResponse(httpSession.getAttribute("token"));
    }
}
