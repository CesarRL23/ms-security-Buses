package com.carl.ms_security.Services;

import com.carl.ms_security.Models.RolePermission;
import com.carl.ms_security.Repositories.RolePermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolePermissionService {

    @Autowired
    private RolePermissionRepository theRolePermissionRepository;

    public List<RolePermission> find(){
        return this.theRolePermissionRepository.findAll();
    }

    public RolePermission findById(String id){
        return this.theRolePermissionRepository.findById(id).orElse(null);
    }

    public RolePermission create(RolePermission newRolePermission){
        return this.theRolePermissionRepository.save(newRolePermission);
    }

    public void delete(String id){
        RolePermission theRolePermission = this.theRolePermissionRepository.findById(id).orElse(null);
        if(theRolePermission != null){
            this.theRolePermissionRepository.delete(theRolePermission);
        }
    }

}