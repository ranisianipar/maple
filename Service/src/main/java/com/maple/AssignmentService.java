package com.maple;

import com.maple.Exception.DataConstraintException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {
    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    private CounterService counter;

    public List<Assignment> getAllAssignments() {
        // need paging
        return assignmentRepository.findAll();
    }

    public Assignment getAssignment (String id) throws DataConstraintException{
        //check id exist or nah
        Optional<Assignment> assignment = assignmentRepository.findById(id);
        if (!assignment.isPresent()) throw new DataConstraintException("Assignment doesn't exist");

        return assignment.get();
    }

    public Assignment createAssignment(Assignment assignment) {
        //check EmployeeId and ItemSku are valid


        //check the item has enough quantity
        //return the new assignment
        return null;
    }

    public Assignment updateAssignment(String id) {
        //get assignment
        //check id exist or nah
        /*add or substract item's quantity on its domain
        * Item quantity should be (+)
        */
        //return the latest assignment
        return null;
    }

    public void deleteAssignment(String id) {
        //get assignment
        //check id exist or nah
        //put the quantity back to its domain
        //remove
    }

    public void deleteAllAssignment() {
        assignmentRepository.deleteAll();
    }
}
