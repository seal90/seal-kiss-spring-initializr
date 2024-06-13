package io.github.seal90.kiss.gateway.login.service;

import io.github.seal90.kiss.base.result.Result;
import io.github.seal90.kiss.gateway.login.dto.LoginDTO;
import reactor.core.publisher.Mono;

public interface LoginService {

    Mono<Result<String>> login(LoginDTO loginDTO);
}
