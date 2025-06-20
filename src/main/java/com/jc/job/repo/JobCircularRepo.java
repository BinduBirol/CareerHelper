package com.jc.job.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import com.jc.job.dto.JobCircularEntity;

public interface JobCircularRepo extends CrudRepository<JobCircularEntity, Long> {

	Optional<JobCircularEntity> findBySourceId(String sourceId);

	List<JobCircularEntity> findAll(Sort by);

}
