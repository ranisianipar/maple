package com.maple;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class EmployeeResponse {
    public String id;
    public String username;
    public String superiorId;
    public String name;
    public String phone;
    public Date createdDate;
    public Date updatedDate;
    public String imagePath;
    public String email;

    public EmployeeResponse() {}

}
