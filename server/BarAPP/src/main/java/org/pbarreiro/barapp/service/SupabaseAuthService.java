package org.pbarreiro.barapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
import org.pbarreiro.barapp.dto.SupabaseUserRequest;

@Service
@RequiredArgsConstructor
public class SupabaseAuthService {

    @Value("${SUPABASE_JWT_ISSUER_URI}")
    private String issuerUri;

    @Value("${SUPABASE_SERVICE_ROLE_KEY}")
    private String serviceRoleKey;

    private final RestClient restClient = RestClient.builder().build();

    public String createAdminUser(String email, String password, String role) {
        String url = issuerUri + "/admin/users";

        SupabaseUserRequest body = SupabaseUserRequest.builder()
                .email(email)
                .password(password)
                .emailConfirm(true)
                .confirm(true)
                .userMetadata(Map.of("role", role))
                .build();

        Map<String, Object> response = restClient.post()
                .uri(url)
                .header("Authorization", "Bearer " + serviceRoleKey)
                .header("apikey", serviceRoleKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

        if (response != null && response.containsKey("id")) {
            return (String) response.get("id");
        }
        
        throw new RuntimeException("Error al crear usuario en Supabase: " + response);
    }
}
