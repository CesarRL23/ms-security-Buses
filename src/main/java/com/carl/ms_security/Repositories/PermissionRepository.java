package com.carl.ms_security.Repositories;

import com.carl.ms_security.Models.Permission;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PermissionRepository extends MongoRepository<Permission, String> {
}
