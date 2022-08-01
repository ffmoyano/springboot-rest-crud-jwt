package com.ffmoyano.jwtcrud;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class JwtCrudApplicationTests {


    @Test
    void contextLoads() {
    }

    @Test
    void generatePassword() {
        System.out.println("EL PASSWORD: " + new BCryptPasswordEncoder().encode("fernando"));
    }

}
