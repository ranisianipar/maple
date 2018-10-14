package future.maple.web.model;

import future.maple.model.Employee;

import java.util.Date;

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

    public EmployeeResponse(Employee employee) {
        this.id = employee.id;
        this.username = employee.username;
        this.superiorId = employee.superiorId;
        this.name = employee.name;
        this.phone = employee.phone;
        this.createdDate = employee.createdDate;
        this.updatedDate = employee.updatedDate;
        this.imagePath = employee.imagePath;
        this.email = employee.email;
    }

}
