package com.carl.ms_security.Repositories;

import com.carl.ms_security.Models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface RoleRepository extends MongoRepository<Role, String> {
	@Query("{ 'name': { $regex: ?0, $options: 'i' } }")
	Role findByName(String name);
}
