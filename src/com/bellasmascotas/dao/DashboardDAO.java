package com.bellasmascotas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Consultas de agregación (COUNT y SUM) para el panel de indicadores (FC11). */
public class DashboardDAO {

    /** Total de mascotas registradas. */
    public int getTotalMascotas() {
        return (int) valor("SELECT COUNT(*) FROM mascotas");
    }

    /** Clientes registrados hoy. */
    public int getNuevosClientesHoy() {
        return (int) valor("SELECT COUNT(*) FROM clientes WHERE fecha_registro = CURDATE()");
    }

    /** Suma de los servicios registrados hoy. */
    public double getVentasDelDia() {
        return valor("SELECT COALESCE(SUM(total), 0) FROM registros_estetica WHERE DATE(fecha) = CURDATE()");
    }

    /** Suma de los servicios del mes actual. */
    public double getIngresosDelMes() {
        return valor("SELECT COALESCE(SUM(total), 0) FROM registros_estetica "
                   + "WHERE MONTH(fecha) = MONTH(CURDATE()) AND YEAR(fecha) = YEAR(CURDATE())");
    }

    /** Suma de los gastos del mes actual. */
    public double getGastosDelMes() {
        return valor("SELECT COALESCE(SUM(monto), 0) FROM gastos_operativos "
                   + "WHERE MONTH(fecha_gasto) = MONTH(CURDATE()) AND YEAR(fecha_gasto) = YEAR(CURDATE())");
    }

    /** Citas de hoy que no están canceladas. */
    public int getCitasHoy() {
        return (int) valor("SELECT COUNT(*) FROM citas WHERE DATE(fecha_hora_cita) = CURDATE() AND estado <> 'Cancelada'");
    }

    /** Vacunas vencidas o que vencen en los próximos 30 días. */
    public int getVacunasPorVencer() {
        return (int) valor("SELECT COUNT(*) FROM control_vacunas WHERE proxima_dosis <= DATE_ADD(CURDATE(), INTERVAL 30 DAY)");
    }

    /** Insumos con stock igual o menor al umbral. */
    public int getInsumosPorReponer(int umbral) {
        String sql = "SELECT COUNT(*) FROM insumos WHERE cantidad_stock <= ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, umbral);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error en indicador: " + e.getMessage());
        }
        return 0;
    }

    private double valor(String sql) {
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("Error en indicador: " + e.getMessage());
        }
        return 0;
    }
}
