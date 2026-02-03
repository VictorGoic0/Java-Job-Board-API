package com.jobboard.service;

import com.jobboard.exception.CompanyNotFoundException;
import com.jobboard.model.dto.CompanyCreateDTO;
import com.jobboard.model.dto.CompanyDTO;
import com.jobboard.model.dto.CompanyUpdateDTO;
import com.jobboard.model.entity.Company;
import com.jobboard.repository.CompanyRepository;
import com.jobboard.util.CompanyMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public CompanyService(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    @Transactional(readOnly = true)
    public Page<CompanyDTO> getAllCompanies(Pageable pageable) {
        return companyRepository.findAll(pageable).map(companyMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public CompanyDTO getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));
        return companyMapper.toDTO(company);
    }

    public CompanyDTO createCompany(CompanyCreateDTO dto) {
        Company entity = companyMapper.toEntity(dto);
        Company saved = companyRepository.save(entity);
        return companyMapper.toDTO(saved);
    }

    public CompanyDTO updateCompany(Long id, CompanyUpdateDTO dto) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));
        companyMapper.updateEntityFromDTO(company, dto);
        Company saved = companyRepository.save(company);
        return companyMapper.toDTO(saved);
    }

    public void deleteCompany(Long id) {
        companyRepository.findById(id).ifPresent(companyRepository::delete);
    }
}
