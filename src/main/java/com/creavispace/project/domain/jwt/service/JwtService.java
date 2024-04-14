package com.creavispace.project.domain.jwt.service;

import com.creavispace.project.domain.jwt.Entity.Jwt;
import java.util.Optional;
public interface JwtService {

    void saveRefreshToken(Jwt refreshJwt);

    Optional<Jwt> findById(String jwt);

    String createRefreshToken(String memberJwt);

}
