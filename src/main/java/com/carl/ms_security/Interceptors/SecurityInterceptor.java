package com.carl.ms_security.Interceptors;

import com.carl.ms_security.Services.ValidatorsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class SecurityInterceptor implements HandlerInterceptor {
    @Autowired
    private ValidatorsService validatorService;
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        System.out.println("DEBUG Interceptor: [" + method + "] " + uri);

        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // ⚠️ Excepciones públicas permitidas por el interceptor
        if ("POST".equalsIgnoreCase(method) && uri.contains("/users")) {
            return true;
        }
        if ("GET".equalsIgnoreCase(method) && 
            (uri.contains("/roles") || uri.contains("/user-role") || 
             uri.contains("/role-permission") || uri.contains("/permissions") || 
             uri.contains("/users") || uri.contains("/error"))) {
            return true;
        }

        if (this.validatorService.isAdmin(request)) {
            return true;
        }

        boolean success = this.validatorService.validationRolePermission(request, uri, method);
        
        if (!success) {
            // Log para que el usuario pueda ver en consola qué permiso le falta
            System.out.println("❌ ACCESO RECHAZADO: [" + method + "] en " + uri);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No tiene autorización para realizar la acción solicitada");
        }
        return success;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // Lógica a ejecutar después de que se haya manejado la solicitud por el controlador
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        // Lógica a ejecutar después de completar la solicitud, incluso después de la renderización de la vista
    }
}


