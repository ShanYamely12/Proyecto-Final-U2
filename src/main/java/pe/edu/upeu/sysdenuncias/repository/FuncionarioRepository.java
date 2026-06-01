package pe.edu.upeu.sysdenuncias.repository;

import pe.edu.upeu.sysdenuncias.enums.Cargo;
import pe.edu.upeu.sysdenuncias.model.Funcionario;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class FuncionarioRepository extends AbstractJdbcRepository<Funcionario, Long> {

    @Override
    protected String getTableName() {
        return "funcionario";
    }

    @Override
    protected String getPkColumn() {
        return "id";
    }

    public Optional<Funcionario> findByCredenciales(String nombre, String credenciales) {
        return executeQueryOne("SELECT * FROM funcionario WHERE nombre = ? AND credenciales = ?", nombre, credenciales);
    }

    @Override
    protected Funcionario insert(Connection connection, Funcionario entity) throws SQLException {
        long id = executeInsertGetKey(connection,
                "INSERT INTO funcionario(nombre, cargo, credenciales) VALUES(?,?,?)",
                entity.getNombre(),
                entity.getCargo().name(),
                entity.getCredenciales()
        );
        entity.setId(id);
        return entity;
    }

    @Override
    protected Funcionario updateRow(Connection connection, Funcionario entity) throws SQLException {
        executeUpdate(connection,
                "UPDATE funcionario SET nombre=?, cargo=?, credenciales=? WHERE id=?",
                entity.getNombre(),
                entity.getCargo().name(),
                entity.getCredenciales(),
                entity.getId()
        );
        return entity;
    }

    @Override
    protected Funcionario mapRow(ResultSet rs) throws SQLException {
        return Funcionario.builder()
                .id(rs.getLong("id"))
                .nombre(rs.getString("nombre"))
                .cargo(
                        rs.getString("cargo") != null
                                ? Cargo.valueOf(rs.getString("cargo").trim().toUpperCase())
                                : null
                )
                .credenciales(rs.getString("credenciales"))
                .build();
    }
}