package com.kumarmangalam.ticketManagementSystem;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@Controller
public class Helper {
    @GetMapping("/")
    String helloWorld(){
        return "forward:/index.html";
    }
    
}
