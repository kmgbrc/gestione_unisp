package it.unisp.auth;

import jakarta.validation.constraints.NotBlank;

public class ChangePasswordRequest {

    @NotBlank(message = "La password attuale è obbligatoria.")
    private String currentPassword;

    @NotBlank(message = "La nuova password è obbligatoria.")
    private String newPassword;

    // Getters e Setters
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
