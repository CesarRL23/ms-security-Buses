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
    private final Map<String, String> pendingRecovery = new HashMap<>(); // email -> code

    private final String reCaptchaSecret = "6LeO8rosAAAAAMh7X0dh07W3CdJ-IUYtmMpO69og";
    private final String reCaptchaUrl = "https://www.google.com/recaptcha/api/siteverify";

    public boolean verifyCaptcha(String token) {
        if (token == null || token.isEmpty()) return false;
        
        Map<String, String> body = new HashMap<>();
        body.put("secret", reCaptchaSecret);
        body.put("response", token);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                reCaptchaUrl + "?secret=" + reCaptchaSecret + "&response=" + token, 
                null, 
                Map.class
            );
            Map<String, Object> responseBody = response.getBody();
            return responseBody != null && (Boolean) responseBody.get("success");
        } catch (Exception e) {
            System.err.println("Error verifying ReCAPTCHA: " + e.getMessage());
            return false;
        }
    }

    public String login(User theNewUser, String captchaToken){
        if (!verifyCaptcha(captchaToken)) {
            return "CAPTCHA_INVALID";
        }
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

    public boolean forgotPassword(String email) {
        User user = theUserRepository.getUserByEmail(email);
        if (user != null) {
            String code = generate2faCode(); // reusamos el generador de codigo numerico
            pendingRecovery.put(email, code);
            sendRecoveryEmail(email, code);
            return true;
        }
        return false;
    }

    public boolean resetPassword(String email, String code, String newPassword) {
        String storedCode = pendingRecovery.get(email);
        if (storedCode != null && storedCode.equals(code)) {
            User user = theUserRepository.getUserByEmail(email);
            if (user != null) {
                user.setPassword(theEncryptionService.convertSHA256(newPassword));
                theUserRepository.save(user);
                pendingRecovery.remove(email);
                return true;
            }
        }
        return false;
    }

    private void sendRecoveryEmail(String email, String code) {
        Map<String, String> payload = new HashMap<>();
        payload.put("sender", "tuemail@gmail.com"); 
        payload.put("to", email);
        payload.put("subject", "Código de Recuperación de Contraseña");
        payload.put("message", "Su código para recuperar la contraseña es: " + code + "\n\nPor favor, introduzca este código en la página para restablecer su contraseña.");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(notificationsUrl, entity, String.class);
            System.out.println("Email recovery sent: " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("Error sending recovery email: " + e.getMessage());
        }
    }
}
