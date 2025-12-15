package com.leojcl.recruitmentsystem.controller;

import com.leojcl.recruitmentsystem.dto.user.RegisterFormDto;
import com.leojcl.recruitmentsystem.entity.Users;
import com.leojcl.recruitmentsystem.entity.UsersType;
import com.leojcl.recruitmentsystem.service.UserService;
import com.leojcl.recruitmentsystem.service.UsersTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class UsersController {

    private final UsersTypeService usersTypeService;
    private final UserService userService;

    @Autowired
    public UsersController(UsersTypeService usersTypeService, UserService userService) {
        this.usersTypeService = usersTypeService;
        this.userService = userService;
    }


    @GetMapping("/register")
    public String register(Model model) {
        List<UsersType> usersTypes = usersTypeService.getAll();
        model.addAttribute("getAllTypes", usersTypes);
        model.addAttribute("form", new RegisterFormDto());

        return "register";
    }

    @PostMapping("/register/new")
    public String userRegistration(@Valid @ModelAttribute("form") RegisterFormDto reg, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            populateModelWithUserTypes(model);
            return "register";
        }

        Optional<Users> optionalUsers = userService.getUserByEmail(reg.getEmail());

        if (optionalUsers.isPresent()) {
            model.addAttribute("error", "Email already registered, try to login or register with other mail.");
            populateModelWithUserTypes(model);
            return "register";
        }

        Users user = new Users();
        user.setEmail(reg.getEmail());
        user.setPassword(reg.getPassword());
        user.setActive(true);
        user.setRegistrationDate(new Date());

        UsersType usersType = usersTypeService.getById(reg.getUserTypeId()).orElseThrow(() -> new IllegalArgumentException("Invalid userTypeId"));
        user.setUserType(usersType);

        userService.addNew(user);

        return "redirect:/dashboard";
    }

    public void populateModelWithUserTypes(Model model) {
        List<UsersType> usersTypes = usersTypeService.getAll();
        model.addAttribute("getAllTypes", usersTypes);
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
