package maple.Controller;

import maple.Model.Employee;
import maple.Service.EmployeeService;
import maple.Service.SimpleException;
import maple.WebModel.BaseResponse;
import maple.WebModel.EmployeeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@RestController
public class EmployeeController {

    public List<EmployeeResponse> employeeResponses;
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/employee")
    public BaseResponse<EmployeeResponse> getAllEmployees(){

        employeeResponses = new ArrayList<>();
        for (Employee e: employeeService.getAll()) {
            //e
            employeeResponses.add(new EmployeeResponse());
        }
        return new BaseResponse<>(employeeResponses);
    }

    @GetMapping("/employee/{username}")
    public BaseResponse<EmployeeResponse> getEmployee(@PathVariable String username) {
        BaseResponse<EmployeeResponse> br = new BaseResponse<EmployeeResponse>();
        //employeeService.get(username)
        try {
            employeeService.get(username);
            br.setValue(new EmployeeResponse());
            br.setCode(200);
            br.setSuccess(true);
        } catch (SimpleException e) {
            br.setCode(500);
            br.setErrorMessage(e.getMessage());
            br.setErrorCode(404);
        } finally {
            return br;
        }

    }

    @PostMapping("/employee")
    public BaseResponse<EmployeeResponse> createEmployee(@Valid @RequestBody Employee emp) {
        // employeeService.create(emp)
        return new BaseResponse<>(new EmployeeResponse());
    }

    @PostMapping("/employee/{username}")
    public BaseResponse<EmployeeResponse> updateEmployee(@PathVariable String username,
                                                         @Valid @RequestBody Employee emp) throws Exception{
        //employeeService.update(username, emp)
        return new BaseResponse<>(new EmployeeResponse());
    }

    @DeleteMapping("/employee/{username}")
    public BaseResponse<String> removeEmployee(@PathVariable String username) {
        employeeService.delete(username);
        return new BaseResponse<>(username+" has been deleted");
    }

    @DeleteMapping("/employees")
    public BaseResponse<String> removeEmployees() {
        return new BaseResponse<>("All employees has been deleted");
    }
}
