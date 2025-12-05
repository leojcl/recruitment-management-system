package com.leojcl.recruitmentsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "skills")
@Getter
@Setter
public class SKill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String experienceLevel;
    private String yearsOfExperience;

    @ManyToOne
    @JoinColumn(name = "job_seeker_profile")
    private JobSeekerProfile jobSeekerProfile;

    public SKill() {
    }

    public SKill(int id, String name, String experienceLevel, String yearsOfExperience, JobSeekerProfile jobSeekerProfile) {
        this.id = id;
        this.name = name;
        this.experienceLevel = experienceLevel;
        this.yearsOfExperience = yearsOfExperience;
        this.jobSeekerProfile = jobSeekerProfile;
    }

    @Override
    public String toString() {
        return "SKill{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", experienceLevel='" + experienceLevel + '\'' +
                ", yearsOfExperience='" + yearsOfExperience + '\'' +
                ", jobSeekerProfile=" + jobSeekerProfile +
                '}';
    }
}
