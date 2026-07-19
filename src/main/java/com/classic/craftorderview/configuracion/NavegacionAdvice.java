package com.classic.craftorderview.configuracion;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NavegacionAdvice {

    @ModelAttribute("seccionActiva")
    public String seccionActiva(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.startsWith("/admin/cotizaciones")) return "cotizaciones";
        if (uri.startsWith("/admin/artesanos"))    return "artesanos";
        if (uri.startsWith("/admin/tiposmueble"))  return "tiposmueble";
        if (uri.startsWith("/admin/materiales"))   return "materiales";
        if (uri.startsWith("/admin/ordenes"))      return "ordenes";
        if (uri.startsWith("/admin/dashboard"))    return "dashboard";
        return "";
    }
}
