package com.itvedant.StudentManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IdCardController {

    @GetMapping("/id-card")
    public String idCard(Model model) {

        model.addAttribute("adminName", "Anand Chavan");

        return "idcard";
    }
}