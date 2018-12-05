package com.maple;

import com.maple.Exception.DataConstraintException;
import com.maple.Exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    AssignmentRepository assignmentRepository;

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

    public Assignment createAssignment(Assignment assignment) throws DataConstraintException{
        //check EmployeeId and ItemSku are valid
        validate(assignment);

        //check the item has enough quantity
        Item item = itemRepository.findById(assignment.getItemSku()).get();
        if (item.getQuantity()-assignment.getQuantity() < 0)
            throw new DataConstraintException ("Item doesn't have enough quantity");

        assignment.setAssignmentId(counter.getNextAssignment());
        //update item's quantity
        item.setQuantity(item.getQuantity()-assignment.getQuantity());
        itemRepository.save(item);

        //return the new assignment
        return assignmentRepository.save(assignment);
    }

    public Assignment updateAssignment(String id, Assignment newAssignment)
            throws DataConstraintException, NotFoundException{

        validate(newAssignment);
        //get assignment
        //check id exist or nah
        Optional<Assignment> assignmentObj = assignmentRepository.findById(id);
        if(!assignmentObj.isPresent()) throw new NotFoundException("Assignment");

        /*add or substract item's quantity on its domain
        * Item quantity should be (+)
        */
        Assignment assignment = assignmentObj.get();
        Item newItem = itemRepository.findById(newAssignment.getItemSku()).get();
        if (newItem.getQuantity()-newAssignment.getQuantity() < 0) {
            throw new DataConstraintException("Item doesn't have enough quantity");
        }
        Item oldItem = itemRepository.findById(assignment.getItemSku()).get();
        oldItem.setQuantity(oldItem.getQuantity()+assignment.getQuantity());
        assignment.setQuantity(newAssignment.getQuantity());
        newItem.setQuantity(newItem.getQuantity()-newAssignment.getQuantity());

        //update the assignment value
        assignment.setEmployeeId(newAssignment.getEmployeeId());
        assignment.setItemSku(newAssignment.getItemSku());
        assignment.setStatus(newAssignment.getStatus());
        assignment.setNote(newAssignment.getNote());
        assignment.setUpdatedBy(newAssignment.getUpdatedBy());
        assignment.setUpdatedDate(new Date());

        //update item's quantity
        itemRepository.save(oldItem);
        itemRepository.save(newItem);

        //return the latest assignment
        return assignmentRepository.save(assignment);
    }

    public void deleteAssignment(String id) throws NotFoundException{
        //get assignment
        //check id exist or nah
        Optional<Assignment> assignmentObj = assignmentRepository.findById(id);
        if (!assignmentObj.isPresent())
            throw new NotFoundException("Assignment");
        Assignment assignment = assignmentObj.get();

        //put the quantity back to the item
        Item item = itemRepository.findById(assignment.getItemSku()).get();
        item.setQuantity(item.getQuantity()+assignment.getQuantity());
        itemRepository.save(item);

        //remove
        assignmentRepository.delete(assignment);
    }

    public void deleteAllAssignment() {
        assignmentRepository.deleteAll();
    }

    private void validate(Assignment assignment) throws DataConstraintException{
        ArrayList errorMessage = new ArrayList();

        if (!employeeRepository.findById(assignment.getEmployeeId()).isPresent())
            errorMessage.add("Employee doesn't exist");
        if (!itemRepository.findById(assignment.getItemSku()).isPresent())
            errorMessage.add("Item doesn't exist");

        if (!errorMessage.isEmpty()) throw new DataConstraintException(errorMessage.toString());
    }
}
