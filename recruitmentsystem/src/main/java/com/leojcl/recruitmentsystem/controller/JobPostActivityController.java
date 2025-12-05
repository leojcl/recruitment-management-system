package com.leojcl.recruitmentsystem.controller;

import com.leojcl.recruitmentsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Controller
public class JobPostActivityController {
    private final UserService userService;

    @Autowired
    public JobPostActivityController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String searchJobs(Model model){
        Object currentUserProfile = userService.getCurrentUserProfile();
        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            assert authentication != null;
            String currentUsername =  authentication.getName();
            model.addAttribute("username", currentUsername);
        }
        model.addAttribute("user", currentUserProfile);
        return "dashboard";
    }
}
