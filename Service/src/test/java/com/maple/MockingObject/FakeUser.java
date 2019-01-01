package com.maple.MockingObject;

import com.maple.Admin;
import com.maple.Employee;

public class FakeUser{

    public static Employee getFakeEmployeeBasic() {
        Employee employee = new Employee();
        employee.setUsername("EMP");
        employee.setPassword("EMP");
        employee.setEmail("EMP@xmail.com");
        employee.setName("EMP");
        employee.setId("EMP-0");
        return employee;
    }

    public static Employee getFakeEmployeeHasSuperior() {
        Employee employee = new Employee();
        employee.setUsername("EMP1");
        employee.setPassword("EMP1");
        employee.setEmail("EMP1@xmail.com");
        employee.setName("EMP1");
        employee.setId("EMP-1");

        employee.setSuperiorId("EMP-0");
        employee.setPhone("081234567890");
        return employee;
    }

    public static Admin getFakeAdmin() {
        return new Admin("ADMIN123", "ADMIN123");
    }
}
