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

    private Jedis jedis = JedisFactory.getInstance().getJedisPool().getResource();

    public void login(LoginRequest loginRequest, HttpSession httpSession) throws MapleException {
        Employee employee = employeeService.getEmployeeByUsername(loginRequest.getUsername());
        if (employee == null || !employee.getPassword().equals(loginRequest.getPassword()))
            throw new MapleException("Username and password don't match", HttpStatus.UNAUTHORIZED);

        // masukkin ke session
        httpSession.setAttribute("username",employee.getUsername());
        httpSession.setAttribute("token", "xyz");
        httpSession.setAttribute("email", employee.getEmail());

        /*  jedis.append(key, value);
        *   key = token
        *   value = employee id
        */

        /*
        * session cukup nyimpen token aja, atau mungkin last page yang di seen sama dia? keknya GAPENTING
        * */
        // lakuin set kalo dia update nama aja, atau ada perubahan lain gitu
        // atau ga isi value nya ID aja, nanti setiap dia ngeupdate nama jadi gampang
        // jedis.set(key, value);
    }

    public void logout(HttpSession httpSession) {
        // flush session
        httpSession.removeAttribute("username");
        // hapus tokennya, di jedis tokennya juga dihapus
        httpSession.removeAttribute("token");
        httpSession.removeAttribute("email");

        // jedis.del(key);
    }
}
