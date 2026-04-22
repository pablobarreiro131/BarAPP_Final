package org.pbarreiro.barapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class SupabaseUserRequest {
    private String email;
    private String password;
    
    @JsonProperty("email_confirm")
    private boolean emailConfirm;
    
    private boolean confirm;
    
    @JsonProperty("user_metadata")
    private Map<String, Object> userMetadata;
}
