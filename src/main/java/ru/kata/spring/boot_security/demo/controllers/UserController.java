package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.services.UserServiceImp;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {
    private UserServiceImp userService;
    @Autowired
    public UserController(UserServiceImp userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String getUser(Principal principal, Model model) {
        model.addAttribute("user_data", userService.findByUsername(principal.getName()));
        return "user";
    }
}
