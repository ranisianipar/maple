package com.maple;

import com.maple.Exception.MapleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

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
        //System.out.println("Employee: "+employee.toString());

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

    public void logout(HttpSession httpSession){
        // flush session
        if (httpSession.getAttribute("token") == null) return;
        String token = httpSession.getAttribute("token").toString();
        jedis.del(token);
    }

    public static String getCurrentUserId(HttpSession httpSession){
        return jedis.get(httpSession.getAttribute("token").toString());
    }
}
