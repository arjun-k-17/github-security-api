package com.bel.securityapi;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FirewallRepository extends JpaRepository<FirewallStatus, Long> {
}

