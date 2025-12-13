package com.leojcl.recruitmentsystem.controller;

import com.leojcl.recruitmentsystem.entity.RecruiterProfile;
import com.leojcl.recruitmentsystem.entity.Users;
import com.leojcl.recruitmentsystem.repository.UsersRepository;
import com.leojcl.recruitmentsystem.service.RecruiterProfileService;
import com.leojcl.recruitmentsystem.service.UserService;
import com.leojcl.recruitmentsystem.util.FileUploadUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/recruiter-profile")
public class RecruiterProfileController {
    private final UsersRepository usersRepository;
    private final RecruiterProfileService recruiterProfileService;
    private final UserService userService;

    public RecruiterProfileController(UsersRepository usersRepository, RecruiterProfileService recruiterProfileService, UserService userService) {
        this.usersRepository = usersRepository;
        this.recruiterProfileService = recruiterProfileService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String recruiterProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            Users user = userService.findByEmail(currentUserName);
            Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getOne(user.getUserId());
            recruiterProfile.ifPresent(profile -> model.addAttribute("profile", profile));
        }
        return "recruiter_profile";

    }

    @PostMapping("/addNew")
    public String addNew(RecruiterProfile recruiterProfile, @RequestParam("image") MultipartFile multipartFile, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users user = userService.findByEmail(currentUsername);

            recruiterProfile.setUserId(user);
            recruiterProfile.setUserAccountId(user.getUserId());
        }
        model.addAttribute("profile", recruiterProfile);
        String fileName = "";
        if (multipartFile != null && !multipartFile.isEmpty()) {
            fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            recruiterProfile.setProfilePhoto(fileName);

        }
        RecruiterProfile savedProfile = recruiterProfileService.addNew(recruiterProfile);

        String uploadDir = "photos/recruiter/" + savedProfile.getUserAccountId();

        try {
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/dashboard";
    }

}
