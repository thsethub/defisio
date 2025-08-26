package com.ufpe.defisio.linfedemapp.dto.user;

public record ResetPasswordRequestDTO(String email, String token, String newPassword) {
}
