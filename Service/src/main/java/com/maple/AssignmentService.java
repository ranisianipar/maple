package com.maple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssignmentService {
    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    private CounterService counter;
}
