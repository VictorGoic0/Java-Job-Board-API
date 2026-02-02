package com.jobboard.util;

import com.jobboard.model.dto.CompanyCreateDTO;
import com.jobboard.model.dto.CompanyDTO;
import com.jobboard.model.dto.CompanySummaryDTO;
import com.jobboard.model.dto.CompanyUpdateDTO;
import com.jobboard.model.entity.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyDTO toDTO(Company entity) {
        if (entity == null) {
            return null;
        }
        CompanyDTO dto = new CompanyDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setWebsite(entity.getWebsite());
        dto.setLocation(entity.getLocation());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public CompanySummaryDTO toSummaryDTO(Company entity) {
        if (entity == null) {
            return null;
        }
        CompanySummaryDTO dto = new CompanySummaryDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLocation(entity.getLocation());
        return dto;
    }

    public Company toEntity(CompanyCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        Company entity = new Company();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setWebsite(dto.getWebsite());
        entity.setLocation(dto.getLocation());
        return entity;
    }

    public void updateEntityFromDTO(Company entity, CompanyUpdateDTO dto) {
        if (entity == null || dto == null) {
            return;
        }
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getWebsite() != null) {
            entity.setWebsite(dto.getWebsite());
        }
        if (dto.getLocation() != null) {
            entity.setLocation(dto.getLocation());
        }
    }
}
