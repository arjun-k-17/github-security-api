package com.bel.securityapi;

import org.springframework.stereotype.Service;

@Service
public class FirewallService {

    private final FirewallRepository repository;

    public FirewallService(FirewallRepository repository) {
        this.repository = repository;
    }

    public String getStatus() {
        return repository.findById(1L)
                .orElseGet(() -> repository.save(new FirewallStatus("INACTIVE")))
                .getStatus();
    }

    public String activate() {
        FirewallStatus fs = new FirewallStatus("ACTIVE");
        repository.save(fs);
        return fs.getStatus();
    }

    public String deactivate() {
        FirewallStatus fs = new FirewallStatus("INACTIVE");
        repository.save(fs);
        return fs.getStatus();
    }
}

