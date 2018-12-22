package com.maple;

import com.maple.Exception.MapleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpSession;

@Service
public class AuthService {
    @Autowired
    EmployeeService employeeService;

    @Autowired
    AdminService adminService;

    private Jedis jedis = JedisFactory.getInstance().getJedisPool().getResource();

    public void login(LoginRequest loginRequest, HttpSession httpSession) throws MapleException {
        if (!jedis.exists(httpSession.getId()))
            return;

        if (!adminService.isAdmin(loginRequest.getUsername(), loginRequest.getPassword())) {
            httpSession.setAttribute("token", httpSession.getId());
            jedis.append(httpSession.getId(), loginRequest.getUsername());
            return;
        }
        Employee employee = employeeService.getEmployeeByUsername(loginRequest.getUsername());
        if (employee == null) throw new MapleException("User not found", HttpStatus.NOT_FOUND);
        if (!employee.getPassword().equals(loginRequest.getPassword()))
            throw new MapleException("Username and password don't match", HttpStatus.UNAUTHORIZED);

        httpSession.setAttribute("token", httpSession.getId());
        jedis.append(httpSession.getId(), employee.getId());
        System.out.println(httpSession.getId()+": "+jedis.get(httpSession.getId()));
    }

    public void logout(HttpSession httpSession){
        // flush session
        if (httpSession.getAttribute("token") == null) return;
        String token = httpSession.getAttribute("token").toString();
        jedis.del(token);
        httpSession.removeAttribute("token");

    }
}
