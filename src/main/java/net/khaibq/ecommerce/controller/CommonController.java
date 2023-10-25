package net.khaibq.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommonController {

    @GetMapping("/maintenance")
    public String maintenancePage() {
        return "maintenance";
    }

    @GetMapping("/coming-soon")
    public String comingSoonPage() {
        return "coming-soon";
    }

    @GetMapping("/components")
    public String componentsPage() {
        return "components";
    }

}
