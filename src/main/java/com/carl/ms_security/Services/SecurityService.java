package com.carl.ms_security.Services;

import com.carl.ms_security.Models.User;
import com.carl.ms_security.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class SecurityService {
    @Autowired
    private UserRepository theUserRepository;
    @Autowired
    private EncryptionService theEncryptionService;
    @Autowired
    private JwtService theJwtService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String notificationsUrl = "http://localhost:5000/send-email"; // URL de ms-notifications
    private final Map<String, String> pending2fa = new HashMap<>(); // email -> code

    public String login(User theNewUser){
        User theActualUser = this.theUserRepository.getUserByEmail(theNewUser.getEmail());
        if(theActualUser != null &&
                theActualUser.getPassword().equals(theEncryptionService.convertSHA256(theNewUser.getPassword()))){
            // Generar código 2FA
            String code = generate2faCode();
            pending2fa.put(theNewUser.getEmail(), code);
            // Enviar email
            send2faEmail(theNewUser.getEmail(), code);
            return "2FA_REQUIRED"; // Indicar que se requiere 2FA
        }else{
            return null;
        }
    }

    public String loginSocial(User theNewUser) {
        User theActualUser = this.theUserRepository.getUserByEmail(theNewUser.getEmail());
        if (theActualUser == null) {
            // Si el usuario no existe, lo creamos
            theActualUser = this.theUserRepository.save(theNewUser);
        }
        // Para login social (Google), generamos el token directamente (confiamos en el proveedor)
        return theJwtService.generateToken(theActualUser);
    }

    public String verify2fa(String email, String code) {
        String storedCode = pending2fa.get(email);
        if (storedCode != null && storedCode.equals(code)) {
            pending2fa.remove(email);
            User user = theUserRepository.getUserByEmail(email);
            return theJwtService.generateToken(user);
        }
        return null;
    }

    private String generate2faCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    private void send2faEmail(String email, String code) {
        Map<String, String> payload = new HashMap<>();
        payload.put("sender", "tuemail@gmail.com"); // Cambiar por email real
        payload.put("to", email);
        payload.put("subject", "Código de verificación 2FA");
        payload.put("message", "Tu código de verificación es: " + code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(notificationsUrl, entity, String.class);
            System.out.println("Email sent: " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
}
