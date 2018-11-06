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

    //buat mapping
    public Employee(){
        createdDate = new Date();
    }

    public void update() {
        this.updatedDate = new Date();
    }
}
