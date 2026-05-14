package com.carl.ms_security.Services;

import com.carl.ms_security.Models.Profile;
import com.carl.ms_security.Models.Session;
import com.carl.ms_security.Models.User;
import com.carl.ms_security.Models.Role;
import com.carl.ms_security.Models.UserRole;
import com.carl.ms_security.Repositories.ProfileRepository;
import com.carl.ms_security.Repositories.SessionRepository;
import com.carl.ms_security.Repositories.UserRepository;
import com.carl.ms_security.Repositories.RoleRepository;
import com.carl.ms_security.Repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository theUserRepository;

    @Autowired
    private ProfileRepository theProfileRepository;

    @Autowired
    private SessionRepository theSessionRepository;

    @Autowired
    private EncryptionService theEncryptionService;

    @Autowired
    private RoleRepository theRoleRepository;

    @Autowired
    private UserRoleRepository theUserRoleRepository;

    /*
     * Permite asociar un usuario y un perfil. Para que funcione ambos
     * ya deben de existir en la base de datos
     * 
     * @param userId
     * 
     * @param profileId
     * 
     * @return
     */
    public boolean addProfile(String userId, String profileId) {
        User theUser = this.theUserRepository.findById(userId).orElse(null);
        Profile theProfile = this.theProfileRepository.findById(profileId).orElse(null);
        if (theUser != null && theProfile != null) {
            theProfile.setUser(theUser);
            this.theProfileRepository.save(theProfile);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeProfile(String userId, String profileId) {
        User theUser = this.theUserRepository.findById(userId).orElse(null);
        Profile theProfile = this.theProfileRepository.findById(profileId).orElse(null);
        if (theUser != null && theProfile != null) {
            theProfile.setUser(null);
            this.theProfileRepository.save(theProfile);
            return true;
        } else {
            return false;
        }
    }

    public List<User> find() {
        return this.theUserRepository.findAll();
    }

    public User findById(String id) {
        User theUser = this.theUserRepository.findById(id).orElse(null);
        return theUser;
    }

    public User create(User newUser) {

        newUser.setPassword(theEncryptionService.convertSHA256(newUser.getPassword()));

        User savedUser = this.theUserRepository.save(newUser);

        ensureCiudadanoRole(savedUser);

        return savedUser;
    }

    public void ensureCiudadanoRole(User user) {
        if (user == null) {
            System.out.println("[ensureCiudadanoRole] User es null");
            return;
        }

        System.out.println("[ensureCiudadanoRole] Procesando usuario: " + user.getId() + " (" + user.getEmail() + ")");

        boolean alreadyAssigned = this.theUserRoleRepository.findByUser(user).stream()
                .anyMatch(userRole -> userRole.getRole() != null
                        && userRole.getRole().getName() != null
                        && userRole.getRole().getName().equalsIgnoreCase("ciudadano"));

        if (alreadyAssigned) {
            System.out.println("[ensureCiudadanoRole] Usuario ya tiene rol ciudadano");
            return;
        }

        System.out.println("[ensureCiudadanoRole] Buscando rol 'ciudadano' en base de datos");
        Role theRole = this.theRoleRepository.findAll().stream()
                .filter(role -> role.getName() != null && role.getName().equalsIgnoreCase("ciudadano"))
                .findFirst()
                .orElse(null);

        if (theRole == null) {
            System.out.println("[ensureCiudadanoRole] Rol no encontrado, creando nuevo rol ciudadano");
            theRole = this.theRoleRepository.save(new Role("ciudadano", "Rol ciudadano por defecto"));
            System.out.println("[ensureCiudadanoRole] Nuevo rol creado con ID: " + theRole.getId());
        } else {
            System.out.println("[ensureCiudadanoRole] Rol encontrado con ID: " + theRole.getId() + ", nombre: "
                    + theRole.getName());
        }

        System.out.println("[ensureCiudadanoRole] Asignando rol al usuario");
        this.theUserRoleRepository.save(new UserRole(user, theRole));
        System.out.println("[ensureCiudadanoRole] Rol ciudadano asignado exitosamente");
    }

    public User update(String id, User newUser) {
        User actualUser = this.theUserRepository.findById(id).orElse(null);
        if (actualUser != null) {
            actualUser.setName(newUser.getName());
            actualUser.setEmail(newUser.getEmail());

            // Sólo actualiza la contraseña si se envía una nueva
            if (newUser.getPassword() != null && !newUser.getPassword().isEmpty()) {
                actualUser.setPassword(theEncryptionService.convertSHA256(newUser.getPassword()));
            }
            // Si es null, conservamos la que 'actualUser' ya tenía

            this.theUserRepository.save(actualUser);
            return actualUser;
        } else {
            return null;
        }
    }

    public void delete(String id) {
        User theUser = this.theUserRepository.findById(id).orElse(null);
        if (theUser != null) {
            this.theUserRepository.delete(theUser);
        }
    }

    /*
     * Permite asociar un usuario y una sesión. Para que funcione ambos
     * ya deben de existir en la base de datos
     * 
     * @param userId
     * 
     * @param sessionId
     * 
     * @return
     */
    public boolean addSession(String userId, String sessionId) {
        User theUser = this.theUserRepository.findById(userId).orElse(null);
        Session theSession = this.theSessionRepository.findById(sessionId).orElse(null);
        if (theUser != null && theSession != null) {
            theSession.setUser(theUser);
            this.theSessionRepository.save(theSession);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeSession(String userId, String sessionId) {
        User theUser = this.theUserRepository.findById(userId).orElse(null);
        Session theSession = this.theSessionRepository.findById(sessionId).orElse(null);
        if (theUser != null && theSession != null) {
            theSession.setUser(null);
            this.theSessionRepository.save(theSession);
            return true;
        } else {
            return false;
        }
    }

}
