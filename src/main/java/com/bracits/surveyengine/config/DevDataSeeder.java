package com.bracits.surveyengine.config;

import com.bracits.surveyengine.admin.dto.RegisterRequest;
import com.bracits.surveyengine.admin.entity.AdminRole;
import com.bracits.surveyengine.admin.entity.AdminUser;
import com.bracits.surveyengine.admin.repository.AdminUserRepository;
import com.bracits.surveyengine.admin.service.AdminAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Seeds default admin users, tenants, and trial subscriptions
 * for local development testing.
 *
 * <p>
 * Only active when the {@code dev} profile is enabled.
 * Skips seeding if the admin email already exists.
 *
 * <p>
 * Default credentials:
 * <ul>
 * <li>Super Admin Email: {@code superadmin@dev.local}</li>
 * <li>Super Admin Password: {@code superadmin123}</li>
 * <li>Super Admin Tenant: {@code platform-dev-tenant}</li>
 * <li>Admin Email: {@code admin@dev.local}</li>
 * <li>Admin Password: {@code admin123}</li>
 * <li>Admin Tenant: {@code dev-tenant}</li>
 * </ul>
 */
@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DevDataSeeder implements ApplicationRunner {

    private static final String SUPER_ADMIN_EMAIL = "superadmin@dev.local";
    private static final String SUPER_ADMIN_PASSWORD = "superadmin123";
    private static final String SUPER_ADMIN_FULL_NAME = "Dev Super Admin";
    private static final String SUPER_ADMIN_TENANT_ID = "platform-dev-tenant";

    private static final String DEV_EMAIL = "admin@dev.local";
    private static final String DEV_PASSWORD = "admin123";
    private static final String DEV_FULL_NAME = "Dev Admin";
    private static final String DEV_TENANT_ID = "dev-tenant";

    private final AdminUserRepository adminUserRepository;
    private final AdminAuthService adminAuthService;

    @Override
    public void run(ApplicationArguments args) {
        seedSuperAdmin();
        seedAdmin();
    }

    private void seedSuperAdmin() {
        if (adminUserRepository.existsByEmail(SUPER_ADMIN_EMAIL)) {
            log.info("Dev seed: super admin user '{}' already exists — skipping", SUPER_ADMIN_EMAIL);
            return;
        }

        log.info("Dev seed: creating super admin user '{}' for tenant '{}'",
                SUPER_ADMIN_EMAIL, SUPER_ADMIN_TENANT_ID);

        RegisterRequest request = new RegisterRequest();
        request.setEmail(SUPER_ADMIN_EMAIL);
        request.setPassword(SUPER_ADMIN_PASSWORD);
        request.setConfirmPassword(SUPER_ADMIN_PASSWORD);
        request.setFullName(SUPER_ADMIN_FULL_NAME);
        request.setTenantId(SUPER_ADMIN_TENANT_ID);

        adminAuthService.register(request);

        AdminUser superAdmin = adminUserRepository.findByEmail(SUPER_ADMIN_EMAIL)
                .orElseThrow(() -> new IllegalStateException("Seeded super admin user not found after registration"));
        superAdmin.setRole(AdminRole.SUPER_ADMIN);
        adminUserRepository.save(superAdmin);

        log.info("Dev seed: super admin user created. Login with email='{}' password='{}'",
                SUPER_ADMIN_EMAIL, SUPER_ADMIN_PASSWORD);
    }

    private void seedAdmin() {
        if (adminUserRepository.existsByEmail(DEV_EMAIL)) {
            log.info("Dev seed: admin user '{}' already exists — skipping", DEV_EMAIL);
            return;
        }

        log.info("Dev seed: creating admin user '{}' for tenant '{}'", DEV_EMAIL, DEV_TENANT_ID);

        RegisterRequest request = new RegisterRequest();
        request.setEmail(DEV_EMAIL);
        request.setPassword(DEV_PASSWORD);
        request.setConfirmPassword(DEV_PASSWORD);
        request.setFullName(DEV_FULL_NAME);
        request.setTenantId(DEV_TENANT_ID);

        adminAuthService.register(request);

        log.info("Dev seed: admin user created successfully. Login with email='{}' password='{}'",
                DEV_EMAIL, DEV_PASSWORD);
    }
}
