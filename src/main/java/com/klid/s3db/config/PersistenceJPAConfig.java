package com.klid.s3db.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.klid.s3db.service.persistence.repository")
@EntityScan("com.klid.s3db.service.persistence.entity")
public class PersistenceJPAConfig {
}
