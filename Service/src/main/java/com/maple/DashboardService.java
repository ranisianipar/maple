package com.maple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    @Autowired
    AssignmentService assignmentService;

    @Autowired
    EmployeeService employeeService;


}
