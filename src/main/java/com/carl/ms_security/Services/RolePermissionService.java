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

    public List<RolePermission> find() {
        return this.theRolePermissionRepository.findAll();
    }

    public RolePermission findById(String id) {
        return this.theRolePermissionRepository.findById(id).orElse(null);
    }

    public RolePermission create(String roleId, String permissionId) {
        Role theRole = this.theRoleRepository.findById(roleId).orElse(null);
        Permission thePermission = this.thePermissionRepository.findById(permissionId).orElse(null);

        if (theRole != null && thePermission != null) {
            RolePermission newRolePermission = new RolePermission();
            newRolePermission.setRole(theRole);
            newRolePermission.setPermission(thePermission);
            return this.theRolePermissionRepository.save(newRolePermission);
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
}
