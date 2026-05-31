package pe.edu.upeu.sysdenuncias.repository;

import pe.edu.upeu.sysdenuncias.enums.Genero;
import pe.edu.upeu.sysdenuncias.model.Ciudadano;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CiudadanoRepository extends AbstractJdbcRepository<Ciudadano, Long> {

    @Override
    protected String getTableName() {
        return "ciudadano";
    }

    @Override
    protected String getPkColumn() {
        return "id";
    }

    @Override
    protected Ciudadano insert(Connection connection, Ciudadano entity) throws SQLException {
        long id = executeInsertGetKey(connection,
                "INSERT INTO ciudadano(nombre, dni, telefono, correo, direccion, genero) VALUES(?,?,?,?,?,?)",
                entity.getNombre(),
                entity.getDni(),
                entity.getTelefono(),
                entity.getCorreo(),
                entity.getDireccion(),
                entity.getGenero() != null ? entity.getGenero().name() : null
        );
        entity.setId(id);
        return entity;
    }

    @Override
    protected Ciudadano updateRow(Connection connection, Ciudadano entity) throws SQLException {
        executeUpdate(connection,
                "UPDATE ciudadano SET nombre=?, dni=?, telefono=?, correo=?, direccion=?, genero=? WHERE id=?",
                entity.getNombre(),
                entity.getDni(),
                entity.getTelefono(),
                entity.getCorreo(),
                entity.getDireccion(),
                entity.getGenero() != null ? entity.getGenero().name() : null,
                entity.getId()
        );
        return entity;
    }

    @Override
    protected Ciudadano mapRow(ResultSet rs) throws SQLException {
        return Ciudadano.builder()
                .id(rs.getLong("id"))
                .nombre(rs.getString("nombre"))
                .dni(rs.getString("dni"))
                .telefono(rs.getString("telefono"))
                .correo(rs.getString("correo"))
                .direccion(rs.getString("direccion"))
                .genero(rs.getString("genero") != null ? Genero.valueOf(rs.getString("genero")) : null)
                .build();
    }
}
