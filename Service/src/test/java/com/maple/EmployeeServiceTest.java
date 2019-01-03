package com.maple;


import com.maple.Exception.MapleException;
import com.maple.Exception.NotFoundException;
import com.maple.MockingObject.FakeObjectFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = {EmployeeService.class})
public class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private AdminService adminService;

    @Mock
    private AssignmentService assignmentService;

    @Mock
    private CounterService counterService;

    @Mock
    private EmployeeRepository employeeRepository;

    Employee employee = FakeObjectFactory.getFakeEmployeeBasic();
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdDate"));
    Page<Employee> employeePageMock = Mockito.mock(Page.class);


    @Before
    public void init() {


    }

    @Test
    public void getAllEmployee() {
        when(employeeRepository.findAll(pageable)).thenReturn(employeePageMock);
        when(employeeRepository.findByUsernameLike("hehe", pageable)).thenReturn(employeePageMock);

        Page<Employee> employeePage = employeeService.getAll(null, pageable);
        assertEquals(employeePageMock, employeePage);
        employeePage = employeeService.getAll("hehe", pageable);
        assertEquals(employeePageMock, employeePage);
    }

    // GET

    @Test
    public void getEmployeeNotFound() {
        boolean thrown = false;
        try {
            employeeService.get("EMP-99");
        }   catch (NotFoundException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void getEmployeeFound() throws MapleException {
        when(employeeRepository.findById("EMP-0")).thenReturn(Optional.of(employee));
        Employee wantedEmployee = employeeService.get("EMP-0");
        assertEquals(employee, wantedEmployee);
    }

    @Test
    public void getEmployeeByUsername() {
        when(employeeRepository.findByUsername("EMP")).thenReturn(employee);
        assertEquals(employee, employeeService.getEmployeeByUsername("EMP"));
    }

    // GET total

    @Test
    public void getTotalEmployee() {
        when(employeeRepository.count()).thenReturn((long) 0);
        assertEquals(0, employeeService.getTotalEmployee());
    }

    @Test
    public void getTotalPages() {
        when(employeeRepository.count()).thenReturn((long) 100);
        assertEquals(10, employeeService.getTotalPage(10));
        assertEquals(13, employeeService.getTotalPage(8));
    }


    // GET by superior ID
    @Test
    public void getEmployeeBySuperiorId() {
        List employees = new ArrayList();
        employees.add(employee);
        when(employeeRepository.findBySuperiorId(null)).thenReturn(employees);

        assertEquals(1, employeeService.getBySuperiorId(null).size());
    }

    // CREATE

    @Test
    public void createEmployeeBasicWithoutPhotoValid(){
        boolean thrown = false;
        when(counterService.getNextEmployee()).thenReturn("EMP-0");
        when(employeeRepository.save(employee)).thenReturn(employee);

        try {
            employeeService.create(employee, null);
        }   catch (Exception m) {
            thrown = true;
        }
        assertFalse(thrown);
    }

    public void createEmployeeWithSuperiorWithoutPhotoValid() throws MapleException, IOException {
        Employee employeeWithSuperior = FakeObjectFactory.getFakeEmployeeHasSuperior();
        Employee result;

        when(employeeRepository.findById("EMP-0")).thenReturn(Optional.of(employee));
        when(employeeRepository.findById("EMP-1")).thenReturn(Optional.of(employeeWithSuperior));

        //valid
        result = employeeService.create(employeeWithSuperior, null);
        assertEquals(employeeWithSuperior, result);
    }

    @Test
    public void createEmployeeWithSuperiorWithoutPhotoInvalidData() throws IOException{
        Employee employeeWithSuperior = FakeObjectFactory.getFakeEmployeeHasSuperior();
        boolean thrown = false;
        String errorMessage = "";


        when(employeeRepository.findById("EMP-99")).thenReturn(Optional.empty());
        when(employeeRepository.findByUsername("EMP")).thenReturn(employee);

        //superior doesn't exist and data isn't unique
        employeeWithSuperior.setSuperiorId("EMP-99");
        employeeWithSuperior.setUsername("EMP");
        employeeWithSuperior.setEmail("EMP");
        try {
            employeeService.create(employeeWithSuperior, null);
        }   catch (MapleException m) {
            thrown = true;
            errorMessage = m.getMessage();
            m.getMessage().contains("");
        }
        assertTrue(thrown);
        assertTrue(errorMessage.contains("Email"));
        assertTrue(errorMessage.contains("superior"));
        assertTrue(errorMessage.contains("username"));
    }

    @Test
    public void createEmployeeSelfSuperiorWithoutPhoto() throws IOException {
        Employee employeeWithSuperior = FakeObjectFactory.getFakeEmployeeHasSuperior();
        boolean thrown = false;
        when(counterService.getNextEmployee()).thenReturn("EMP-1");
        when(employeeRepository.findById("EMP-1")).thenReturn(Optional.of(employeeWithSuperior));
        employeeWithSuperior.setSuperiorId("EMP-1");
        try {
            employeeService.create(employeeWithSuperior, null);
        }   catch (MapleException m) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    // with photo

    // UPDATE

//    @Test
//    public void updateEmployeeValid() throws MapleException, IOException {
//        when(employeeRepository.findById("EMP-0")).thenReturn(Optional.of(employee));
//
//        Employee updatedEmployee = FakeObjectFactory.getFakeEmployeeBasic();
//        updatedEmployee.setPassword("HAHAHA");
//
//        // how to mock getCurrentUserId() --> static method
//        assertEquals(updatedEmployee, employeeService.update("EMP-0", updatedEmployee, null, ""));
//    }

    // DELETE

    @Test
    public void deleteManyEmployeeThatBeASuperior() throws Exception{
        ArrayList deletedIds = new ArrayList();
        deletedIds.add("EMP-0");
        when(employeeRepository.findById("EMP-0")).thenReturn(Optional.of(employee));
        ArrayList employees = new ArrayList();
        employees.add(FakeObjectFactory.getFakeEmployeeHasSuperior());
        when(employeeRepository.findBySuperiorId(employee.getId())).thenReturn(employees);

        employeeService.deleteMany(deletedIds);
        verify(employeeRepository, times(1)).deleteByIdIn(deletedIds);
        verify(assignmentService, times(1)).updateByEmployee(deletedIds);

    }

    // Non-Functional

    @Test
    public void checkEmployeeExistOrNot() {
        assertFalse(employeeService.isExist("EMP-99"));
    }

    @Test
    public void onlyTheirSuperiorEmployeeHasNoSuperiorRaiseException() throws MapleException{
        when(employeeRepository.findById("EMP-0")).thenReturn(Optional.of(employee));
        boolean thrown = false;

        try {
            employeeService.onlyTheirSuperior("EMP-0", "EMP-1");
        }   catch (MapleException m) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void onlyTheirSuperiorWithEmployeeHasSuperiorRaiseException() throws MapleException{
        when(employeeRepository.findById("EMP-1")).thenReturn(Optional.of(FakeObjectFactory.getFakeEmployeeHasSuperior()));
        boolean thrown = false;

        try {
            employeeService.onlyTheirSuperior("EMP-1", "ADMIN-1");
        }   catch (MapleException m) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void onlyTheirSuperiorPassed(){
        Optional<Employee> employeeHasSuperior = Optional.of(FakeObjectFactory.getFakeEmployeeHasSuperior());
        when(employeeRepository.findById("EMP-1")).thenReturn(employeeHasSuperior);

        boolean thrown = false;
        try {
            employeeService.onlyTheirSuperior("EMP-1",employeeHasSuperior.get().getSuperiorId());
        }   catch (MapleException m) {
            thrown = true;
        }
        assertFalse(thrown);
    }








}
