package com.carl.ms_security.Services;

import com.carl.ms_security.Models.*;
import com.carl.ms_security.Repositories.PermissionRepository;
import com.carl.ms_security.Repositories.RolePermissionRepository;
import com.carl.ms_security.Repositories.UserRepository;
import com.carl.ms_security.Repositories.UserRoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ValidatorsService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PermissionRepository thePermissionRepository;

    @Autowired
    private RolePermissionRepository theRolePermissionRepository;

    @Autowired
    private UserRoleRepository theUserRoleRepository;

    @Autowired
    private UserRepository theUserRepository;

    private static final String BEARER_PREFIX = "Bearer ";

    public boolean validationRolePermission(HttpServletRequest request,
                                            String url,
                                            String method){

        boolean success = false;

        User theUser = this.getUser(request);

        if (theUser != null) {

            List<UserRole> roles =
                    this.theUserRoleRepository.getRoleByUserId(theUser.getId());

            url = url.replaceAll("[0-9a-fA-F]{24}|\\d+", "?");

            Permission thePermission =
                    this.thePermissionRepository.getPermission(url, method);

            int i = 0;

            while (i < roles.size() && !success) {

                UserRole actual = roles.get(i);
                Role theRole = actual.getRole();

                if (theRole != null && thePermission != null) {

                    RolePermission theRolePermission =
                            this.theRolePermissionRepository.getRolePermission(
                                    theRole.getId(),
                                    thePermission.getId()
                            );

                    if (theRolePermission != null) {
                        success = true;
                    }
                }

                i++;
            }
        }

        return success;
    }


    public User getUser(final HttpServletRequest request) {

        User theUser = null;

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null &&
                authorizationHeader.startsWith(BEARER_PREFIX)) {

            String token =
                    authorizationHeader.substring(BEARER_PREFIX.length());

            User theUserFromToken =
                    jwtService.getUserFromToken(token);

            if (theUserFromToken != null) {

                theUser =
                        this.theUserRepository
                                .findById(theUserFromToken.getId())
                                .orElse(null);
            }
        }

        return theUser;
    }
}
