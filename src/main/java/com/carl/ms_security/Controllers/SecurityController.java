package com.carl.ms_security.Controllers;

import com.carl.ms_security.Models.User;
import com.carl.ms_security.Services.SecurityService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("api/public/security")
public class SecurityController {

    @Autowired
    private SecurityService theSecurityService;

    @PostMapping("login")
    public HashMap<String,Object> login(@RequestBody User theNewUser,
                                        final HttpServletResponse response) throws IOException {

        HashMap<String, Object> theResponse = new HashMap<>();

        String result = this.theSecurityService.login(theNewUser);

        if (result != null) {
            if (result.equals("2FA_REQUIRED")) {
                theResponse.put("message", "2FA required");
                theResponse.put("email", theNewUser.getEmail());
            } else {
                theResponse.put("token", result);
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

        return theResponse;
    }

    @PostMapping("verify-2fa")
    public HashMap<String,Object> verify2fa(@RequestBody Map<String, String> payload,
                                            final HttpServletResponse response) throws IOException {

        HashMap<String, Object> theResponse = new HashMap<>();

        String email = payload.get("email");
        String code = payload.get("code");

        String token = this.theSecurityService.verify2fa(email, code);

        if (token != null) {
            theResponse.put("token", token);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

        return theResponse;
    }
}