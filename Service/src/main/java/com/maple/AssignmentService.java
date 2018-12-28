package com.maple;

import com.maple.Exception.DataConstraintException;
import com.maple.Exception.MapleException;
import com.maple.Exception.NotFoundException;
import com.maple.Helper.SimpleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;


import static com.maple.AuthService.getCurrentUserId;

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

    // User as a superior
    public List<Assignment> getRequestedAssignment(Pageable pageable, HttpSession httpSession) {
        if (adminService.isExist(httpSession.getAttribute("token").toString()))
            // cari yang superiornya null
            return assignmentRepository.findByEmployeeId(null, pageable);
        Iterator employees = employeeService.getBySuperiorId(getCurrentUserId(httpSession)).iterator();
        List<String> ids = new ArrayList<>();
        while (employees.hasNext()) {
            Employee e = (Employee) employees.next();
            ids.add(e.getId());
        }
        return assignmentRepository.findByEmployeeIdIn(ids, pageable);
    }

    // User as a requestor
    public List<Assignment> getAll(Pageable pageable, HttpSession httpSession) throws MapleException{
        //admin
        if (adminService.isExist(httpSession.getAttribute("token").toString()))
            return assignmentRepository.findAll(pageable).getContent();
        return assignmentRepository.findByEmployeeId(getCurrentUserId(httpSession), pageable);
    }

    public Assignment get(String id, HttpSession httpSession) throws MapleException{
        //check id exist or nah

        Optional<Assignment> assignmentOptional = assignmentRepository.findById(id);
        if (!assignmentOptional.isPresent()) throw new NotFoundException("Assignment");
        Assignment assignment = assignmentOptional.get();
        String currentUserId = getCurrentUserId(httpSession);

        // harusnya kalo admin bole2 aja
        if (!assignment.getEmployeeId().equals(currentUserId) || !adminService.isExist(currentUserId))
            throw new NotFoundException("Assignment");
        return assignment;
    }
    public List<Assignment> getByEmployee(String employeeId, Pageable pageable) throws NotFoundException{
        if (!employeeService.isExist(employeeId)) throw new NotFoundException("Employee ID");
        return assignmentRepository.findByEmployeeId(employeeId, pageable);
    }

    public long getTotalObject() {return SimpleUtils.getTotalObject(assignmentRepository);}
    public long getTotalPage(long size) {return SimpleUtils.getTotalPages(size, getTotalObject());}

    public void assignMany(ManyAssignmentRequest manyAssignmentRequest, HttpSession httpSession) throws MapleException {
        List<Item> items = new ArrayList<>();
        for (Assignment assignment : manyAssignmentRequest.getValue()) {
            validate(assignment);

            Item item = itemService.get(assignment.getItemSku());
            int quantity = assignment.getQuantity();
            if (item.getQuantity() < quantity)
                throw new MapleException("Item doesn't have enough quantity", HttpStatus.BAD_REQUEST);
            item.setQuantity(item.getQuantity()-quantity);
            items.add(item);
            assignment.setCreatedDate(new Date());

            assignment.setEmployeeId(getCurrentUserId(httpSession));

            //give id
            assignment.setAssignmentId(counter.getNextAssignment());

            //default value
            assignment.setStatus("PENDING");
        }
        itemService.updateManyItemQuantity(items);
        assignmentRepository.saveAll(manyAssignmentRequest.getValue());
    }

    // action = up/down (for state action)
    public Assignment updateStatus(String id, String action, HttpSession httpSession) throws MapleException{
        Optional<Assignment> assignmentObj = assignmentRepository.findById(id);
        if (!assignmentObj.isPresent()) throw new NotFoundException(id);

        Assignment assignment = assignmentObj.get();
        String currentUserId = getCurrentUserId(httpSession);

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

        if (!errorMessage.isEmpty()) throw new DataConstraintException(errorMessage.toString());
    }
}
