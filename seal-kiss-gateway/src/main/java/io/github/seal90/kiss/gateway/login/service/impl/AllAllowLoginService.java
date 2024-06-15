package io.github.seal90.kiss.gateway.login.service.impl;

import io.github.seal90.kiss.base.result.Result;
import io.github.seal90.kiss.gateway.login.dto.LoginDTO;
import io.github.seal90.kiss.gateway.login.service.LoginService;
import reactor.core.publisher.Mono;

import java.util.*;

public class AllAllowLoginService implements LoginService {

    @Override
    public Mono<Result<String>> login(LoginDTO loginDTO) {
        String authKey = UUID.randomUUID().toString().replace("-", "");
        String userId = "USER_ID";
        String userInfo = "USER_INFO";

        BitSet userAuthority = new BitSet();
        for(int i=0; i<10;i++) {
            userAuthority.set(i);
        }

        AuthCache.authKeyToUser.put(authKey, userId);

        Map<String, Set<String>> userToAuthKeys = AuthCache.userToAuthKeys;
        Set<String> authKeys = userToAuthKeys.get(userId);
        if(null == authKeys) {
            HashSet<String> hashSet = new HashSet<>();
            hashSet.add(authKey);
            userToAuthKeys.put(userId, hashSet);
        } else {
            authKeys.add(authKey);
        }

        AuthCache.userToUserInfo.put(userId, userInfo);
        AuthCache.userToAuthority.put(userId, userAuthority);


        return Mono.just(Result.ok(authKey));
    }
}
