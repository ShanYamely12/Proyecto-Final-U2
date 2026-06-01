package pe.edu.upeu.sysdenuncias.repository;

import pe.edu.upeu.sysdenuncias.enums.Cargo;
import pe.edu.upeu.sysdenuncias.enums.EstadoDenuncia;
import pe.edu.upeu.sysdenuncias.enums.Genero;
import pe.edu.upeu.sysdenuncias.model.Ciudadano;
import pe.edu.upeu.sysdenuncias.model.Denuncia;
import pe.edu.upeu.sysdenuncias.model.Funcionario;
import pe.edu.upeu.sysdenuncias.model.TipoDenuncia;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class DenunciaRepository extends AbstractJdbcRepository<Denuncia, Long> {

    @Override
    protected String getTableName() {
        return "denuncia";
    }

    @Override
    protected String getPkColumn() {
        return "id";
    }

    // Overriding findAll to use a JOIN to populate the associated objects
    @Override
    public List<Denuncia> findAll() {
        String sql = "SELECT d.id AS d_id, d.descripcion, d.fecha, d.ubicacion, d.estado, " +
                "c.id AS c_id, c.nombre AS c_nombre, c.dni AS c_dni, c.telefono AS c_telefono, c.direccion AS c_direccion, c.genero AS c_genero, " +
                "t.id AS t_id, t.nombre AS t_nombre, " +
                "f.id AS f_id, f.nombre AS f_nombre, f.cargo AS f_cargo " +
                "FROM denuncia d " +
                "JOIN ciudadano c ON d.ciudadano_id = c.id " +
                "JOIN tipo_denuncia t ON d.tipo_id = t.id " +
                "LEFT JOIN funcionario f ON d.funcionario_id = f.id";
        return executeQuery(sql);
    }

    @Override
    protected Denuncia insert(Connection connection, Denuncia entity) throws SQLException {
        long id = executeInsertGetKey(connection,
                "INSERT INTO denuncia(descripcion, fecha, ubicacion, estado, ciudadano_id, tipo_id, funcionario_id) VALUES(?,?,?,?,?,?,?)",
                entity.getDescripcion(),
                java.sql.Timestamp.valueOf(entity.getFecha()),
                entity.getUbicacion(),
                entity.getEstado() != null ? entity.getEstado().name() : null,
                entity.getCiudadano().getId(),
                entity.getTipoDenuncia().getId(),
                entity.getFuncionario() != null ? entity.getFuncionario().getId() : null
        );
        entity.setId(id);
        return entity;
    }

    @Override
    protected Denuncia updateRow(Connection connection, Denuncia entity) throws SQLException {
        executeUpdate(connection,
                "UPDATE denuncia SET descripcion=?, fecha=?, ubicacion=?, estado=?, ciudadano_id=?, tipo_id=?, funcionario_id=? WHERE id=?",
                entity.getDescripcion(),
                java.sql.Timestamp.valueOf(entity.getFecha()),
                entity.getUbicacion(),
                entity.getEstado() != null ? entity.getEstado().name() : null,
                entity.getCiudadano().getId(),
                entity.getTipoDenuncia().getId(),
                entity.getFuncionario() != null ? entity.getFuncionario().getId() : null,
                entity.getId()
        );
        return entity;
    }

    @Override
    protected Denuncia mapRow(ResultSet rs) throws SQLException {
        Ciudadano ciudadano = Ciudadano.builder()
                .id(rs.getLong("c_id"))
                .nombre(rs.getString("c_nombre"))
                .dni(rs.getString("c_dni"))
                .telefono(rs.getString("c_telefono"))
                .direccion(rs.getString("c_direccion"))
                .genero(rs.getString("c_genero") != null ? Genero.valueOf(rs.getString("c_genero")) : null)
                .build();

        TipoDenuncia tipoDenuncia = TipoDenuncia.builder()
                .id(rs.getLong("t_id"))
                .nombre(rs.getString("t_nombre"))
                .build();

        Funcionario funcionario = null;
        long fId = rs.getLong("f_id");
        if (!rs.wasNull()) {
            funcionario = Funcionario.builder()
                    .id(fId)
                    .nombre(rs.getString("f_nombre"))
                    .cargo(rs.getString("f_cargo") != null
                            ? Cargo.valueOf(rs.getString("f_cargo"))
                            : null)
                    .build();
        }

        return Denuncia.builder()
                .id(rs.getLong("d_id"))
                .descripcion(rs.getString("descripcion"))
                .fecha(rs.getTimestamp("fecha") != null ? rs.getTimestamp("fecha").toLocalDateTime() : null)
                .ubicacion(rs.getString("ubicacion"))
                .estado(rs.getString("estado") != null ? EstadoDenuncia.valueOf(rs.getString("estado")) : null)
                .ciudadano(ciudadano)
                .tipoDenuncia(tipoDenuncia)
                .funcionario(funcionario)
                .build();
    }
}
