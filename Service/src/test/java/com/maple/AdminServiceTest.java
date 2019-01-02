package com.maple;

import com.maple.MockingObject.FakeObjectFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = {AdminService.class})
public class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private AdminRepository adminRepository;

    @Test
    public void getAdminByUsername() {
        Admin admin = FakeObjectFactory.getFakeAdmin();
        when(adminRepository.findByUsername(admin.getUsername())).thenReturn(admin);

        assertEquals(admin, adminService.getByUsername(admin.getUsername()));
    }

    @Test
    public void checkAdminUsernameExisting() {
        Admin admin = FakeObjectFactory.getFakeAdmin();
        when(adminRepository.findByUsername(admin.getUsername())).thenReturn(admin);

        assertFalse(adminService.isExist("xyz"));
        assertTrue(adminService.isExist(admin.getUsername()));
    }

}
