package com.leojcl.recruitmentsystem.service;

import com.leojcl.recruitmentsystem.entity.JobCompany;
import com.leojcl.recruitmentsystem.entity.JobLocation;
import com.leojcl.recruitmentsystem.entity.JobPostActivity;
import com.leojcl.recruitmentsystem.entity.RecruiterJobsDto;
import com.leojcl.recruitmentsystem.repository.JobPostActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class JobPostActivityService {

    private final JobPostActivityRepository jobPostActivityRepository;

    @Autowired
    public JobPostActivityService(JobPostActivityRepository jobPostActivityRepository) {
        this.jobPostActivityRepository = jobPostActivityRepository;
    }

    public JobPostActivity addNew(JobPostActivity jobPostActivity) {
        return jobPostActivityRepository.save(jobPostActivity);
    }

    public List<RecruiterJobsDto> getRecruiterJobs(int recruiterId) {

//        List<IRecruiterJobs> recruiterJobs = jobPostActivityRepository.getRecruiterJobs(recruiter);
//        List<RecruiterJobsDto> recruiterJobsDtoList = new ArrayList<>();
//
//        for (IRecruiterJobs rec : recruiterJobs) {
//            JobLocation loc = new JobLocation(rec.getLocationId(), rec.getCity(), rec.getState(), rec.getCountry());
//            JobCompany comp = new JobCompany(rec.getCompanyId(), rec.getName(), "");
//            recruiterJobsDtoList.add(new RecruiterJobsDto(rec.getTotalCandidates(), rec.getJob_post_id(), rec.getJob_title(), loc, comp));
//        }
//        return recruiterJobsDtoList;
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
}
