package vn.hoidanit.jobhunter.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import net.bytebuddy.asm.Advice.Return;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO handleGetAllCompany(Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta mt = new Meta();

        mt.setPage(pageCompany.getNumber() + 1);
        mt.setPageSize(pageCompany.getSize());
        mt.setPages(pageCompany.getTotalPages());
        mt.setTotal(pageCompany.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageCompany.getContent());
        return rs;
    }

    public Company handleUpdateACompany(Company company) {
        Company currentCompany = this.companyRepository.findById(company.getId());
        if (currentCompany != null) {
            currentCompany.setName(company.getName());
            currentCompany.setDescription(company.getDescription());
            currentCompany.setAddress(company.getAddress());
            currentCompany.setLogo(company.getLogo());
            return this.companyRepository.save(currentCompany);
        }
        return null;
    }

    public void handleDeleteACompany(long id) {
        this.companyRepository.deleteById(id);
    }
}
