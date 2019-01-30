package com.example.demo.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.entity.Msg;

@Controller
public class HomeController {
	
	    @RequestMapping("/")
	    public String home(Model model){
	        Msg msg =  new Msg("测试标题","测试内容","欢迎来到HOME页面,您拥有 ROLE_HOME 权限");
	        model.addAttribute("msg", msg);
	        return "home";
	    }

	    @RequestMapping("/login")
	    public  String login(){
	        return "login";
	    }

	    @RequestMapping("/admin")
	    @ResponseBody
	    public String hello(){
	        return "hello admin";
	    }
	    
		@RequestMapping("logout")
	    public void logout(HttpSession session,HttpServletResponse response){
	        session.invalidate();
	        try {
				response.sendRedirect("http://localhost:8080/cas/logout");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

}
