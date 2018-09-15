package future.maple.model;

import org.springframework.data.annotation.Id;

import java.util.Date;

public class Employee {

    @Id
    public String employeeId;

    public String username;
    public String password;
    public String superiorId;
    public String name;
    public String phone;
    public boolean isAdmin;
    public Date createdDate;
    public Date updatedDate;

//    atribut displayPicture gmn?

    public Employee(String id, String username, String password, String sid, String name, String phone, boolean isAdmin){
        employeeId = id;
        this.username = username;
        this.password = password;
        superiorId = sid;
        this.name = name;
        this.phone = phone;
        this.isAdmin = isAdmin;
        createdDate = new Date();
    }

    @Override
    public String toString(){
        return String.format("Employee id:'%s', username:'%s', password:'%s', sid:'%s' "
                ,employeeId, username, password, superiorId);
    }
}
