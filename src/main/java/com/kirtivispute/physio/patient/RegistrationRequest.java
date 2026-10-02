package com.kirtivispute.physio.patient;

import jakarta.validation.constraints.*;
import java.util.Locale;

public class RegistrationRequest {
    @NotBlank(message = "Enter your full name.")
    @Size(max = 100, message = "Use a name of at most 100 characters.")
    private String fullName;
    @NotBlank(message = "Enter your email address.")
    @Email(message = "Enter a valid email address.")
    @Size(max = 254, message = "Use an email of at most 254 characters.")
    private String email;
    @NotBlank(message = "Enter a password.")
    @Size(min = 8, max = 128, message = "Use a password with 8 to 128 characters.")
    private String password;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName == null ? null : fullName.strip(); }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null ? null : email.strip().toLowerCase(Locale.ROOT); }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
