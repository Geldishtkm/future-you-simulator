package org.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA configuration class.
 * Enables JPA auditing and transaction management.
 */
@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {
    // Configuration handled by Spring Boot auto-configuration
    // This class exists for explicit configuration if needed in the future
}

