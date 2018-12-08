package com.maple;

import com.maple.validation.MissingParamHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost")
@RestController
public class AuthController extends MissingParamHandler {
    @PostMapping
    public void login(@RequestParam(value = "username") String username,
                      @RequestParam(value = "password") String password) {
        //session input employee
        //return access token

    }
}
