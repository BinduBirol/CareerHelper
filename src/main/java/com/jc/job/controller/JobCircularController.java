package com.jc.job.controller;


import com.jc.job.dto.JobCircularEntity;
import com.jc.job.repo.JobCircularRepo;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/job")
public class JobCircularController {

    private final JobCircularRepo jobCircularRepo;

    public JobCircularController(JobCircularRepo jobCircularRepo) {
        this.jobCircularRepo = jobCircularRepo;
    }

    @GetMapping("/circulars")
    public List<JobCircularEntity> getAllCirculars() {
        return jobCircularRepo.findAll(Sort.by(Sort.Direction.DESC, "publishDate"));
    }
}
