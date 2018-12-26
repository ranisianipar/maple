package com.maple.Mongobee;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.maple.Admin;
import com.maple.AdminRepository;
import com.maple.IdCounter;
import com.maple.IdCounterRepository;

@ChangeLog
public class MongoChangeLog {

    @ChangeSet(order = "001", id = "addIdCounter", author = "admin")
    public void addIdCounter(final IdCounterRepository counterRepository) {
        final IdCounter idCounter = new IdCounter();
    }

    @ChangeSet(order = "002", id = "createAdmin", author = "admin")
    public void addAdmin(final AdminRepository adminRepository) {
        final Admin admin = new Admin("admin", "admin");
        adminRepository.save(admin);
    }
}
