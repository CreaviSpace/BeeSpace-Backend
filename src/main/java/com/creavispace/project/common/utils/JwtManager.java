package com.creavispace.project.common.utils;

import com.creavispace.project.domain.jwt.dto.JwtDto;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class JwtManager {
    private static final Map<String, JwtDto> jsonToken = new ConcurrentHashMap<>();
    private static final List<String> blackList = new ArrayList<>();

    public static void storeToken(String tokenName, JwtDto jwtDto) {
        jsonToken.put(tokenName, jwtDto);
    }

    public static JwtDto findToken(String tokenName) {
        JwtDto token = null;
        if (jsonToken.containsKey(tokenName)) {
            token = jsonToken.remove(tokenName);
            System.out.println("token = " + token);
        }
        return token;
    }

    public static boolean isLogoutToken(String jwt) {
        boolean contains = blackList.contains(jwt);
        log.info("isLogoutToken? = {}", contains);
        return contains;
    }
}

