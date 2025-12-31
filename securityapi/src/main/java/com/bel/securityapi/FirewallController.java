package com.bel.securityapi;

import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/firewall")
public class FirewallController {

    private final FirewallService service;

    public FirewallController(FirewallService service) {
        this.service = service;
    }

    @GetMapping("/status")
    public String getStatus() {
        return service.getStatus();
    }

    @PostMapping("/activate")
    public String activate() {
        return service.activate();
    }

    @PostMapping("/deactivate")
    public String deactivate() {
        return service.deactivate();
    }
}

