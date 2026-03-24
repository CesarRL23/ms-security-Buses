package com.carl.ms_security.Repositories;

import com.carl.ms_security.Models.Permission;
import com.carl.ms_security.Models.Role;
import com.carl.ms_security.Models.RolePermission;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface RolePermissionRepository extends MongoRepository<RolePermission, String> {
    Optional<RolePermission> findByRoleAndPermission(Role role, Permission permission);
}