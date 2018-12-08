package com.maple;

import com.maple.Exception.MapleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    EmployeeService employeeService;

    public void login(String username, String password) throws MapleException {
        Employee employee = employeeService.authenticate(username, password);
        if (employee == null) throw new MapleException("Username and password don't match", HttpStatus.UNAUTHORIZED);
        // masukkin ke session
    }

    public void logout() {
        // flush session
    }
}
