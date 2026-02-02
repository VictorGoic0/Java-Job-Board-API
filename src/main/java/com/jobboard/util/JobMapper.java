package com.jobboard.util;

import com.jobboard.model.dto.JobCreateDTO;
import com.jobboard.model.dto.JobDetailDTO;
import com.jobboard.model.dto.JobDTO;
import com.jobboard.model.dto.JobUpdateDTO;
import com.jobboard.model.entity.Company;
import com.jobboard.model.entity.Job;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {

    private final CompanyMapper companyMapper;

    public JobMapper(CompanyMapper companyMapper) {
        this.companyMapper = companyMapper;
    }

    public JobDTO toDTO(Job entity) {
        if (entity == null) {
            return null;
        }
        JobDTO dto = new JobDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setLocation(entity.getLocation());
        dto.setSalaryMin(entity.getSalaryMin());
        dto.setSalaryMax(entity.getSalaryMax());
        dto.setJobType(entity.getJobType());
        dto.setExperienceLevel(entity.getExperienceLevel());
        dto.setRemoteOption(entity.getRemoteOption());
        dto.setPostedDate(entity.getPostedDate());
        dto.setIsActive(entity.getIsActive());
        dto.setCompany(companyMapper.toSummaryDTO(entity.getCompany()));
        return dto;
    }

    public JobDetailDTO toDetailDTO(Job entity) {
        if (entity == null) {
            return null;
        }
        JobDetailDTO dto = new JobDetailDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setLocation(entity.getLocation());
        dto.setSalaryMin(entity.getSalaryMin());
        dto.setSalaryMax(entity.getSalaryMax());
        dto.setJobType(entity.getJobType());
        dto.setExperienceLevel(entity.getExperienceLevel());
        dto.setRemoteOption(entity.getRemoteOption());
        dto.setPostedDate(entity.getPostedDate());
        dto.setIsActive(entity.getIsActive());
        dto.setCompany(companyMapper.toSummaryDTO(entity.getCompany()));
        dto.setDescription(entity.getDescription());
        dto.setExpiryDate(entity.getExpiryDate());
        dto.setApplicationUrl(entity.getApplicationUrl());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public Job toEntity(JobCreateDTO dto, Company company) {
        if (dto == null) {
            return null;
        }
        Job entity = new Job();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setCompany(company);
        entity.setLocation(dto.getLocation());
        entity.setSalaryMin(dto.getSalaryMin());
        entity.setSalaryMax(dto.getSalaryMax());
        entity.setJobType(dto.getJobType());
        entity.setExperienceLevel(dto.getExperienceLevel());
        entity.setRemoteOption(dto.getRemoteOption());
        entity.setExpiryDate(dto.getExpiryDate());
        entity.setApplicationUrl(dto.getApplicationUrl());
        return entity;
    }

    public void updateEntityFromDTO(Job entity, JobUpdateDTO dto, Company company) {
        if (entity == null || dto == null) {
            return;
        }
        if (dto.getTitle() != null) {
            entity.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (company != null) {
            entity.setCompany(company);
        }
        if (dto.getLocation() != null) {
            entity.setLocation(dto.getLocation());
        }
        if (dto.getSalaryMin() != null) {
            entity.setSalaryMin(dto.getSalaryMin());
        }
        if (dto.getSalaryMax() != null) {
            entity.setSalaryMax(dto.getSalaryMax());
        }
        if (dto.getJobType() != null) {
            entity.setJobType(dto.getJobType());
        }
        if (dto.getExperienceLevel() != null) {
            entity.setExperienceLevel(dto.getExperienceLevel());
        }
        if (dto.getRemoteOption() != null) {
            entity.setRemoteOption(dto.getRemoteOption());
        }
        if (dto.getExpiryDate() != null) {
            entity.setExpiryDate(dto.getExpiryDate());
        }
        if (dto.getApplicationUrl() != null) {
            entity.setApplicationUrl(dto.getApplicationUrl());
        }
    }
}
