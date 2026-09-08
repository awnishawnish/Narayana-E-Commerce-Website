package NarayanGroup.example.E_Commerce.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@AllArgsConstructor
@Lazy
public class SecurityConfig {

    private final AuthenticationFilter authenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/api/user",
                                "/api/user/**","/api/product",
                                "/api/product/**","/login/**","/api/userId/cart","/api/userId/cart/**","/api/checkout/**","/api/address/**")
                        .permitAll()
                        .anyRequest().authenticated()
                )

//                        .requestMatchers("/login", "/refresh-token", "/logout").permitAll()
//
//                        .requestMatchers("/api/user/**",
//                                "/api/usergenerateOtp",
//                                "/api/user/verify-otp",
//                                "/api/user/resetPassword",
//                                "/api/user/saveUserDetails").permitAll()
//
//                        .requestMatchers("/oauth2/**",
//                                "/api/user/google",
//                                "/api/user/login-failure").permitAll()
//
//
//                                .requestMatchers("api/product/**").permitAll()

//                        .requestMatchers("/api/product/add",
//                                "/api/product/add-bulk",
//                                "/api/product/update/**",
//                                "/api/product/delete/**").hasAuthority("ADMIN")
//
//                        .requestMatchers("/api/product/*/quantity").hasAuthority("ADMIN")
//
//                        .requestMatchers("/api/product/all",
//                                "/api/product/search",
//                                "/api/product/**").hasAnyAuthority("USER", "ADMIN")
//
//                        .requestMatchers("/api/user/all").hasAuthority("ADMIN")


                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(authenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:5173"));

        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );

        config.setAllowedHeaders(List.of("*"));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
