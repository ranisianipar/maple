package com.maple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CounterService {
    @Autowired
    private IdCounterRepository counterRepository;
    private final IdCounter counter = new IdCounter();

    private void init() {
        if (counterRepository.count() == 0) counterRepository.save(counter);
    }

    public String getNextEmployee() {
        init();

        long id = counterRepository.findFirst().getEmployeeId();
        counter.setEmployeeId(id+1);
        counterRepository.save(counter);
        return "EMP-"+id;
    }
    public String getNextItem() {
        init();

        long id = counterRepository.findFirst().getItemId();
        counter.setItemId(id+1);
        counterRepository.save(counter);
        return "ITM-"+id;
    }

    public String getNextAssignment() {
        init();

        long id = counterRepository.findFirst().getAssignmentId();
        counter.setAssignmentId(id+1);
        counterRepository.save(counter);
        return "ASG-"+id;
    }
}
