package com.bracits.surveyengine.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 120, message = "Full name must be between 2 and 120 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email")
    @Size(max = 255, message = "Email must be at most 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).+$",
            message = "Password must include uppercase, lowercase, number, and special character")
    private String password;

    @NotBlank(message = "Confirm password is required")
    @Size(max = 64, message = "Confirm password must be at most 64 characters")
    private String confirmPassword;

    /**
     * Optional for internal/trusted flows only.
     * Public registration ignores client-provided tenant ids and server-generates one.
     */
    @Size(max = 63, message = "Tenant ID must be at most 63 characters")
    private String tenantId;

    @AssertTrue(message = "Password and confirm password do not match")
    public boolean isPasswordMatch() {
        if (password == null || confirmPassword == null) {
            return true;
        }
        return password.equals(confirmPassword);
    }
}
