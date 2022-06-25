package com.ffmoyano.idunn.configuration;

import com.ffmoyano.idunn.filter.CustomAuthenticationFilter;
import com.ffmoyano.idunn.service.TokenService;
import com.ffmoyano.idunn.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfiguration {


    private final UserService userService;


    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    public SecurityConfiguration(UserService userService, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        CustomAuthenticationFilter customAuthenticationFilter =
                new CustomAuthenticationFilter(authenticationManager, userService, tokenService);


        http
                .cors().and().csrf().disable()
                .authorizeRequests(authorize ->
                        authorize
                                .antMatchers("/user/**").hasRole("USER")
                                .antMatchers("/**").permitAll()
                                .anyRequest().authenticated())
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(customAuthenticationFilter)
                .addFilterAfter(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }

}
