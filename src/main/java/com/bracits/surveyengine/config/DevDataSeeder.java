package com.bracits.surveyengine.config;

import com.bracits.surveyengine.admin.dto.RegisterRequest;
import com.bracits.surveyengine.admin.repository.AdminUserRepository;
import com.bracits.surveyengine.admin.service.AdminAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Seeds a default admin user, tenant, and trial subscription
 * for local development testing.
 *
 * <p>
 * Only active when the {@code dev} profile is enabled.
 * Skips seeding if the admin email already exists.
 *
 * <p>
 * Default credentials:
 * <ul>
 * <li>Email: {@code admin@dev.local}</li>
 * <li>Password: {@code admin123}</li>
 * <li>Tenant: {@code dev-tenant}</li>
 * </ul>
 */
@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DevDataSeeder implements ApplicationRunner {

    private static final String DEV_EMAIL = "admin@dev.local";
    private static final String DEV_PASSWORD = "admin123";
    private static final String DEV_FULL_NAME = "Dev Admin";
    private static final String DEV_TENANT_ID = "dev-tenant";

    private final AdminUserRepository adminUserRepository;
    private final AdminAuthService adminAuthService;

    @Override
    public void run(ApplicationArguments args) {
        if (adminUserRepository.existsByEmail(DEV_EMAIL)) {
            log.info("Dev seed: admin user '{}' already exists — skipping", DEV_EMAIL);
            return;
        }

        log.info("Dev seed: creating admin user '{}' for tenant '{}'", DEV_EMAIL, DEV_TENANT_ID);

        RegisterRequest request = new RegisterRequest();
        request.setEmail(DEV_EMAIL);
        request.setPassword(DEV_PASSWORD);
        request.setFullName(DEV_FULL_NAME);
        request.setTenantId(DEV_TENANT_ID);

        adminAuthService.register(request);

        log.info("Dev seed: admin user created successfully. Login with email='{}' password='{}'",
                DEV_EMAIL, DEV_PASSWORD);
    }
}
