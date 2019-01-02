package com.maple;

import com.maple.Exception.MapleException;
import com.maple.Exception.MethodNotAllowedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import static com.maple.Helper.SimpleUtils.getCurrentUserId;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    AuthService authService;

    public Admin getByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    public boolean isExist(String username) {
        if (adminRepository.findByUsername(username) != null)
            return true;
        return false;
    }

    public void onlyAdmin(String method, String token) throws MapleException{
        if(!isExist(getCurrentUserId(token)))
            throw new MethodNotAllowedException(method);
    }
}
