package com.maple;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.Date;

@Document(collection = "assignments")
@Data
public class Assignment {
    @Id
    private String assignmentId;
    private String employeeId;
    private String itemSku;

    private String status;
    private int quantity;
    private Date createdDate;
    private Date updatedDate;
    private String createdBy;
    private String updatedBy;
    private String note;

    public Assignment(){}
}
