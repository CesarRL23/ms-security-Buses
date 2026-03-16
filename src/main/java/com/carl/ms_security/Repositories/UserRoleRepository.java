package com.carl.ms_security.Repositories;

import com.carl.ms_security.Models.User;
import com.carl.ms_security.Models.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserRoleRepository extends MongoRepository<UserRole,String> {

    @Query("{'user.$id' : ObjetId(?0) }")
    public List<UserRole> getRoleByUserId(String userId);
}
