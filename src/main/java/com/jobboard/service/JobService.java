package com.jobboard.service;

import com.jobboard.exception.CompanyNotFoundException;
import com.jobboard.exception.JobNotFoundException;
import com.jobboard.model.dto.JobCreateDTO;
import com.jobboard.model.dto.JobDetailDTO;
import com.jobboard.model.dto.JobDTO;
import com.jobboard.model.dto.JobUpdateDTO;
import com.jobboard.model.entity.Company;
import com.jobboard.model.entity.Job;
import com.jobboard.repository.CompanyRepository;
import com.jobboard.repository.JobRepository;
import com.jobboard.util.JobMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final JobMapper jobMapper;

    public JobService(JobRepository jobRepository, CompanyRepository companyRepository, JobMapper jobMapper) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.jobMapper = jobMapper;
    }

    @Transactional(readOnly = true)
    public List<JobDTO> getAllJobs() {
        return jobRepository.findAllWithCompany().stream()
                .map(jobMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public JobDetailDTO getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException(id));
        return jobMapper.toDetailDTO(job);
    }

    public JobDTO createJob(JobCreateDTO dto) {
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException(dto.getCompanyId()));
        Job entity = jobMapper.toEntity(dto, company);
        Job saved = jobRepository.save(entity);
        return jobMapper.toDTO(saved);
    }

    public JobDTO updateJob(Long id, JobUpdateDTO dto) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException(id));
        Company company = null;
        if (dto.getCompanyId() != null) {
            company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new CompanyNotFoundException(dto.getCompanyId()));
        }
        jobMapper.updateEntityFromDTO(job, dto, company);
        Job saved = jobRepository.save(job);
        return jobMapper.toDTO(saved);
    }

    public void deleteJob(Long id) {
        jobRepository.findById(id).ifPresent(jobRepository::delete);
    }
}
