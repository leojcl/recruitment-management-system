package com.leojcl.recruitmentsystem.controller;

import com.leojcl.recruitmentsystem.entity.*;
import com.leojcl.recruitmentsystem.exception.ResourceNotFoundException;
import com.leojcl.recruitmentsystem.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class JobSeekerApplyController {
    private final JobPostActivityService jobPostActivityService;
    private final UserService userService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final JobSeekerSaveService jobSeekerSaveService;
    private final RecruiterProfileService recruiterProfileService;
    private final JobSeekerProfileService jobSeekerProfileService;

    @Autowired
    public JobSeekerApplyController(JobPostActivityService jobPostActivityService, UserService userService, JobSeekerApplyService jobSeekerApplyService, JobSeekerSaveService jobSeekerSaveService, RecruiterProfileService recruiterProfileService, JobSeekerProfileService jobSeekerProfileService) {
        this.jobPostActivityService = jobPostActivityService;
        this.userService = userService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobSeekerSaveService = jobSeekerSaveService;
        this.recruiterProfileService = recruiterProfileService;
        this.jobSeekerProfileService = jobSeekerProfileService;
    }

    @GetMapping("job-details-apply/{id}")
    public String display(@PathVariable("id") int id, Model model) {
        JobPostActivity jobDetails = jobPostActivityService.getOne(id);
        List<JobSeekerApply> applies = jobSeekerApplyService.getJobCandidates(jobDetails);
        List<JobSeekerSave> saves = jobSeekerSaveService.getJobsCandidates(jobDetails);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            model.addAttribute("username", authentication.getName());

            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
                RecruiterProfile recruiter = recruiterProfileService.getCurrentRecruiterProfile();
                if (recruiter != null) {

                    model.addAttribute("displayName",
                            recruiter.getFirstName() != null && recruiter.getLastName() != null
                                    ? recruiter.getFirstName() + " " + recruiter.getLastName()
                                    : authentication.getName());
                    model.addAttribute("avatarPath", recruiter.getPhotosImagePath());

                    model.addAttribute("applyList", applies);
                }
            } else {
                JobSeekerProfile seeker = jobSeekerProfileService.getCurrentSeekerProfile();
                if (seeker != null) {

                    model.addAttribute("displayName",
                            seeker.getFirstName() != null && seeker.getLastName() != null
                                    ? seeker.getFirstName() + " " + seeker.getLastName()
                                    : authentication.getName());
                    model.addAttribute("avatarPath", seeker.getPhotosImagePath());

                    Integer uid = seeker.getUserAccountId();

                    boolean alreadyApplied = applies.stream()
                            .anyMatch(a -> Objects.equals(a.getUserId().getUserAccountId(), uid));
                    boolean alreadySaved = saves.stream()
                            .anyMatch(s -> Objects.equals(s.getUserId().getUserAccountId(), uid));

                    model.addAttribute("alreadyApplied", alreadyApplied);
                    model.addAttribute("alreadySaved", alreadySaved);

                }
            }
        }

        model.addAttribute("applyJob", new JobSeekerApply());
        model.addAttribute("jobDetails", jobDetails);

        return "job-details";
    }

    @PostMapping("job-details/apply/{id}")
    public String apply(@PathVariable("id") int id, JobSeekerApply jobSeekerApply) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users users = userService.findByEmail(currentUsername);
            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(users.getUserId());
            JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);

            if (seekerProfile.isPresent() && jobPostActivity != null) {
                jobSeekerApply = new JobSeekerApply();
                jobSeekerApply.setUserId(seekerProfile.get());
                jobSeekerApply.setJob(jobPostActivity);
                jobSeekerApply.setApplyDate(new Date());
            } else {
                throw new ResourceNotFoundException("User not found");
            }
            if (jobSeekerApplyService.alreadyApplied(seekerProfile.get(), jobPostActivity)) {
                return "redirect:/job-details-apply/" + id;
            }
            jobSeekerApplyService.addNew(jobSeekerApply);
        }

        return "redirect:/dashboard";
    }
}
