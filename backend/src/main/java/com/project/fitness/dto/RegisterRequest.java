package com.project.fitness.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 10, max = 72, message = "Password must be between 10 and 72 characters")
    private String password;
    @NotBlank(message = "First name is required")
    @Size(max = 80)
    private String firstName;
    @NotBlank(message = "Last name is required")
    @Size(max = 80)
    private String lastName;
}
