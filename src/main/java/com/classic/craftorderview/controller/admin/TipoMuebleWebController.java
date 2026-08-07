package com.classic.craftorderview.controller.admin;

import com.classic.craftorderview.constantes.ModelAtributos;
import com.classic.craftorderview.constantes.Vistas;
import com.classic.craftorderview.model.dto.request.TipoMuebleRequestDTO;
import com.classic.craftorderview.model.dto.response.PaginaResponseDTO;
import com.classic.craftorderview.model.dto.response.TipoMuebleResponseDTO;
import com.classic.craftorderview.services.TipoMuebleApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/admin/tiposmueble")
public class TipoMuebleWebController {

    private static final List<String> GALERIA_FALLBACK = List.of(
        "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=300",
        "https://images.unsplash.com/photo-1506439773649-6e0eb8cfb237?w=300",
        "https://images.unsplash.com/photo-1540574163026-643ea20ade25?w=300",
        "https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=600",
        "https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?w=300",
        "https://images.unsplash.com/photo-1598928506311-c55ded91a20c?w=300",
        "https://images.unsplash.com/photo-1567016432779-094069958ea5?w=300",
        "https://images.unsplash.com/photo-1550581190-9c1c48d21d6c?w=300"
    );

    private final TipoMuebleApiService tipoMuebleService;

    public TipoMuebleWebController(TipoMuebleApiService tipoMuebleService) {
        this.tipoMuebleService = tipoMuebleService;
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

        PaginaResponseDTO<TipoMuebleResponseDTO> pagina =
                tipoMuebleService.listarTodosPaginado(nombre, page);

        model.addAttribute("tiposMueble", pagina.getContenido());
        model.addAttribute(ModelAtributos.PAGINA, pagina);
        model.addAttribute(ModelAtributos.NOMBRE_FILTRO, nombre);
        model.addAttribute("galeriaImagenes", construirGaleria());
        model.addAttribute(ModelAtributos.TITULO_PAGINA, "Tipos de Mueble");

        Object tipoMuebleEditar = session.getAttribute("tipoMuebleEditar");
        if (tipoMuebleEditar != null) {
            model.addAttribute("tipoMuebleEditar", tipoMuebleEditar);
            session.removeAttribute("tipoMuebleEditar");
        }

        return Vistas.ADMIN_TIPOSMUEBLE;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String crear(@ModelAttribute TipoMuebleRequestDTO dto,
                         @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                         Model model, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        try {
            tipoMuebleService.crear(dto, imagenFile);
            return "redirect:/admin/tiposmueble";
        } catch (Exception e) {
            PaginaResponseDTO<TipoMuebleResponseDTO> pagina =
                    tipoMuebleService.listarTodosPaginado("", 0);
            model.addAttribute("tiposMueble", pagina.getContenido());
            model.addAttribute(ModelAtributos.PAGINA, pagina);
            model.addAttribute(ModelAtributos.NOMBRE_FILTRO, "");
            model.addAttribute("galeriaImagenes", construirGaleria());
            model.addAttribute(ModelAtributos.TITULO_PAGINA, "Tipos de Mueble");
            model.addAttribute("tipoMuebleNuevo", dto);
            model.addAttribute(ModelAtributos.ERROR_MODAL, e.getMessage());
            model.addAttribute(ModelAtributos.ABRIR_MODAL, true);
            return Vistas.ADMIN_TIPOSMUEBLE;
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        TipoMuebleResponseDTO dto = tipoMuebleService.buscarPorId(id);
        session.setAttribute("tipoMuebleEditar", dto);
        return "redirect:/admin/tiposmueble";
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable Long id, @ModelAttribute TipoMuebleRequestDTO dto,
                              @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                              Model model, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        try {
            tipoMuebleService.actualizar(id, dto, imagenFile);
            return "redirect:/admin/tiposmueble";
        } catch (Exception e) {
            PaginaResponseDTO<TipoMuebleResponseDTO> pagina =
                    tipoMuebleService.listarTodosPaginado("", 0);
            model.addAttribute("tiposMueble", pagina.getContenido());
            model.addAttribute(ModelAtributos.PAGINA, pagina);
            model.addAttribute(ModelAtributos.NOMBRE_FILTRO, "");
            model.addAttribute("galeriaImagenes", construirGaleria());
            model.addAttribute(ModelAtributos.TITULO_PAGINA, "Tipos de Mueble");
            model.addAttribute(ModelAtributos.ERROR_MODAL, e.getMessage());
            model.addAttribute("tipoMuebleEditar", tipoMuebleService.buscarPorId(id));
            model.addAttribute(ModelAtributos.ABRIR_MODAL, true);
            return Vistas.ADMIN_TIPOSMUEBLE;
        }
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable Long id, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        tipoMuebleService.activar(id);
        return "redirect:/admin/tiposmueble";
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable Long id, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        tipoMuebleService.desactivar(id);
        return "redirect:/admin/tiposmueble";
    }

    private List<String> construirGaleria() {
        List<String> urlsRegistros = tipoMuebleService.listarUrlsActivas();

        if (!urlsRegistros.isEmpty()) {
            return urlsRegistros;
        }

        return GALERIA_FALLBACK;
    }

    private String verificarSesion(HttpSession session, String rolRequerido) {
        String rol = (String) session.getAttribute(ModelAtributos.SESSION_USUARIO_ROL);
        if (rol == null) {
            return "redirect:/login";
        }
        if (!rolRequerido.equals(rol)) {
            return "ARTESANO".equals(rol)
                    ? "redirect:/artesano/tablero"
                    : "redirect:/admin/kanban";
        }
        return null;
    }
}
