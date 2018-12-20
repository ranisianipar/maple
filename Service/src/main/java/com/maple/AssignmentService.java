package com.maple;

import com.maple.Exception.DataConstraintException;
import com.maple.Exception.MapleException;
import com.maple.Exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

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


    public List<Assignment> getAllAssignments(Pageable pageable) {
        return assignmentRepository.findAll(pageable).getContent();
    }

    public Assignment getAssignment (String id) throws DataConstraintException{
        //check id exist or nah
        Optional<Assignment> assignment = assignmentRepository.findById(id);
        if (!assignment.isPresent()) throw new DataConstraintException("Assignment doesn't exist");

        return assignment.get();
    }
    public List<Assignment> getByEmployee(String employeeId) throws NotFoundException{
        if (!employeeService.isExist(employeeId)) throw new NotFoundException("Employee ID");
        return assignmentRepository.findByEmployeeId(employeeId);
    }

    public long getTotalAssignment() {return SimpleUtils.getTotalObject(assignmentRepository);}
    public long getTotalPage(long size) {return SimpleUtils.getTotalPages(size, getTotalAssignment());}

    public Assignment createAssignment(Assignment assignment) throws MapleException, IOException {
        //check EmployeeId and ItemSku are valid
        validate(assignment);

        //check the item has enough quantity
        Item item = itemService.get(assignment.getItemSku());
        if (item.getQuantity()-assignment.getQuantity() < 0)
            throw new DataConstraintException ("Item doesn't have enough quantity");

        assignment.setAssignmentId(counter.getNextAssignment());

        //default value
        assignment.setStatus("PENDING");

        //update item's quantity
        item.setQuantity(item.getQuantity()-assignment.getQuantity());
        itemService.update(item.getItemSku(), item, null);

        System.out.println(getAllAssignments(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdDate"))
        ));

        //return the new assignment
        return assignmentRepository.save(assignment);
    }

    // action = up/down (for state action)
    public Assignment updateStatus(String id, String action) throws MapleException{
        Optional<Assignment> assignmentObj = assignmentRepository.findById(id);
        if (!assignmentObj.isPresent()) throw new NotFoundException(id);

        Assignment assignment = assignmentObj.get();

        //to decide whether increase or decrease state
        if (action.equalsIgnoreCase("UP"))
            assignment.setStatus(increaseStatus(assignment.getStatus(), assignment));
        else if(action.equalsIgnoreCase("DOWN"))
            assignment.setStatus(decreaseStatus(assignment.getStatus()));
        else throw new MapleException("Method isn't recognized", HttpStatus.METHOD_NOT_ALLOWED);

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

    public void assignMany(RequestAssignment requestAssignment) throws MapleException {
        List<Item> items = new ArrayList<>();
        for (Assignment assignment : requestAssignment.getValue()) {
            Item item = itemService.get(assignment.getItemSku());
            int quantity = assignment.getQuantity();
            if (item.getQuantity() < quantity)
                throw new MapleException("Item quantity not enough", HttpStatus.BAD_REQUEST);
            item.setQuantity(item.getQuantity()-quantity);
            items.add(item);
        }
        itemService.updateManyItemQuantity(items);

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
                }
            }
        }
    }

    public List<String> getButtonByStatus(String status) {
        List<String>button = new ArrayList<>();
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

    private void validate(Assignment assignment) throws DataConstraintException{
        ArrayList errorMessage = new ArrayList();

        if (!employeeService.isExist(assignment.getEmployeeId()))
            errorMessage.add("Employee doesn't exist");
        if (!itemService.isExist(assignment.getItemSku()))
            errorMessage.add("Item doesn't exist");

        if (!errorMessage.isEmpty()) throw new DataConstraintException(errorMessage.toString());
    }
}
