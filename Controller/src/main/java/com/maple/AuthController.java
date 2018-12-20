package com.maple;

import com.maple.validation.MissingParamHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@CrossOrigin(origins = Constant.LINK_ORIGIN)
@RestController
public class AuthController extends MissingParamHandler {
    @PostMapping
    public void login(@RequestParam(value = "username") String username,
                      @RequestParam(value = "password") String password,
                      HttpSession httpSession) {

        //session input employee
        //return access token

    }
}
