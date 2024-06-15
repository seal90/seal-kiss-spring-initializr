package io.github.seal90.kiss.gateway.login.controller;

import io.github.seal90.kiss.base.result.Result;
import io.github.seal90.kiss.core.constant.AppConstant;
import io.github.seal90.kiss.gateway.login.dto.LoginDTO;
import io.github.seal90.kiss.gateway.login.request.LoginRequest;
import io.github.seal90.kiss.gateway.login.response.LoginResponse;
import io.github.seal90.kiss.gateway.login.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public Mono<Result<LoginResponse>> login(ServerHttpRequest request, @RequestBody @Validated LoginRequest loginRequest) {
        HttpHeaders headers = request.getHeaders();
        String loginFlag = headers.getFirst(AppConstant.AUTH_HEADER_KEY);
        LoginDTO loginDTO = LoginDTO.builder().loginFlag(loginFlag).terminalType(loginRequest.getTerminalType())
                .userName(loginRequest.getUserName()).password(loginRequest.getPassword()).build();

        Mono<Result<String>> result = loginService.login(loginDTO);

        return result.map(stringResult -> {
            String data = stringResult.getData();
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAuthKey(data);

            return new Result<>(stringResult, loginResponse);
        });
    }
}
