package com.maple.MockingObject;

import com.maple.Admin;
import com.maple.Assignment;
import com.maple.Employee;
import com.maple.Item;


public class FakeObjectFactory {

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

    // without date
    public static Item getFakeItemBasic() {
        Item item = new Item();
        item.setName("Daniel Wellington Watch");
        item.setQuantity(500);
        item.setDescription("Jam mahal");
        item.setPrice(1500000);
        item.setItemSku("ITM-0");
        return item;
    }

    public static Assignment getFakeAssignmentBasic() {
        Assignment assignment = new Assignment();
        assignment.setEmployeeId("EMP-0");
        assignment.setItemSku("ITM-0");
        assignment.setQuantity(1);
        return assignment;
    }
}
