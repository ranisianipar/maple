package com.maple;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Document(collection = "assignments")
@Data
public class Assignment {
    @Id
    private String assignmentId;

    @NotBlank(message = "employeeId can't be blank")
    private String employeeId;
    @NotBlank(message = "itemSku can't be blank")
    private String itemSku;

    private String status;

    @NotNull(message = "quantity can't be blank")
    private Integer quantity;
    private Date createdDate;
    private Date updatedDate;
    private String createdBy;
    private String updatedBy;
    private String note;

    public Assignment(){}
}
