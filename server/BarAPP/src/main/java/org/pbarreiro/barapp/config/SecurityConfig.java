package org.pbarreiro.barapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configure(http))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/productos/**").hasAnyRole("admin", "camarero")
                .requestMatchers(HttpMethod.GET, "/api/categorias/**").hasAnyRole("admin", "camarero")

                .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("admin")
                .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("admin")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("admin")

                .requestMatchers(HttpMethod.POST, "/api/categorias/**").hasRole("admin")
                .requestMatchers(HttpMethod.PUT, "/api/categorias/**").hasRole("admin")
                .requestMatchers(HttpMethod.DELETE, "/api/categorias/**").hasRole("admin")

                .requestMatchers("/api/dashboard/**").hasRole("admin")

                .requestMatchers("/api/mesas/**").hasAnyRole("admin", "camarero")
                .requestMatchers("/api/comandas/**").hasAnyRole("admin", "camarero")
                .requestMatchers("/api/perfiles/**").hasAnyRole("admin", "camarero")

                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("role");
        
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
