package com.kumarmangalam.ticketManagementSystem;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Helper {
    @GetMapping("/")
    String helloWorld(){
        return "hello";
    }
    
}
