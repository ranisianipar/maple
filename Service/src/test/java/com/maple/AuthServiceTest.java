package com.maple;

import com.maple.Exception.MapleException;
import com.maple.MockingObject.MockHttpServletRequestEmployee;
import com.maple.MockingObject.MockHttpSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import javax.servlet.http.HttpServletRequest;
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
    Employee employee;
    HttpServletRequest request;

    @Before
    public void init() {
        employee = new Employee();
        employee.setUsername("EMP");
        employee.setPassword("EMP");
        employee.setEmail("EMP@xmail.com");
        employee.setName("EMP");
        employee.setId("EMP-0");

        loginRequest = new LoginRequest();
        session = new MockHttpSession();
        jedis.del(session.getId());
        request = new MockHttpServletRequestEmployee();

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
        assertEquals("UNKNOWN",authService.decideRole(session.getId()));
    }

    @Test
    public void getValidTokenEmployeeSucceedTest() throws MapleException {
        loginRequest.setUsername("EMP");
        when(employeeService.getEmployeeByUsername("EMP")).thenReturn(employee);

        boolean thrown = false;
        try {
            loginRequest.setPassword("xxx");
            authService.getValidToken(loginRequest, session);
        }   catch (MapleException m) {
            thrown = true;
        }
        assertTrue(thrown);
        loginRequest.setPassword("EMP");
        when(employeeService.isExist("EMP-0")).thenReturn(true);
        assertEquals(authService.getValidToken(loginRequest, session), session.getId());
        assertEquals(authService.getValidToken(loginRequest, session), session.getId());
        assertEquals("employee",authService.decideRole(session.getId()));
    }

    @Test
    public void getValidTokenAdminSucceedTest() throws MapleException {
        Admin admin = new Admin("ADMIN123", "ADMIN123");
        loginRequest.setUsername("ADMIN123");
        loginRequest.setPassword("ADMIN123");
        jedis.del(session.getId()+"-ADMIN");
        when(adminService.getByUsername("ADMIN123")).thenReturn(admin);
        when(adminService.isExist("ADMIN123")).thenReturn(true);

        String token = authService.getValidToken(loginRequest, session);
        assertEquals(token, session.getId()+"-ADMIN");
        assertEquals("admin",authService.decideRole(token));

        jedis.del(session.getId()+"-ADMIN");
    }
    @Test
    public void getEmployeeDataSucceed() throws MapleException{
        loginRequest.setUsername("EMP");
        loginRequest.setPassword("EMP");
        when(employeeService.getEmployeeByUsername("EMP")).thenReturn(employee);
        authService.getValidToken(loginRequest, session);

        when(employeeService.get("EMP-0")).thenReturn(employee);

        assertEquals(employee,authService.getEmployeeData(session.getId()));
    }

    @Test
    public void logoutSucceed() {
        authService.logout(request);
        assertEquals(null,jedis.get(request.getHeader("Authorization-key")));
    }
}

