package com.maple;

import com.maple.Exception.MapleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class AuthService {
    @Autowired
    EmployeeService employeeService;

    public void login(LoginRequest loginRequest, HttpSession httpSession) throws MapleException {
        Employee employee = employeeService.getEmployeeByUsername(loginRequest.getUsername());
        if (employee == null || !employee.getPassword().equals(loginRequest.getPassword()))
            throw new MapleException("Username and password don't match", HttpStatus.UNAUTHORIZED);

        // masukkin ke session
        httpSession.setAttribute("username",employee.getUsername());
        httpSession.setAttribute("token", "xyz");
        httpSession.setAttribute("email", employee.getEmail());
    }

    public void logout(HttpSession httpSession) {
        // flush session
        httpSession.removeAttribute("username");
        // hapus tokennya, di jedis tokennya juga dihapus
        httpSession.removeAttribute("token");
        httpSession.removeAttribute("email");
    }
}
