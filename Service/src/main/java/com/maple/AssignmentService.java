package com.maple;

import com.maple.Exception.DataConstraintException;
import com.maple.Exception.MapleException;
import com.maple.Exception.NotFoundException;
import com.maple.Helper.SimpleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;


import static com.maple.Helper.SimpleUtils.getCurrentUserId;

@Service
public class AssignmentService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private CounterService counter;

    @Autowired
    private AdminService adminService;

    private final String ASSIGNMENT = "ASSIGNMENTS";

    // User as a superior
    public List<Assignment> getAssignment(Pageable pageable, String token) {
        String currentUserId = getCurrentUserId(token);
        Iterator employees;
        if (adminService.isExist(currentUserId)){
            // cari yang superiornya null
            employees = employeeService.getBySuperiorId(null).iterator();
        }
        else employees = employeeService.getBySuperiorId(currentUserId).iterator();
        List<String> ids = new ArrayList<>();
        while (employees.hasNext()) {
            Employee e = (Employee) employees.next();
            ids.add(e.getId());
        }
        return assignmentRepository.findByEmployeeIdIn(ids, pageable);
    }

    // User as a requestor --> buat dashboard
    public List<Assignment> getAssignmentByStatus(Pageable pageable, String token, String status) {
        String currentUserId = getCurrentUserId(token);
        //admin
        if (adminService.isExist(currentUserId)) {
            if (status.equals("all")) return assignmentRepository.findAll(pageable).getContent();
            return assignmentRepository.findByStatus(status, pageable);
        }
        if (status.equals("all")) return assignmentRepository.findByEmployeeId(currentUserId, pageable);
        return assignmentRepository.findByEmployeeIdAndStatus(currentUserId, status, pageable);
    }

    // buat dashboard
    public long countByStatus(String token, String status){
        String currentUserId = getCurrentUserId(token);
        if (adminService.isExist(currentUserId)) {
            if (status.equals("all")) return assignmentRepository.count();
            return assignmentRepository.countByStatus(status);
        }
        if (status.equals("all")) return assignmentRepository.countByEmployeeId(currentUserId);
        return assignmentRepository.countByEmployeeIdAndStatus(currentUserId, status);
    }


    public Assignment get(String id, String token) throws MapleException{
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(id);
        if (!assignmentOptional.isPresent()) throw new NotFoundException(ASSIGNMENT);
        Assignment assignment = assignmentOptional.get();
        String currentUserId = getCurrentUserId(token);

        if (!assignment.getEmployeeId().equals(currentUserId) && !adminService.isExist(currentUserId)){
            throw new NotFoundException(ASSIGNMENT);
        }
        return assignment;
    }

    // buat assignment page
    public long getTotalObjectByUser(String token, String status) {
        String currentUserId = getCurrentUserId(token);
        Iterator<Employee> employees;
        if (adminService.isExist(currentUserId)) employees = employeeService.getBySuperiorId(null).iterator();
        else employees = employeeService.getBySuperiorId(currentUserId).iterator();

        List<String> ids = new ArrayList<>();
        while (employees.hasNext()) {
            Employee e = employees.next();
            ids.add(e.getId());
        }
        if (!status.equals("all")) return assignmentRepository.countByStatusAndEmployeeIdIn(status, ids);
        return assignmentRepository.countByEmployeeIdIn(ids);
    }

    public long getTotalPages(long size, String token, String status) {
        return SimpleUtils.getTotalPages(size, getTotalObjectByUser(token, status));
    }

    public void assignMany(ManyAssignmentRequest manyAssignmentRequest, String token) throws MapleException {
        List<Item> items = new ArrayList<>();
        String currentUserId = getCurrentUserId(token);
        for (Assignment assignment : manyAssignmentRequest.getValue()) {
            validate(assignment);

            Item item = itemService.get(assignment.getItemSku());
            int quantity = assignment.getQuantity();
            if (item.getQuantity() < quantity)
                throw new MapleException("Item doesn't have enough quantity", HttpStatus.BAD_REQUEST);
            item.setQuantity(item.getQuantity()-quantity);
            items.add(item);
            assignment.setCreatedDate(new Date());

            assignment.setEmployeeId(currentUserId);
            assignment.setCreatedBy(currentUserId);
            //give id
            assignment.setAssignmentId(counter.getNextAssignment());

            //default value
            assignment.setStatus("PENDING");
        }
        itemService.updateManyItemQuantity(items);
        assignmentRepository.saveAll(manyAssignmentRequest.getValue());
    }

    // action = up/down (for state action)
    public Assignment updateStatus(String id, String action, String token) throws MapleException{
        Optional<Assignment> assignmentObj = assignmentRepository.findById(id);
        if (!assignmentObj.isPresent()) throw new NotFoundException(id);

        Assignment assignment = assignmentObj.get();
        String currentUserId = getCurrentUserId(token);

        // only his superior
        employeeService.onlyTheirSuperior(assignment.getEmployeeId(), currentUserId);

        //to decide whether increase or decrease state
        if (action.equalsIgnoreCase("UP"))
            assignment.setStatus(increaseStatus(assignment.getStatus(), assignment));
        else if(action.equalsIgnoreCase("DOWN"))
            assignment.setStatus(decreaseStatus(assignment.getStatus()));
        else throw new MapleException("Method isn't recognized", HttpStatus.METHOD_NOT_ALLOWED);

        assignment.setUpdatedDate(new Date());
        assignment.setUpdatedBy(currentUserId);
        return assignmentRepository.save(assignment);
    }
    // implement state design pattern
    // status won't change if its state is 'complete'
    private String increaseStatus(String status, Assignment assignment) {
        if (status.equals("PENDING"))
            return "APPROVED";
        else if (status.equals("APPROVED"))
            return "RECEIVED";
        return status;
    }
    private String decreaseStatus(String status) {
        if (status.equals("PENDING")) return "REJECTED";
        else if (status.equals("RECEIVED")) return "RETURNED";
        return status;
    }


    public void updateByEmployee(List<String> ids) {
        List<Assignment> assignments;
        for (String id : ids ) {
            assignments = assignmentRepository.findByEmployeeId(id);
            //return the item and delete assignment
            if (!assignments.isEmpty()) {
                for (Assignment assignment : assignments) {
                    itemService.returnItem(assignment.getItemSku(), assignment.getQuantity());
                    assignment.setStatus("Employee has been removed");
                    assignment.setUpdatedDate(new Date());
                }
            }
        }
    }

    public void updateByItem(List<String> ids) {
        List<Assignment> assignments;
        for (String id: ids) {
            assignments = assignmentRepository.findByItemSku(id);
            if (!assignments.isEmpty()) {
                for (Assignment assignment : assignments) {
                    assignment.setStatus("Item has been removed");
                    assignment.setUpdatedDate(new Date());
                }
            }
        }
    }

    public List<String> getButtonByStatus(String status) {
        List<String>button = new ArrayList<>();
        if (status == null) return null;
        if (status.equals("PENDING")) {
            button.add("btnApprove");
            button.add("btnReject");
        }
        else if (status.equals("APPROVED")) {
            button.add("btnHandover");
        }
        else if (status.equals("RECEIVED")) {
            button.add("btnReturn");
        }
        else return null;
        return button;
    }

    public String getItemName(String itemSku) throws MapleException{
        return itemService.get(itemSku).getName();
    }

    public String getEmployeeName(String employeeId) throws MapleException{
        return employeeService.get(employeeId).getUsername();
    }

    private void validate(Assignment assignment) throws DataConstraintException{
        ArrayList errorMessage = new ArrayList();

        if (!employeeService.isExist(assignment.getEmployeeId()))
            errorMessage.add("Employee doesn't exist");
        if (!itemService.isExist(assignment.getItemSku()))
            errorMessage.add("Item doesn't exist");
        if (assignment.getQuantity() <= 0)
            errorMessage.add("Minimal quantity is 1");

        if (!errorMessage.isEmpty()) throw new DataConstraintException(errorMessage.toString());
    }
}
