package com.WojciechWo.Wo.Woapp;

import com.WojciechWo.Wo.Woapp.user.User;
import com.WojciechWo.Wo.Woapp.user.UserRepo;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;


@Configuration


public class SecurityConfig {

    private final UserRepo userRepo;
    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(UserRepo userRepo, JwtTokenFilter jwtTokenFilter) {
        this.userRepo = userRepo;
        this.jwtTokenFilter = jwtTokenFilter;
    }
    @EventListener(ApplicationReadyEvent.class)
    public void saveUser() {
        User user1 = new User("test1", getBcryptPasswordEncoder().encode("password"), "ROLE_USER");
        userRepo.save(user1);
        User user2 = new User("test2", getBcryptPasswordEncoder().encode("password"), "ROLE_ADMIN");
        userRepo.save(user2);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepo.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with name not found: " + username));
    }

    @Bean
    public PasswordEncoder getBcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authorizationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues());
        http.authorizeRequests()
                .antMatchers("/auth/login").permitAll()
                .antMatchers("/Wo").hasRole("ADMIN")
                .anyRequest().authenticated();
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}


