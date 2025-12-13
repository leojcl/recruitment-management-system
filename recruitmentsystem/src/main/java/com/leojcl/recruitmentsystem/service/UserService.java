package com.leojcl.recruitmentsystem.service;

import com.leojcl.recruitmentsystem.entity.JobSeekerProfile;
import com.leojcl.recruitmentsystem.entity.RecruiterProfile;
import com.leojcl.recruitmentsystem.entity.Users;
import com.leojcl.recruitmentsystem.repository.JobSeekerProfileRepository;
import com.leojcl.recruitmentsystem.repository.RecruiterProfileRepository;
import com.leojcl.recruitmentsystem.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    private static final int RECRUITER_TYPE_ID = 1;

    private final UsersRepository usersRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UsersRepository usersRepository, JobSeekerProfileRepository jobSeekerProfileRepository, RecruiterProfileRepository recruiterProfileRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Users> getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    @Transactional
    public Users addNew(Users users) {
        if (users == null) {
            throw new IllegalArgumentException("Users must not be null");
        }
        if (users.getUserType() == null) {
            throw new IllegalArgumentException("UserType must not be null");
        }
        if (users.getPassword() == null) {
            throw new IllegalArgumentException("Password must not be null");
        }
        users.setActive(true);
        users.setRegistrationDate(new Date(System.currentTimeMillis()));
        users.setPassword(passwordEncoder.encode(users.getPassword()));

        Users savedUsers = usersRepository.save(users);
        int userTypeId = users.getUserType().getUserTypeId();
        if (userTypeId == RECRUITER_TYPE_ID) {
            recruiterProfileRepository.save(new RecruiterProfile(savedUsers));
        } else {
            jobSeekerProfileRepository.save(new JobSeekerProfile(savedUsers));
        }

        return savedUsers;
    }

    public Optional<Users> getCurrentUserOptional() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        String username = authentication.getName();
        return usersRepository.findByEmail(username);
    }

    public Users getCurrentUserOrThrow() {
        return getCurrentUserOptional().orElseThrow(() -> new UsernameNotFoundException("User not authenticated or not found"));
    }

    //    public Optional<Users> getUserByEmail(String email) {
//        return usersRepository.findByEmail(email);
//    }
    @Deprecated
    public Object getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        Users user = getCurrentUserOrThrow();
        int userId = user.getUserId();

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
            return recruiterProfileRepository.findById(userId).orElse(null);
        }
        return jobSeekerProfileRepository.findById(userId).orElse(null);

//        if (!(authentication instanceof AnonymousAuthenticationToken)) {
//            String userName = authentication.getName();
//            Users users = usersRepository.findByEmail(userName).orElseThrow(() -> new UsernameNotFoundException("Could not found " + userName));
//            int userId = users.getUserId();
//            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
//                RecruiterProfile recruiterProfile = recruiterProfileRepository.findById(userId).orElse(new RecruiterProfile());
//                return recruiterProfile;
//            } else {
//                JobSeekerProfile jobSeekerProfile = jobSeekerProfileRepository.findById(userId).orElse(new JobSeekerProfile());
//                return jobSeekerProfile;
//            }
//        }
//        return null;
    }

    public Users getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String userName = authentication.getName();
            Users user = usersRepository.findByEmail(userName).orElseThrow(() -> new UsernameNotFoundException("Could not found " + userName));

            return user;
        }
        return null;
    }

    public Optional<RecruiterProfile> getCurrentRecruiter() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) return Optional.empty();

        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
            return Optional.empty();
        }

        Users user = getCurrentUserOrThrow();
        return recruiterProfileRepository.findById(user.getUserId());
    }

    public Optional<JobSeekerProfile> getCurrentJobSeeker() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) return Optional.empty();
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
            return Optional.empty();
        }

        Users user = getCurrentUserOrThrow();
        return jobSeekerProfileRepository.findById(user.getUserId());
    }

    public Users findByEmail(String currentUsername) {
        return usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
