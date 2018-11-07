package com.maple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CounterService {
    @Autowired
    private IdCounterRepository counterRepository;
    private final IdCounter counter = new IdCounter();

    public String getNextEmployee() {
        if (counterRepository.count() == 0) counterRepository.save(counter);

        long id = counterRepository.findFirst().getEmployeeId();
        counter.setEmployeeId(id+1);
        counterRepository.save(counter);
        return "EMP-"+id;
    }
    public String getNextItem() {
        if (counterRepository.count() == 0) counterRepository.save(counter);

        long id = counterRepository.findFirst().getItemId();
        counter.setItemId(id+1);
        counterRepository.save(counter);
        return "ITEM-"+id;
    }
}
