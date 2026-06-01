package pe.edu.upeu.sysdenuncias.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.sysdenuncias.enums.Cargo;

/**
 * Entidad Funcionario — representa al empleado de la institución
 * encargado de atender y gestionar las denuncias ciudadanas.
 *
 * Principio POO → Encapsulamiento:
 *   Atributos private con acceso controlado vía Lombok (@Data).
 *
 * También actúa como entidad de autenticación:
 *   El campo 'credenciales' almacena la contraseña del funcionario
 *   para el módulo de login del sistema.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario {

    private Long id;

    @NotBlank(message = "El nombre del funcionario es obligatorio")
    private String nombre;

    @NotBlank(message = "El cargo es obligatorio")
    private Cargo cargo;

    @NotBlank(message = "Las credenciales (contraseña) son obligatorias")
    private String credenciales;
}
