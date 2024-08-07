package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    @ApiMessage("create a company")
    public ResponseEntity<Company> createNewCompany(@Valid @RequestBody Company postManCompany) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.handleCreateCompany(postManCompany));
    }

    @GetMapping("/companies")
    @ApiMessage("fetch all companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompany(
            @Filter Specification<Company> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.companyService.handleGetAllCompany(spec, pageable));
    }

    @GetMapping("/companies/{id}")
    @ApiMessage("fetch company by id")
    public ResponseEntity<Company> fetchCompanyById(@PathVariable("id") long id) throws IdInvalidException {
        Company company = this.companyService.getCompanyById(id);
        if (company == null) {
            throw new IdInvalidException("Company voi id = " + id + " khong ton tai");
        }
        return ResponseEntity.ok().body(company);
    }

    @PutMapping("/companies")
    @ApiMessage("update a company")
    public ResponseEntity<Company> updateACompany(@Valid @RequestBody Company company) {
        return ResponseEntity.status(HttpStatus.OK).body(this.companyService.handleUpdateACompany(company));
    }

    @DeleteMapping("/companies/{id}")
    @ApiMessage("delete a company")
    public ResponseEntity<Void> deleteACompany(@PathVariable("id") long id) {
        this.companyService.handleDeleteACompany(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
