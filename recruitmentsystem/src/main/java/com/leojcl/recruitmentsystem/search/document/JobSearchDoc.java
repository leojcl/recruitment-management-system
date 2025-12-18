package com.leojcl.recruitmentsystem.search.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Data
@Document(indexName = "jobs")
public class JobSearchDoc {
    @Id
    private Integer jobPostId;

    // full text search
    private String jobTitle;
    private String descriptionOfJob;

    // filters
    private String jobType;
    private String remote;
    private String salary;

    // denormalzied fields
    private String companyName;
    private String locationCity;

    // sorting
    private Date postedDate;
}
