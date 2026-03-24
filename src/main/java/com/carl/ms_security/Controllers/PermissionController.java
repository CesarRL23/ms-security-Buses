package com.carl.ms_security.Controllers;

import com.carl.ms_security.Models.Permission;
import com.carl.ms_security.Services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/permissions")
public class PermissionController {

    @Autowired
    private PermissionService thePermissionService;

    @GetMapping("")
    public List<Permission> find() {
        return this.thePermissionService.find();
    }

    @GetMapping("{id}")
    public Permission findById(@PathVariable("id") String id) {
        return this.thePermissionService.findById(id);
    }

    @PostMapping
    public Permission create(@RequestBody Permission newPermission) {
        return this.thePermissionService.create(newPermission);
    }

    @PutMapping("{id}")
    public Permission update(@PathVariable("id") String id, @RequestBody Permission newPermission) {
        return this.thePermissionService.update(id, newPermission);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") String id) {
        this.thePermissionService.delete(id);
    }
}
