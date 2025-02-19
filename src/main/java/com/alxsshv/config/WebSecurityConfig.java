package com.alxsshv.config;

import com.alxsshv.security.model.SystemSecurityRoles;
import com.alxsshv.security.service.implementation.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserServiceImpl();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                               .requestMatchers("/js/**", "/css/**", "/login", "/registration", "/users/registration").permitAll()
                                .requestMatchers("/organization/**").hasAnyRole(SystemSecurityRoles.USER.getName())
                                .requestMatchers("/journal/**", "/users/username", "/users/search").hasAnyRole(SystemSecurityRoles.VERIFICATION_EMPLOYEE.getName())
                                .requestMatchers("/settings/**", "/logs/**", "/journal/**", "/users/username", "/users/**").hasAnyRole(SystemSecurityRoles.SYSTEM_ADMIN.getName())
                                .anyRequest().authenticated())
                .formLogin(form -> form.loginPage("/login").permitAll())
                .exceptionHandling(form -> form.accessDeniedPage("/access_denied"))
                .build();
    }
}
