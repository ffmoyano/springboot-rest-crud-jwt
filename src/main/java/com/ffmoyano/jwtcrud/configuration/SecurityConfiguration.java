package com.ffmoyano.jwtcrud.configuration;

import com.ffmoyano.jwtcrud.filter.CustomAuthenticationFilter;
import com.ffmoyano.jwtcrud.filter.CustomAuthorizationFilter;
import com.ffmoyano.jwtcrud.service.TokenService;
import com.ffmoyano.jwtcrud.service.UserService;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfiguration {

    private final Logger logger;
    private final UserService userService;

    private final AppPropertiesConfiguration appPropertiesConfiguration;
    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;


    public SecurityConfiguration(Logger logger, UserService userService, AppPropertiesConfiguration appPropertiesConfiguration, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.logger = logger;
        this.userService = userService;
        this.appPropertiesConfiguration = appPropertiesConfiguration;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;

    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        CustomAuthenticationFilter customAuthenticationFilter =
                new CustomAuthenticationFilter(authenticationManager, userService, tokenService);

        CustomAuthorizationFilter customAuthorizationFilter =
                new CustomAuthorizationFilter(logger, appPropertiesConfiguration);

        http
                .cors().and().csrf().disable()
                .authorizeRequests(authorize ->
                        authorize
                                .antMatchers("/adventurer/**").hasRole("USER")
                                .antMatchers("/**").permitAll()
                                .anyRequest().authenticated())
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // first we add the customAuthenticationFilter and after it the customAuthorizationFilter
                .addFilter(customAuthenticationFilter)
                // the first parameter is the filter,
                // the second is the class of the preceding filter (customAuthenticationFilter)
                .addFilterAfter(customAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }

}
