package com.maple;

import com.maple.Exception.MapleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.maple.Helper.SimpleUtils.getCurrentUserId;
import static com.maple.Helper.SimpleUtils.getTokenFromRequest;
import static com.maple.Helper.SimpleUtils.jedis;

@Service
public class AuthService {
    @Autowired
    EmployeeService employeeService;

    @Autowired
    AdminService adminService;


    public String getValidToken(LoginRequest loginRequest, HttpSession httpSession) throws MapleException {
        if (jedis.exists(httpSession.getId())) return httpSession.getId();

        // cek admin atau bukan
        Admin admin = adminService.getByUsername(loginRequest.getUsername());
        System.out.println("Admin: "+admin);
        System.out.println("Admin: "+adminService.getAll().toString());
        Employee employee = employeeService.getEmployeeByUsername(loginRequest.getUsername());
        System.out.println("Employee: "+employee);

        if (admin == null && employee == null) {
            throw new MapleException("Username and password didn't match", HttpStatus.UNAUTHORIZED);
        } else if (admin != null && admin.getPassword().equals(loginRequest.getPassword())) {
            jedis.append(httpSession.getId(), admin.getUsername());
            return httpSession.getId();
        } else if(employee != null && employee.getPassword().equals(loginRequest.getPassword())) {
            jedis.append(httpSession.getId(), employee.getId());
            return httpSession.getId();
        }

        throw new MapleException("Username and password didn't match", HttpStatus.UNAUTHORIZED);
    }

    public void logout(HttpServletRequest request){
        // flush session
        String token = getTokenFromRequest(request);
        if (token == null) return;
        jedis.del(token);
    }

    public Employee getEmployeeData(String token) throws MapleException{
        return employeeService.get(getCurrentUserId(token));
    }

    public String decideRole(String token){
        if (token == null) return "UNKNOWN";
        else if (adminService.isExist(getCurrentUserId(token))){
            return "admin";
        }
        else return "employee";
    }
}
