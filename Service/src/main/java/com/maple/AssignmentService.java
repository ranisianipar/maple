package com.maple;

import com.maple.Exception.DataConstraintException;
import com.maple.Exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    public long getTotalAssignment() {return SimpleUtils.getTotalObject(assignmentRepository);}
    public long getTotalPage(long size) {return SimpleUtils.getTotalPages(size, getTotalAssignment());}

    public Assignment createAssignment(Assignment assignment) throws NotFoundException, DataConstraintException, IOException {
        //check EmployeeId and ItemSku are valid
        validate(assignment);

        //check the item has enough quantity
        Item item = itemService.get(assignment.getItemSku());
        if (item.getQuantity()-assignment.getQuantity() < 0)
            throw new DataConstraintException ("Item doesn't have enough quantity");

        assignment.setAssignmentId(counter.getNextAssignment());

        //update item's quantity
        item.setQuantity(item.getQuantity()-assignment.getQuantity());
        itemService.update(item.getItemSku(), item, null);

        //return the new assignment
        return assignmentRepository.save(assignment);
    }
// ulang aja
    public Assignment updateAssignment(String id, Assignment newAssignment)
            throws DataConstraintException, NotFoundException, IOException{

        validate(newAssignment);
        //get assignment
        //check id exist or nah
        Optional<Assignment> assignmentObj = assignmentRepository.findById(id);
        if(!assignmentObj.isPresent()) throw new NotFoundException("Assignment");

        /*
        * add or substract item's quantity on its domain
        * Item quantity should be (+)
        */
        Assignment oldAssignment = assignmentObj.get();

        if (itemService.get(newAssignment.getItemSku()).getQuantity()-newAssignment.getQuantity() < 0) { //newItem q
            throw new DataConstraintException("Item doesn't have enough quantity");
        }

        itemService.returnItem(oldAssignment.getItemSku(), oldAssignment.getQuantity());

        // refresh item yg udah dikembaliin (karna bisa jadi item yg diupdate sama)
        Item newItem = itemService.get(newAssignment.getItemSku());
        // update the quantity
        oldAssignment.setQuantity(newAssignment.getQuantity());
        // substract new item quantity
        newItem.setQuantity(newItem.getQuantity()-oldAssignment.getQuantity());

        //update the assignment value
        oldAssignment.setEmployeeId(newAssignment.getEmployeeId());
        oldAssignment.setItemSku(newAssignment.getItemSku());
        oldAssignment.setStatus(newAssignment.getStatus());
        oldAssignment.setNote(newAssignment.getNote());
        oldAssignment.setUpdatedBy(newAssignment.getUpdatedBy());
        oldAssignment.setUpdatedDate(new Date());

        //update item's quantity
        itemService.update(newItem.getItemSku(), newItem, null);

        //return the latest assignment
        return assignmentRepository.save(oldAssignment);
    }

    public void deleteAssignment(String id) throws NotFoundException{
        //get assignment
        //check id exist or nah
        Optional<Assignment> assignmentObj = assignmentRepository.findById(id);
        if (!assignmentObj.isPresent())
            throw new NotFoundException("Assignment");
        Assignment assignment = assignmentObj.get();

        //put the quantity back to the item
        itemService.returnItem(assignment.getItemSku(), assignment.getQuantity());

        //remove
        assignmentRepository.delete(assignment);
    }

    public void deleteAllAssignment() {
        assignmentRepository.deleteAll();
    }

    public void deleteByEmployee(List<String> ids) {
        List<Assignment> assignments;
        for (String id : ids ) {
            assignments = assignmentRepository.findByEmployeeId(id);
            //return the item and delete assignment
            if (!assignments.isEmpty()) {
                for (Assignment assignment : assignments) {
                    itemService.returnItem(assignment.getItemSku(), assignment.getQuantity());
                    assignmentRepository.delete(assignment);
                }
            }
        }
    }

    public void deleteByItem(List<String> ids) {
        List<Assignment> assignments;
        for (String id: ids) {
            assignments = assignmentRepository.findByItemSku(id);
            if (!assignments.isEmpty()) {
                for (Assignment assignment : assignments) {
                    assignmentRepository.delete(assignment);
                }
            }
        }
    }

    public String getButtonByStatus(String status) {
        if (status.equals("PENDING")) {
            List<String> button = new ArrayList<>();
            button.add("bthApprove");
            button.add("btnReject");
            return button.toString();
        }
        else if (status.equals("APPROVED")) {
            return "btnHandover";
        }
        else if (status.equals("RECEIVED")) {
            return "btnReturn";
        }
        else return null;
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
