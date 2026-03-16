package com.carl.ms_security.Repositories;

import com.carl.ms_security.Models.RolePermission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface RolePermissionRepository extends MongoRepository<RolePermission,String> {

    @Query("{'role.$id': ObjectId(?0), 'permission.$id': ObjectId(?1)}")
    RolePermission getRolePermission(String roleId, String permissionId);

}