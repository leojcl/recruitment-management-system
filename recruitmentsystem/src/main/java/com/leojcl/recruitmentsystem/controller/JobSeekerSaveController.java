package com.leojcl.recruitmentsystem.controller;

import com.leojcl.recruitmentsystem.entity.JobPostActivity;
import com.leojcl.recruitmentsystem.entity.JobSeekerProfile;
import com.leojcl.recruitmentsystem.entity.JobSeekerSave;
import com.leojcl.recruitmentsystem.entity.Users;
import com.leojcl.recruitmentsystem.service.JobPostActivityService;
import com.leojcl.recruitmentsystem.service.JobSeekerProfileService;
import com.leojcl.recruitmentsystem.service.JobSeekerSaveService;
import com.leojcl.recruitmentsystem.service.UserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class JobSeekerSaveController {
    private final UserService userService;
    private final JobSeekerProfileService jobSeekerProfileService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerSaveService jobSeekerSaveService;


    public JobSeekerSaveController(UserService userService, JobSeekerProfileService jobSeekerProfileService, JobPostActivityService jobPostActivityService, JobSeekerSaveService jobSeekerSaveService) {
        this.userService = userService;
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerSaveService = jobSeekerSaveService;
    }

    @PostMapping("job-details/save/{id}")
    public String save(@PathVariable("id") int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users user = userService.findByEmail(currentUsername);

            JobSeekerProfile seekerProfile = jobSeekerProfileService.getOne(user.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

            JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
            if (jobPostActivity == null) throw new RuntimeException("Job not found");
            JobSeekerSave save = new JobSeekerSave();
            save.setUserId(seekerProfile);
            save.setJob(jobPostActivity);

            jobSeekerSaveService.addNew(save);
        }
        return "redirect:/dashboard";
    }

    @GetMapping("saved-jobs/")
    public String saveJobs(Model model) {
        List<JobPostActivity> jobPost = new ArrayList<>();
        Object currentUserProfile = userService.getCurrentUserProfile();
        List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getCandidatesJobs((JobSeekerProfile) currentUserProfile);
        for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
            jobPost.add(jobSeekerSave.getJob());
        }

        model.addAttribute("jobPost", jobPost);
        model.addAttribute("user", currentUserProfile);
        return "saved-jobs";
    }
}
