package com.authLogin.AuthLogin.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }



    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        http.authorizeHttpRequests((authorize) ->
//                        //authorize.anyRequest().authenticated()
//                        authorize.requestMatchers(HttpMethod.GET, "/ashram/api/**").permitAll()
//                                .requestMatchers("/api/auth/v1/signin").permitAll()
//                                .requestMatchers("/api/auth/v1/**").permitAll()
//                                .anyRequest().authenticated()
//
//                );
//
//        return http.build();
//    }
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest()
                    .permitAll())
            .csrf(AbstractHttpConfigurer::disable);
    return http.build();
}
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3001"); // Adjust the origin as needed
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
