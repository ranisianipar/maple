package com.maple;


import javax.servlet.http.*;

public class ExampleServlet extends HttpServlet {

//    public void doPost(HttpServletRequest req, HttpServletResponse res) {
//        res.addCookie(new Cookie("token","xyz"));
//    }
    public void service(HttpServletRequest req, HttpServletResponse res) {
        HttpSession httpSession = req.getSession(false);

    }
}
