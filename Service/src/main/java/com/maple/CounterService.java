package com.maple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CounterService {
    @Autowired
    private IdCounterRepository counterRepository;

    public String getNextEmployee() {
        IdCounter counter = IdCounter.getInstance();

        long id = counter.getEmployeeId();
        counter.setEmployeeId(id + 1);
        counterRepository.save(counter);
        return "EMP-"+id;
    }

}
