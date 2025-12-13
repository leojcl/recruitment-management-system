package com.leojcl.recruitmentsystem.controller;

import com.leojcl.recruitmentsystem.entity.JobPostActivity;
import com.leojcl.recruitmentsystem.entity.JobSeekerProfile;
import com.leojcl.recruitmentsystem.entity.RecruiterProfile;
import com.leojcl.recruitmentsystem.entity.Users;
import com.leojcl.recruitmentsystem.exception.ForbiddenException;
import com.leojcl.recruitmentsystem.exception.ResourceNotFoundException;
import com.leojcl.recruitmentsystem.service.JobPostActivityService;
import com.leojcl.recruitmentsystem.service.JobSeekerApplyService;
import com.leojcl.recruitmentsystem.service.JobSeekerSaveService;
import com.leojcl.recruitmentsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class JobPostActivityController {
    private final UserService userService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final JobSeekerSaveService jobSeekerSaveService;

    @Autowired
    public JobPostActivityController(UserService userService, JobPostActivityService jobPostActivityService, JobSeekerApplyService jobSeekerApplyService, JobSeekerSaveService jobSeekerSaveService) {
        this.userService = userService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobSeekerSaveService = jobSeekerSaveService;
    }

    private LocalDate resolveSearchDate(boolean today, boolean days7, boolean days30) {
        if (days30) return LocalDate.now().minusDays(30);
        if (days7) return LocalDate.now().minusDays(7);
        if (today) return LocalDate.now();
        return null;
    }

    private List<String> resolveJobTypes(String partTime, String fullTime, String freelance) {
        if (partTime == null && fullTime == null && freelance == null) {
            return List.of("Part-Time", "Full-Time", "Freelance");
        }
//        return Arrays.asList(partTime, fullTime, freelance);
        return Stream.of(partTime, fullTime, freelance)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<String> resolveRemoteTypes(String remoteOnly, String officeOnly, String partialRemote) {
        if (remoteOnly == null && officeOnly == null && partialRemote == null) {
            return List.of("Remote-Only", "Office-Only", "Partial-Remote");
        }
//        return Arrays.asList(remoteOnly, officeOnly, partialRemote);
        return Stream.of(remoteOnly, officeOnly, partialRemote)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void markAppliedAndSavedJobs(List<JobPostActivity> jobs, JobSeekerProfile seeker) {

        Set<Integer> appliedJobIds = jobSeekerApplyService
                .getCandidatesJobs(seeker)
                .stream()
                .map(a -> a.getJob().getJobPostId())
                .collect(Collectors.toSet());

        Set<Integer> savedJobIds = jobSeekerSaveService
                .getCandidatesJobs(seeker)
                .stream()
                .map(s -> s.getJob().getJobPostId())
                .collect(Collectors.toSet());

        for (JobPostActivity job : jobs) {
            job.setActive(appliedJobIds.contains(job.getJobPostId()));
            job.setSaved(savedJobIds.contains(job.getJobPostId()));
        }
    }

    @GetMapping("/dashboard")
    public String searchJobs(Model model, @RequestParam(value = "job", required = false) String job,
                             @RequestParam(value = "location", required = false) String location,
                             @RequestParam(value = "partTime", required = false) String partTime,
                             @RequestParam(value = "fullTime", required = false) String fullTime,
                             @RequestParam(value = "freelance", required = false) String freelance,
                             @RequestParam(value = "remoteOnly", required = false) String remoteOnly,
                             @RequestParam(value = "officeOnly", required = false) String officeOnly,
                             @RequestParam(value = "partialRemote", required = false) String partialRemote,
                             @RequestParam(value = "today", required = false, defaultValue = "false") boolean today,
                             @RequestParam(value = "days7", required = false, defaultValue = "false") boolean days7,
                             @RequestParam(value = "days30", required = false, defaultValue = "false") boolean days30) {

        model.addAttribute("partTime", Objects.equals(partTime, "Part-Time"));
        model.addAttribute("fullTime", Objects.equals(fullTime, "Full-Time"));
        model.addAttribute("freelance", Objects.equals(freelance, "Freelance"));

        model.addAttribute("remoteOnly", Objects.equals(remoteOnly, "Remote-Only"));
        model.addAttribute("officeOnly", Objects.equals(officeOnly, "Office-Only"));
        model.addAttribute("partialRemote", Objects.equals(partialRemote, "Partial-Remote"));

        model.addAttribute("today", today);
        model.addAttribute("days7", days7);
        model.addAttribute("days30", days30);

        model.addAttribute("job", job);
        model.addAttribute("location", location);

        LocalDate searchDate = resolveSearchDate(today, days7, days30);

        List<String> jobTypes = resolveJobTypes(partTime, fullTime, freelance);
        List<String> remoteTypes = resolveRemoteTypes(remoteOnly, officeOnly, partialRemote);

        List<JobPostActivity> jobs;

        boolean dateSearchFlag = true;
        if (!StringUtils.hasText(job) && !StringUtils.hasText(location) && searchDate == null
                && jobTypes.size() == 3
                && remoteTypes.size() == 3) {
            jobs = jobPostActivityService.getAll();
        } else {
            jobs = jobPostActivityService.search(job, location, jobTypes, remoteTypes, searchDate);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("jobPost", jobs);
            return "dashboard";
        }
        model.addAttribute("username", authentication.getName());

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
            RecruiterProfile recruiter = userService.getCurrentRecruiter()
                    .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

            model.addAttribute("displayName",
                    recruiter.getFirstName() != null && recruiter.getLastName() != null ?
                            recruiter.getFirstName() + " " + recruiter.getLastName() : authentication.getName()
            );

            model.addAttribute("avatarPath",
                    recruiter.getPhotosImagePath());
            model.addAttribute("user", recruiter);

            model.addAttribute("jobPost", jobs);
            return "dashboard";
        }
        JobSeekerProfile seeker = userService.getCurrentJobSeeker().orElseThrow(() -> new ResourceNotFoundException("Job Seeker not found"));
        model.addAttribute("displayName",
                seeker.getFirstName() != null && seeker.getLastName() != null ?
                        seeker.getFirstName() + " " + seeker.getLastName() : authentication.getName());

        model.addAttribute("avatarPath",
                seeker.getPhotosImagePath());
        model.addAttribute("user", seeker);

        markAppliedAndSavedJobs(jobs, seeker);
        model.addAttribute("jobPost", jobs);

        return "dashboard";
    }

    @GetMapping("/dashboard/add")
    public String addJobs(Model model) {
        model.addAttribute("jobPostActivity", new JobPostActivity());
        model.addAttribute("user", userService.getCurrentUserProfile());

        return "add-jobs";
    }

    @PreAuthorize("hasAuthority('Recruiter')")
    @GetMapping("jobs/{id}/edit")
    public String editJobForm(@PathVariable Integer id, Model model) {
        JobPostActivity job = jobPostActivityService.getOne(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = userService.findByEmail(authentication.getName());

        if (job.getPostedById().getUserId() != currentUser.getUserId()) {
            throw new ForbiddenException("FORBIDDEN");
        }
        model.addAttribute("job", job);
//        model.addAttribute("location");
        return "job-edit";
    }

    @PreAuthorize("hasAuthority('Recruiter')")
    @PostMapping("jobs/{id}")
    public String updateJob(@PathVariable Integer id, @Valid @ModelAttribute("job") JobPostActivity form,
                            BindingResult bindingResult,
                            @RequestParam(required = false) String locationCity,
                            @RequestParam(required = false) String companyName) {
        if (bindingResult.hasErrors()) {
            return "job-edit";
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = userService.findByEmail(authentication.getName());

        jobPostActivityService.updateJob(id, form, currentUser, locationCity, companyName);
        return "redirect:/job-details-apply/" + id;
    }

    @PreAuthorize("hasAuthority('Recruiter')")
    @PostMapping("jobs/{id}/delete")
    public String deleteJob(@PathVariable Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = userService.findByEmail(authentication.getName());
        jobPostActivityService.deleteJob(id, currentUser);
        return "redirect:/dashboard";
    }

    @GetMapping("global-search/")
    public String globalSearch(Model model, @RequestParam(value = "job", required = false) String job, @RequestParam(value = "location", required = false) String location, @RequestParam(value = "partTime", required = false) String partTime,
                               @RequestParam(value = "fullTime", required = false) String fullTime, @RequestParam(value = "freelance", required = false) String freelance, @RequestParam(value = "remoteOnly", required = false) String remoteOnly,
                               @RequestParam(value = "officeOnly", required = false) String officeOnly, @RequestParam(value = "partialRemote", required = false) String partialRemote, @RequestParam(value = "today", required = false) boolean today,
                               @RequestParam(value = "days7", required = false) boolean days7, @RequestParam(value = "days30", required = false) boolean days30) {
        model.addAttribute("partTime", Objects.equals(partTime, "Part-Time"));
        model.addAttribute("fullTime", Objects.equals(fullTime, "Full-Time"));
        model.addAttribute("freelance", Objects.equals(freelance, "Freelance"));

        model.addAttribute("remoteOnly", Objects.equals(remoteOnly, "Remote-Only"));
        model.addAttribute("officeOnly", Objects.equals(officeOnly, "Office-Only"));
        model.addAttribute("partialRemote", Objects.equals(partialRemote, "Partial-Remote"));

        model.addAttribute("today", today);
        model.addAttribute("days7", days7);
        model.addAttribute("days30", days30);

        model.addAttribute("job", job);
        model.addAttribute("location", location);

        LocalDate searchDate = null;
        List<JobPostActivity> jobPostActivities = null;
        boolean dateSearchFlag = true;
        boolean remote = true;
        boolean type = true;

        if (days30) {
            searchDate = LocalDate.now().minusDays(30);
        } else if (days7) {
            searchDate = LocalDate.now().minusDays(7);
        } else if (today) {
            searchDate = LocalDate.now();
        } else {
            dateSearchFlag = false;
        }

        if (partTime == null && fullTime == null && freelance == null) {
            partTime = "Part-Time";
            fullTime = "Full-Time";
            freelance = "Freelance";
            remote = false;
        }
        if (officeOnly == null && remoteOnly == null && partialRemote == null) {
            officeOnly = "Office-Only";
            remoteOnly = "Remote-Only";
            partialRemote = "Partial-Remote";
            type = false;
        }

        if (!dateSearchFlag && !remote && !type && !StringUtils.hasText(job) && !StringUtils.hasText(location)) {

            jobPostActivities = jobPostActivityService.getAll();
        } else {
            jobPostActivities = jobPostActivityService.search(job, location, Arrays.asList(partTime, fullTime, freelance),
                    Arrays.asList(remoteOnly, officeOnly, partialRemote), searchDate);
        }

        model.addAttribute("jobPost", jobPostActivities);
        return "global-search";
    }

    @PreAuthorize("hasAuthority('Recruiter')")
    @PostMapping("/dashboard/addNew")
    public String addNew(JobPostActivity jobPostActivity, Model model) {
        Users user = userService.getCurrentUser();
        if (user != null) {
            jobPostActivity.setPostedById(user);
        }
        jobPostActivity.setPostedDate(new Date());
        model.addAttribute("jobPostActivity", jobPostActivity);
        JobPostActivity saved = jobPostActivityService.addNew(jobPostActivity);
        return "redirect:/dashboard";
    }
}
