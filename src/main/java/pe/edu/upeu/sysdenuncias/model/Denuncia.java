package pe.edu.upeu.sysdenuncias.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.sysdenuncias.enums.EstadoDenuncia;

import java.time.LocalDateTime;

/**
 * Entidad Denuncia — registra el reporte del ciudadano.
 *
 * Principio POO → Asociación y Encapsulamiento:
 *   En lugar de usar llaves foráneas crudas (Long ciudadanoId),
 *   relacionamos objetos completos (Ciudadano, TipoDenuncia, Funcionario).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Denuncia {


    private Long id;

    @NotBlank(message = "La descripción de la denuncia es obligatoria")
    private String descripcion;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDateTime fecha;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    @NotNull(message = "El estado de la denuncia es obligatorio")
    private EstadoDenuncia estado;

    // Relaciones (Asociación en POO)
    @NotNull(message = "El ciudadano es obligatorio")
    private Ciudadano ciudadano;

    @NotNull(message = "El tipo de denuncia es obligatorio")
    private TipoDenuncia tipoDenuncia;

    // El funcionario puede ser asignado después (no NotNull inicialmente)
    private Funcionario funcionario;

    private String observacion;

}
