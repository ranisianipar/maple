package com.maple;

import lombok.Data;

import java.util.List;

@Data
public class AssignmentResponse {
    Assignment assignment;
    String itemName;
    String employeeUsername;
    List<String> button;

    public AssignmentResponse() {}
}
