package org.pbarreiro.barapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffRequest {
    private String email;
    private String password;
    private String nombre;
    private String rol;
}
