package com.linkfit.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/members")
    public String members() { return "members"; }

    @GetMapping("/staff")
    public String staff() { return "staff"; }

    @GetMapping("/classes")
    public String classes() { return "classes"; }

    @GetMapping("/attendance")
    public String attendance() { return "attendance"; }

    @GetMapping("/consults")
    public String consults() { return "consults"; }

    @GetMapping("/revenue")
    public String revenue() { return "revenue"; }

    @GetMapping("/products")
    public String products() { return "products"; }

    @GetMapping("/messages")
    public String messages() { return "messages"; }

    @GetMapping("/settings")
    public String settings() { return "settings"; }
}
