package io.github.seal90.kiss.gateway.login.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginDTO {

    private String loginFlag;

    private String terminalType;

    private String userName;

    private String password;
}
