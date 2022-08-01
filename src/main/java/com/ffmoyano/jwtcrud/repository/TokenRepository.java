package com.ffmoyano.jwtcrud.repository;

import com.ffmoyano.jwtcrud.entity.Token;
import com.ffmoyano.jwtcrud.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Token findTokenByUser(AppUser idunnUser);
    Token findTokenByRefreshToken(String refreshToken);
}
