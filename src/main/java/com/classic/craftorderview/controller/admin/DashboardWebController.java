package com.classic.craftorderview.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/dashboard")
public class DashboardWebController {

    @GetMapping
    public String mostrar(Model model) {
        model.addAttribute("tituloPagina", "Dashboard");
        return "admin/dashboard/dashboard";
    }
}
