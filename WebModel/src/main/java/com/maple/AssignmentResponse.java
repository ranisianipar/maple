package com.maple;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AssignmentResponse {
    String status;
    String itemName;
    String employeeUsername;
    List<String> button;
    private Integer quantity;
    private Date createdDate;
    private Date updatedDate;
    private String createdBy;
    private String updatedBy;
    private String note;

    public AssignmentResponse() {}
}
