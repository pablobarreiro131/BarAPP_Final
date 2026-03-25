package org.pbarreiro.barapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerfilDTO {
    private UUID id;
    private String nombre;
    private String rol;
    private OffsetDateTime fechaCreacion;
}
