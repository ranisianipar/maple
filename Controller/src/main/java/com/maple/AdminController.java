package com.maple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = Constant.LINK_ORIGIN)
@RequestMapping(value = "/admin")
@RestController
public class AdminController {

    @Autowired
    AdminService adminService;

    @DeleteMapping
    public void delete(){
        adminService.deleteAdmin();
    }
}
