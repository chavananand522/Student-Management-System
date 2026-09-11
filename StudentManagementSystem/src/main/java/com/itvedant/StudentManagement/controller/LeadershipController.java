package com.itvedant.StudentManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LeadershipController {

    @GetMapping("/leadership")
    public String leadership() {
        return "leadership";
    }
}