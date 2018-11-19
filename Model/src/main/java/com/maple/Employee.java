package com.maple;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Document(collection = "employees")
@Data
public class Employee {

    @Id
    private String id;

    @NotBlank(message = "Username can't be blank")
    @Indexed(unique=true)
    private String username;
    @NotBlank(message = "Password can't be blank")
    private String password;
    public String superiorId;

    @NotBlank(message = "Name can't be blank")
    @Indexed(unique=true)
    private String name;
    private String phone;
    private Date createdDate;
    private Date updatedDate;
    private String imagePath;

    @NotBlank(message = "Email can't be blank")
    private String email;

    //buat mapping
    public Employee(){
        createdDate = new Date();
    }

    //perlu dipindah
    public void update() {
        this.updatedDate = new Date();
    }
}
