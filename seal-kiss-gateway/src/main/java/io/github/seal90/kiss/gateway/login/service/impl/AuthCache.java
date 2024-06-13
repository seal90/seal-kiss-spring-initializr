package io.github.seal90.kiss.gateway.login.service.impl;

import java.util.*;

public class AuthCache {

    public static final Map<String, String> authKeyToUser = new HashMap<>();

    public static final Map<String, Set<String>> userToAuthKeys = new HashMap<>();

    public static final Map<String, String> userToUserInfo = new HashMap<>();

    public static final Map<String, BitSet> userToAuthority = new HashMap<>();

    public static final Map<String, Integer> pathAuthKeyToId = new HashMap<>();
}
