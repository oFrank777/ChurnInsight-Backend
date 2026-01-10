package com.alura.churninsight.domain.Usuario;

import jakarta.validation.constraints.NotBlank;

public record DatosAutenticacion(
        @NotBlank String correoElectronico,
        @NotBlank String contrasena
) {
}
