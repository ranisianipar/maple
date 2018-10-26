package com.maple;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Document(collection = "employees")
@Getter @Setter
public class Employee {

    @Id
    public String id;

    @NotBlank(message = "Username can't be blank")
    public String username;
    @NotBlank(message = "Password can't be blank")
    public String password;
    public String superiorId;

    @NotBlank(message = "Fullname can't be blank")
    public String name;
    public String phone;
    public Date createdDate;
    public Date updatedDate;
    public String imagePath;

    @NotBlank(message = "Email can't be blank")
    public String email;

    public Employee(String id, String username, String password, String superiorId, String name, String phone, String email){
        this.id = id;
        this.username = username;
        this.password = password;
        this.superiorId = superiorId;
        this.name = name;
        this.phone = phone;
        createdDate = new Date();
        this.email = email;
    }
}
