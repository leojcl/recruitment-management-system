package com.leojcl.recruitmentsystem.search.repository;

import com.leojcl.recruitmentsystem.search.document.JobSearchDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface JobSearchRepository extends ElasticsearchRepository<JobSearchDoc, Integer> {
}
