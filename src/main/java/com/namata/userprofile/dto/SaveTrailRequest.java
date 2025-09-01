package com.namata.userprofile.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveTrailRequest {
    
    @NotNull(message = "Trail ID é obrigatório")
    private UUID trailId;
    
    private String notes; // Notas opcionais do usuário
}