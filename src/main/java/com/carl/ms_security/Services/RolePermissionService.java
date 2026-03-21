package com.carl.ms_security.Services;

import com.carl.ms_security.Models.Permission;
import com.carl.ms_security.Models.Role;
import com.carl.ms_security.Models.RolePermission;
import com.carl.ms_security.Repositories.PermissionRepository;
import com.carl.ms_security.Repositories.RolePermissionRepository;
import com.carl.ms_security.Repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolePermissionService {

    @Autowired
    private RolePermissionRepository theRolePermissionRepository;

    @Autowired
    private RoleRepository theRoleRepository;

    @Autowired
    private PermissionRepository thePermissionRepository;

    public List<RolePermission> findAll() {
        return this.theRolePermissionRepository.findAll();
    }

    public RolePermission findById(String id) {
        return this.theRolePermissionRepository.findById(id).orElse(null);
    }

    public RolePermission create(RolePermission newRolePermission) {
        return this.theRolePermissionRepository.save(newRolePermission);
    }

    public RolePermission update(String id, RolePermission newRolePermission) {
        RolePermission actualRolePermission = this.theRolePermissionRepository.findById(id).orElse(null);

        if (actualRolePermission != null) {
            actualRolePermission.setRole(newRolePermission.getRole());
            actualRolePermission.setPermission(newRolePermission.getPermission());
            // Si el modelo tuviera más campos, se actualizarían aquí
            return this.theRolePermissionRepository.save(actualRolePermission);
        } else {
            return null;
        }
    }

    public void delete(String id) {
        RolePermission theRolePermission = this.theRolePermissionRepository.findById(id).orElse(null);
        if (theRolePermission != null) {
            this.theRolePermissionRepository.delete(theRolePermission);
        }
    }

    public RolePermission addPermissionToRole(String roleId, String permissionId) {
        Role role = this.theRoleRepository.findById(roleId).orElse(null);
        Permission permission = this.thePermissionRepository.findById(permissionId).orElse(null);
        if (role != null && permission != null) {
            RolePermission rolePermission = new RolePermission(role, permission);
            return this.theRolePermissionRepository.save(rolePermission);
        } else {
            return null;
        }
    }
}