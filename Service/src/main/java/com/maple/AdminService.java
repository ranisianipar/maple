package com.maple;

import com.maple.Exception.MapleException;
import com.maple.Exception.MethodNotAllowedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import java.util.List;

import static com.maple.AuthService.getCurrentUserId;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    AuthService authService;

    public List<Admin> getAll() {
        initData();
        return adminRepository.findAll();
    }

    public void deleteAdmin() {
        // nanti dihapus kalo udah ga perlu
    }

    public Admin getByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    // dicek sama value dari jedis
    public boolean isExist(String username) {
        if (adminRepository.findByUsername(username) != null)
            return true;
        return false;
    }

    public void onlyAdmin(String method, HttpSession httpSession) throws MapleException{
        if(!isExist(getCurrentUserId(httpSession)))
            throw new MethodNotAllowedException(method);
    }

    public void initData() {
        List<Admin> admins = adminRepository.findAll();
        if (admins.isEmpty()){
            Admin admin = new Admin("admin","admin");
            adminRepository.save(admin);
        }
    }
}
