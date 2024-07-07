package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("create a role")
    public ResponseEntity<Role> create(@Valid @RequestBody Role r) throws IdInvalidException {
        if (this.roleService.isNameExist(r.getName())) {
            throw new IdInvalidException("Role voi name = " + r.getName() + " da ton tai");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(r));
    }

    @PutMapping("/roles")
    @ApiMessage("update a role")
    public ResponseEntity<Role> update(@RequestBody Role r) throws IdInvalidException {
        Role role = this.roleService.findRoleById(r.getId());
        if (this.roleService.isNameExist(r.getName())) {
            throw new IdInvalidException("Role voi name = " + r.getName() + " da ton tai");
        }
        if (role == null) {
            throw new IdInvalidException("Role voi id = " + r.getId() + " khong ton tai");
        }
        return ResponseEntity.ok().body(this.roleService.update(r));
    }

    @GetMapping("/roles")
    @ApiMessage("fetch all roles with paginate")
    public ResponseEntity<ResultPaginationDTO> fetchAll(@Filter Specification<Role> spec,
            Pageable pageable) {
        ResultPaginationDTO roles = this.roleService.fetchAllRole(spec, pageable);
        return ResponseEntity.ok().body(roles);
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("delete a role")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Role currentRole = this.roleService.findRoleById(id);
        if (currentRole == null) {
            throw new IdInvalidException("Role not found");
        }
        this.roleService.delete(id);
        return ResponseEntity.ok().body(null);
    }
}
