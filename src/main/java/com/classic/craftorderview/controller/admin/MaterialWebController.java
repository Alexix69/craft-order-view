package com.classic.craftorderview.controller.admin;

import com.classic.craftorderview.constantes.ModelAtributos;
import com.classic.craftorderview.constantes.Vistas;
import com.classic.craftorderview.model.dto.request.AcabadoPorcentajeRequestDTO;
import com.classic.craftorderview.model.dto.request.MaterialRequestDTO;
import com.classic.craftorderview.model.dto.response.MaterialResponseDTO;
import com.classic.craftorderview.model.dto.response.PaginaResponseDTO;
import com.classic.craftorderview.services.MaterialApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/materiales")
public class MaterialWebController {

    private final MaterialApiService materialService;

    public MaterialWebController(MaterialApiService materialService) {
        this.materialService = materialService;
    }

    @GetMapping
    public String listar(
            @RequestParam(defaultValue = "") String nombre,
            @RequestParam(defaultValue = "0") int page,
            Model model, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        PaginaResponseDTO<MaterialResponseDTO> pagina =
                materialService.listarTodosPaginado(nombre, page);

        model.addAttribute("materiales", pagina.getContenido());
        model.addAttribute(ModelAtributos.PAGINA, pagina);
        model.addAttribute(ModelAtributos.NOMBRE_FILTRO, nombre);
        model.addAttribute("acabados", materialService.listarAcabados());
        model.addAttribute(ModelAtributos.TITULO_PAGINA, "Materiales y Acabados");

        Object materialEditar = session.getAttribute("materialEditar");
        if (materialEditar != null) {
            model.addAttribute("materialEditar", materialEditar);
            session.removeAttribute("materialEditar");
        }

        return Vistas.ADMIN_MATERIALES;
    }

    @PostMapping
    public String crear(@ModelAttribute MaterialRequestDTO dto, Model model, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        try {
            materialService.crear(dto);
            return "redirect:/admin/materiales";
        } catch (Exception e) {
            PaginaResponseDTO<MaterialResponseDTO> pagina =
                    materialService.listarTodosPaginado("", 0);
            model.addAttribute("materiales", pagina.getContenido());
            model.addAttribute(ModelAtributos.PAGINA, pagina);
            model.addAttribute(ModelAtributos.NOMBRE_FILTRO, "");
            model.addAttribute("acabados", materialService.listarAcabados());
            model.addAttribute(ModelAtributos.TITULO_PAGINA, "Materiales y Acabados");
            model.addAttribute("materialNuevo", dto);
            model.addAttribute(ModelAtributos.ERROR_MODAL, e.getMessage());
            model.addAttribute(ModelAtributos.ABRIR_MODAL, true);
            return Vistas.ADMIN_MATERIALES;
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        MaterialResponseDTO dto = materialService.buscarPorId(id);
        session.setAttribute("materialEditar", dto);
        return "redirect:/admin/materiales";
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable Long id, @ModelAttribute MaterialRequestDTO dto,
                              Model model, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        try {
            materialService.actualizar(id, dto);
            return "redirect:/admin/materiales";
        } catch (Exception e) {
            PaginaResponseDTO<MaterialResponseDTO> pagina =
                    materialService.listarTodosPaginado("", 0);
            model.addAttribute("materiales", pagina.getContenido());
            model.addAttribute(ModelAtributos.PAGINA, pagina);
            model.addAttribute(ModelAtributos.NOMBRE_FILTRO, "");
            model.addAttribute("acabados", materialService.listarAcabados());
            model.addAttribute(ModelAtributos.TITULO_PAGINA, "Materiales y Acabados");
            model.addAttribute(ModelAtributos.ERROR_MODAL, e.getMessage());
            model.addAttribute("materialEditar", materialService.buscarPorId(id));
            model.addAttribute(ModelAtributos.ABRIR_MODAL, true);
            return Vistas.ADMIN_MATERIALES;
        }
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable Long id, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        materialService.activar(id);
        return "redirect:/admin/materiales";
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable Long id, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        materialService.desactivar(id);
        return "redirect:/admin/materiales";
    }

    @PostMapping("/acabados/{tipo}/porcentaje")
    public String actualizarPorcentaje(@PathVariable String tipo, @ModelAttribute AcabadoPorcentajeRequestDTO dto,
                                        Model model, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        try {
            materialService.actualizarPorcentajeAcabado(tipo, dto);
            return "redirect:/admin/materiales";
        } catch (Exception e) {
            PaginaResponseDTO<MaterialResponseDTO> pagina =
                    materialService.listarTodosPaginado("", 0);
            model.addAttribute("materiales", pagina.getContenido());
            model.addAttribute(ModelAtributos.PAGINA, pagina);
            model.addAttribute(ModelAtributos.NOMBRE_FILTRO, "");
            model.addAttribute("acabados", materialService.listarAcabados());
            model.addAttribute(ModelAtributos.TITULO_PAGINA, "Materiales y Acabados");
            model.addAttribute("errorModalAcabado", e.getMessage());
            model.addAttribute("abrirModalAcabado", true);
            model.addAttribute("acabadoTipoError", tipo);
            model.addAttribute("acabadoPorcentajeError", dto.getPorcentaje());
            return Vistas.ADMIN_MATERIALES;
        }
    }

    private String verificarSesion(HttpSession session, String rolRequerido) {
        String rol = (String) session.getAttribute(ModelAtributos.SESSION_USUARIO_ROL);
        if (rol == null) {
            return "redirect:/login";
        }
        if (!rolRequerido.equals(rol)) {
            return "ARTESANO".equals(rol)
                    ? "redirect:/artesano/tablero"
                    : "redirect:/admin/dashboard";
        }
        return null;
    }
}
