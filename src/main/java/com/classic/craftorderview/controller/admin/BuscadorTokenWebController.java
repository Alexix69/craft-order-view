package com.classic.craftorderview.controller.admin;

import com.classic.craftorderview.constantes.ModelAtributos;
import com.classic.craftorderview.services.CotizacionApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/admin")
public class BuscadorTokenWebController {

    private final CotizacionApiService cotizacionService;

    public BuscadorTokenWebController(CotizacionApiService cotizacionService) {
        this.cotizacionService = cotizacionService;
    }

    @GetMapping("/buscar-token")
    @ResponseBody
    public ResponseEntity<?> buscarToken(
            @RequestParam String token,
            HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute(ModelAtributos.SESSION_USUARIO_ROL))) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "No autorizado"));
        }

        try {
            Map<String, Object> resultado = cotizacionService.buscarTokenAdmin(token);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
