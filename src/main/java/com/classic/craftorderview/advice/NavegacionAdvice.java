package com.classic.craftorderview.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NavegacionAdvice {

    @ModelAttribute("seccionActiva")
    public String seccionActiva(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
