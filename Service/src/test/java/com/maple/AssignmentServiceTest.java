package com.maple;

import com.maple.Exception.MapleException;
import com.maple.MockingObject.FakeObjectFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = {AssignmentService.class})
public class AssignmentServiceTest {

    @InjectMocks
    private AssignmentService assignmentService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private ItemService itemService;

    Assignment assignment = FakeObjectFactory.getFakeAssignmentBasic();

    @Test
    public void getButtonByStatusNull() {
        assertNull(assignmentService.getButtonByStatus(assignment.getStatus()));
    }

    @Test
    public void getButtonByStatusPending() {
        assignment.setStatus("PENDING");
        assertTrue(assignmentService.getButtonByStatus(assignment.getStatus()).contains("btnApprove"));
        assertTrue(assignmentService.getButtonByStatus(assignment.getStatus()).contains("btnReject"));
    }

    @Test
    public void getButtonByStatusApproved() {
        assignment.setStatus("APPROVED");
        assertTrue(assignmentService.getButtonByStatus(assignment.getStatus()).contains("btnHandover"));
    }

    @Test
    public void getButtonByStatusReceived() {
        assignment.setStatus("RECEIVED");
        assertTrue(assignmentService.getButtonByStatus(assignment.getStatus()).contains("btnReturn"));
    }

    @Test
    public void getButtonByUnknownStatus() {
        assignment.setStatus("xyz");
        assertNull(assignmentService.getButtonByStatus(assignment.getStatus()));
    }

    @Test
    public void getItemName() throws MapleException {
        Item item = FakeObjectFactory.getFakeItemBasic();
        when(itemService.get("ITM-0")).thenReturn(item);
        assertEquals(item.getName(), assignmentService.getItemName("ITM-0"));
    }

    @Test
    public void getEmployeeName() throws MapleException {
        Employee employee = FakeObjectFactory.getFakeEmployeeBasic();
        when(employeeService.get("EMP-0")).thenReturn(employee);
        assertEquals(employee.getUsername(), assignmentService.getEmployeeName("EMP-0"));
    }
}
