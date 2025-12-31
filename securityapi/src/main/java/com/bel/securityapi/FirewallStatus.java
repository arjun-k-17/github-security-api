package com.bel.securityapi;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "firewall_status")
public class FirewallStatus {

    @Id
    private Long id = 1L;   // single row design

    private String status;

    public FirewallStatus() {}

    public FirewallStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

