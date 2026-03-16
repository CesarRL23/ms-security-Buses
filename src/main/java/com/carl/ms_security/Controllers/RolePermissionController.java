package com.carl.ms_security.Controllers;

import com.carl.ms_security.Models.RolePermission;
import com.carl.ms_security.Services.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/role-permissions")
public class RolePermissionController {

    @Autowired
    private RolePermissionService theRolePermissionService;

    @GetMapping("")
    public List<RolePermission> find(){
        return this.theRolePermissionService.find();
    }

    @GetMapping("{id}")
    public RolePermission findById(@PathVariable String id){
        return this.theRolePermissionService.findById(id);
    }

    @PostMapping
    public RolePermission create(@RequestBody RolePermission newRolePermission){
        return this.theRolePermissionService.create(newRolePermission);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id){
        this.theRolePermissionService.delete(id);
    }

}