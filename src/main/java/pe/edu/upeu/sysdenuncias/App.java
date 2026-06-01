package pe.edu.upeu.sysdenuncias;

import pe.edu.upeu.sysdenuncias.enums.Cargo;
import pe.edu.upeu.sysdenuncias.model.Funcionario;
import pe.edu.upeu.sysdenuncias.repository.FuncionarioRepository;

public class App {

    public static void main(String[] args) {

        System.out.println("Iniciando Sistema de Gestión de Denuncias...");

        try {
            FuncionarioRepository funcRepo = new FuncionarioRepository();

            if (funcRepo.findByCredenciales("admin", "admin").isEmpty()) {

                Funcionario admin = Funcionario.builder()
                        .nombre("admin")
                        .cargo(Cargo.ADMINISTRADOR)
                        .credenciales("admin")
                        .build();

                funcRepo.save(admin);

                System.out.println("✅ Usuario Admin creado.");
            }

        } catch (Exception e) {
            System.out.println("ℹ Nota sobre Admin: " + e.getMessage());
        }

        SistemaDenunciasApplication.launch(
                SistemaDenunciasApplication.class,
                args
        );
    }
}