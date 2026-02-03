package com.jobboard.repository;

import com.jobboard.model.entity.Job;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByCompanyId(Long companyId);

    @Query("SELECT j FROM Job j JOIN FETCH j.company")
    List<Job> findAllWithCompany();

    @Query(value = "SELECT j FROM Job j JOIN FETCH j.company", countQuery = "SELECT COUNT(j) FROM Job j")
    Page<Job> findAllWithCompany(Pageable pageable);
}
