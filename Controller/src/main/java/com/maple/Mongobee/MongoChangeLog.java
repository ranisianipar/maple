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
    public void addIdCounter(IdCounterRepository counterRepository) {
        final IdCounter idCounter = new IdCounter();
    }
    @ChangeSet(order = "002", id = "addAdmin", author = "admin")
    public void addAdmins(AdminRepository adminRepository) {
        final Admin organization = new Admin("admin", "admin");
        adminRepository.save(organization);
    }
}
