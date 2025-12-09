package com.leojcl.recruitmentsystem.controller;

import com.leojcl.recruitmentsystem.entity.JobSeekerProfile;
import com.leojcl.recruitmentsystem.entity.Skill;
import com.leojcl.recruitmentsystem.entity.Users;
import com.leojcl.recruitmentsystem.repository.UsersRepository;
import com.leojcl.recruitmentsystem.service.JobSeekerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/job-seeker-profile")
public class JobSeekerProfileController {
    private JobSeekerProfileService jobSeekerProfileService;
    private UsersRepository usersRepository;

    @Autowired
    public JobSeekerProfileController(JobSeekerProfileService jobSeekerProfileService, UsersRepository usersRepository) {
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.usersRepository = usersRepository;
    }

    @GetMapping("/")
    public String jobSeekerProfile(Model model) {
        JobSeekerProfile jobSeekerProfile = new JobSeekerProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Users user = usersRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("Could not found user"));

            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(user.getUserId());
            if (seekerProfile.isPresent()) {
                jobSeekerProfile = seekerProfile.get();

                if (jobSeekerProfile.getSkills().isEmpty()) {
                    List<Skill> skills = new ArrayList<>();
                    skills.add(new Skill());
                    jobSeekerProfile.setSkills(skills);
                }
            }
            model.addAttribute("skills", jobSeekerProfile.getSkills());
            model.addAttribute("profile", jobSeekerProfile);
        }

        return "job-seeker-profile";
    }
}
