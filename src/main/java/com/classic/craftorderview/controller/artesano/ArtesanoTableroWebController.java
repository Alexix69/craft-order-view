package com.classic.craftorderview.controller.artesano;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/artesano/tablero")
public class ArtesanoTableroWebController {

    @GetMapping
    public String redirigirAlKanban() {
        return "redirect:/artesano/kanban";
    }
}
