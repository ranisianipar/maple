package com.maple;

import com.maple.MockingObject.MockHttpSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpSession;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(AuthControllerTest.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    public void loginSucceed() throws Exception {

        LoginRequest loginRequest = new LoginRequest();
        HttpSession session = new MockHttpSession();
        loginRequest.setUsername("EMP");
        loginRequest.setPassword("EMP");
//        when(authService.getValidToken(loginRequest, session)).thenReturn(session.getId());
//        // gimana passing loginRequest ke requestBodynya
//        this.mockMvc.perform(post(Constant.LINK_LOGIN, loginRequest))
//                .andExpect(status().isOk())
//                .andExpect(content().string(containsString(session.getId())));
    }
}
