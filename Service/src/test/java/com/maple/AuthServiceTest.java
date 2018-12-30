package com.maple;

import com.maple.Exception.MapleException;
import com.maple.Helper.JedisFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpSession;


import static com.maple.Helper.SimpleUtils.jedis;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = {AuthService.class})
public class AuthServiceTest {

//    @Rule
//    public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb("mapledb-test");

    @InjectMocks
    private AuthService authService;

    @Mock
    private AdminService adminService;
    @Mock
    private EmployeeService employeeService;

    HttpSession session;
    LoginRequest loginRequest;

    @Before
    public void init() {
        loginRequest = new LoginRequest();
        session = new MockHttpSession();
        jedis.del(session.getId());
    }

    @After
    public void reset() {
        jedis.del(session.getId());
    }

    @Test
    public void getValidTokenErrorTest() {
        boolean thrown = false;

        loginRequest.setUsername("zzz");
        loginRequest.setPassword("zzz");
        when(employeeService.getEmployeeByUsername("zzz")).thenReturn(null);
        when(adminService.getByUsername("zzz")).thenReturn(null);
        try {
            authService.getValidToken(loginRequest, session);
        }   catch (MapleException m) {
            thrown = true;
        }
        assertTrue(thrown);

    }

    @Test
    public void getValidTokenEmployeeSucceedTest() throws MapleException {
        Employee employee = new Employee();
        employee.setUsername("EMP");
        employee.setPassword("EMP");
        employee.setEmail("EMP@xmail.com");
        employee.setName("EMP");
        employee.setId("EMP-0");

        loginRequest.setUsername("EMP");
        loginRequest.setPassword("EMP");
        when(employeeService.getEmployeeByUsername("EMP")).thenReturn(employee);

        assertEquals(authService.getValidToken(loginRequest, session), session.getId());
        assertEquals(authService.getValidToken(loginRequest, session), session.getId());
    }

    @Test
    public void getValidTokenAdminSucceedTest() throws MapleException {
        Admin admin = new Admin("ADMIN123", "ADMIN123");
        loginRequest.setUsername("ADMIN123");
        loginRequest.setPassword("ADMIN123");

        when(adminService.getByUsername("ADMIN123")).thenReturn(admin);

        assertEquals(authService.getValidToken(loginRequest, session), session.getId()+"-ADMIN");
    }

}

