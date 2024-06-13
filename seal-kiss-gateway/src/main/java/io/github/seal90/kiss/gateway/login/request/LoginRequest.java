package io.github.seal90.kiss.gateway.login.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    private String terminalType;

    @NotBlank
    private String userName;

    @NotBlank
    private String password;
}
