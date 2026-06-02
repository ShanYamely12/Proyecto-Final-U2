package pe.edu.upeu.sysdenuncias.repository.helper;

import pe.edu.upeu.sysdenuncias.config.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public abstract class SqlHelper<T> {

    protected Connection getConnection() {
        return DatabaseConnection.getConnection();    }

    protected abstract T mapRow(ResultSet rs) throws SQLException;

    protected List<T> executeQuery(String sql, Object... params) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            setParams(ps, params);
            ResultSet rs = ps.executeQuery();
            List<T> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            rs.close();
            ps.close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error en query: " + e.getMessage(), e);
        }
    }

    protected Optional<T> executeQueryOne(String sql, Object... params) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            setParams(ps, params);
            ResultSet rs = ps.executeQuery();
            Optional<T> result = Optional.empty();
            if (rs.next()) {
                result = Optional.of(mapRow(rs));
            }
            rs.close();
            ps.close();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error en queryOne: " + e.getMessage(), e);
        }
    }

    protected boolean executeExists(String sql, Object id) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();
            rs.close();
            ps.close();
            return exists;
        } catch (SQLException e) {
            throw new RuntimeException("Error en exists: " + e.getMessage(), e);
        }
    }

    protected long executeInsertGetKey(Connection conn, String sql, Object... params) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        setParams(ps, params);
        ps.executeUpdate();
        ResultSet keys = ps.getGeneratedKeys();
        long key = -1;
        if (keys.next()) {
            key = keys.getLong(1);
        }
        keys.close();
        ps.close();
        if (key == -1) {
            throw new SQLException("No se generó clave para: " + sql);
        }
        return key;
    }

    protected void executeUpdate(Connection conn, String sql, Object... params) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        setParams(ps, params);
        ps.executeUpdate();
        ps.close();
    }

    protected int executeUpdateStandalone(String sql, Object... params) {
        try {
            Connection conn = getConnection();
            
            
            PreparedStatement ps = conn.prepareStatement(sql);
            setParams(ps, params);
            int rows = ps.executeUpdate();
            ps.close();
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException("Error en executeUpdate: " + e.getMessage(), e);
        }
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }
}