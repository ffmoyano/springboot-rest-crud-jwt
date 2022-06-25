package com.ffmoyano.idunn.repository;

import com.ffmoyano.idunn.entity.Token;
import com.ffmoyano.idunn.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Token findTokenByUser(AppUser idunnUser);
    Token findTokenByRefreshToken(String refreshToken);
}
