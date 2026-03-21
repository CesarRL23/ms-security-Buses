package com.carl.ms_security.Controllers;

import com.carl.ms_security.Models.RolePermission;
import com.carl.ms_security.Services.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/role-permission")
public class RolePermissionController {

    @Autowired
    private RolePermissionService theRolePermissionService;

    @GetMapping("")
    public List<RolePermission> find() {
        return this.theRolePermissionService.find();
    }

    @PostMapping("role/{roleId}/permission/{permissionId}")
    public RolePermission create(@PathVariable String roleId, @PathVariable String permissionId) {
        return this.theRolePermissionService.create(roleId, permissionId);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        this.theRolePermissionService.delete(id);
    }
}
