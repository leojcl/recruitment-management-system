package com.leojcl.recruitmentsystem.service;

import com.leojcl.recruitmentsystem.entity.*;
import com.leojcl.recruitmentsystem.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.util.StringUtils.hasText;

@Service
public class JobPostActivityService {

    private final JobPostActivityRepository jobPostActivityRepository;
    private final JobLocationRepository jobLocationRepository;
    private final JobCompanyRepository jobCompanyRepository;
    private final JobSeekerApplyRepository jobSeekerApplyRepository;
    private final JobSeekerSaveRepository jobSeekerSaveRepository;

    @Autowired
    public JobPostActivityService(JobPostActivityRepository jobPostActivityRepository, JobLocationRepository jobLocationRepository, JobCompanyRepository jobCompanyRepository, JobSeekerApplyRepository jobSeekerApplyRepository, JobSeekerSaveRepository jobSeekerSaveRepository) {
        this.jobPostActivityRepository = jobPostActivityRepository;
        this.jobLocationRepository = jobLocationRepository;
        this.jobCompanyRepository = jobCompanyRepository;
        this.jobSeekerApplyRepository = jobSeekerApplyRepository;
        this.jobSeekerSaveRepository = jobSeekerSaveRepository;
    }

    public JobPostActivity addNew(JobPostActivity jobPostActivity) {
        return jobPostActivityRepository.save(jobPostActivity);
    }

    public List<RecruiterJobsDto> getRecruiterJobs(int recruiterId) {

        return jobPostActivityRepository.getRecruiterJobs(recruiterId)
                .stream()
                .map(r -> new RecruiterJobsDto(
                        r.getTotalCandidates(),
                        r.getJob_post_id(),
                        r.getJob_title(),
                        new JobLocation(r.getLocationId(), r.getCity(), r.getState(), r.getCountry()),
                        new JobCompany(r.getCompanyId(), r.getName(), "")
                ))
                .toList();
    }

    public JobPostActivity getOne(int id) {
        return jobPostActivityRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
    }

    public List<JobPostActivity> getAll() {
        return jobPostActivityRepository.findAll();
    }

    public List<JobPostActivity> search(String job, String location, List<String> type, List<String> remote, LocalDate searchDate) {
        if (searchDate == null) {
            return jobPostActivityRepository.searchWithoutDate(job, location, remote, type);
        }
        return jobPostActivityRepository.search(job, location, remote, type, searchDate);
    }

    @Transactional
    public JobPostActivity updateJob(Integer id, JobPostActivity form, Users currentUser, String locationCity, String companyName) {
        JobPostActivity job = jobPostActivityRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));

        if (job.getPostedById().getUserId() != currentUser.getUserId()) {
            throw new ResponseStatusException(FORBIDDEN, "Forbidden");
        }

        job.setJobTitle(form.getJobTitle());
        job.setDescriptionOfJob(form.getDescriptionOfJob());
        job.setJobType(form.getJobType());
        job.setSalary(form.getSalary());
        job.setRemote(form.getRemote());

        if (hasText(locationCity)) {
            JobLocation jobLocation = (job.getJobLocationId() != null)
                    ? job.getJobLocationId()
                    : new JobLocation();

            jobLocation.setCity(locationCity);

            JobLocation savedLoc = jobLocationRepository.save(jobLocation);

            job.setJobLocationId(savedLoc);
        }
        if (hasText(companyName)) {
            JobCompany jobCompany = (job.getJobCompanyId() != null)
                    ? job.getJobCompanyId()
                    : new JobCompany();
            jobCompany.setName(companyName);

            JobCompany savedCom = jobCompanyRepository.save(jobCompany);

            job.setJobCompanyId(savedCom);
        }

        return jobPostActivityRepository.save(job);
    }

    @Transactional
    public void deleteJob(Integer id, Users currentUser) {
        JobPostActivity job = jobPostActivityRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
        if (job.getPostedById().getUserId() != currentUser.getUserId()) {
            throw new ResponseStatusException(FORBIDDEN, "Forbidden");
        }
        jobSeekerApplyRepository.deleteByJob(job);
        jobSeekerSaveRepository.deleteByJob(job);

        jobPostActivityRepository.delete(job);
    }
}
