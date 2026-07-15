package com.diplomado.billing_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Atrapa la URL raíz y redirige obligatoriamente al login
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }
}