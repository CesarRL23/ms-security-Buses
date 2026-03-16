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
    private ValidatorsService validatorsService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        boolean success = this.validatorsService.validationRolePermission(
                request,
                request.getRequestURI(),
                request.getMethod()
        );

        if (!success) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {

        // lógica después de ejecutar el controller
    }

}