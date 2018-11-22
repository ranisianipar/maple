package com.maple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService {
    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    private CounterService counter;

    public List<Assignment> getAllAssignments() {

        return assignmentRepository.findAll();
    }

    public Assignment getAssignment (String id) {
        //check id exist or nah
        return null;
    }

    public Assignment createAssignment() {
        //check EmployeeId and ItemSku Not null
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
