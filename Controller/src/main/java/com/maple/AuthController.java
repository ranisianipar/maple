package com.maple;

import com.maple.Exception.MapleException;
import com.maple.validation.MissingParamHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import static com.maple.Helper.SimpleUtils.responseMapping;

@CrossOrigin(origins = Constant.LINK_ORIGIN)
@RestController
public class AuthController extends MissingParamHandler {

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public BaseResponse login(
            @RequestBody LoginRequest loginRequest,
            HttpSession httpSession) {
        try {
            httpSession.setAttribute("token", authService.getValidToken(loginRequest, httpSession));
            return responseMapping(new BaseResponse("session created"), null);
        } catch (MapleException m) {
            return responseMapping(new BaseResponse(), m);
        }

    }

    @PostMapping("/logout")
    public BaseResponse logout(HttpSession httpSession) {
        authService.logout(httpSession);
        httpSession.removeAttribute("token");
        return responseMapping(new BaseResponse("session deleted"), null);
    }
}
