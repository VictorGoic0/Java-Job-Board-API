package com.jobboard.controller;

import com.jobboard.model.dto.CompanyCreateDTO;
import com.jobboard.model.dto.CompanyDTO;
import com.jobboard.model.dto.CompanyUpdateDTO;
import com.jobboard.service.CompanyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies")
@Validated
public class CompanyController {

    private static final Set<String> ALLOWED_COMPANY_SORT_FIELDS = Set.of(
            "id", "name", "description", "website", "location", "createdAt", "updatedAt");

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public ResponseEntity<Page<CompanyDTO>> getAllCompanies(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "name,asc") String sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return ResponseEntity.ok(companyService.getAllCompanies(pageable));
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "name");
        }
        String[] parts = sort.split(",");
        if (parts.length < 2) {
            return Sort.by(Sort.Direction.ASC, "name");
        }
        List<Sort.Order> orders = new ArrayList<>();
        for (int i = 0; i + 1 < parts.length; i += 2) {
            String property = parts[i].trim();
            if (!ALLOWED_COMPANY_SORT_FIELDS.contains(property)) {
                continue;
            }
            Sort.Direction direction = "asc".equalsIgnoreCase(parts[i + 1].trim())
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;
            orders.add(new Sort.Order(direction, property));
        }
        return orders.isEmpty() ? Sort.by(Sort.Direction.ASC, "name") : Sort.by(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getCompanyById(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

    @PostMapping
    public ResponseEntity<CompanyDTO> createCompany(@Valid @RequestBody CompanyCreateDTO dto) {
        CompanyDTO result = companyService.createCompany(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CompanyDTO> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody CompanyUpdateDTO dto) {
        return ResponseEntity.ok(companyService.updateCompany(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
}
