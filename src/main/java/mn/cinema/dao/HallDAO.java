package mn.cinema.dao;

import mn.cinema.db.DBConnection;
import mn.cinema.model.Hall;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HallDAO {

    /** Танхим үүсгээд, мөр/баганы тоогоор нь суудлуудыг автоматаар үүсгэнэ. */
    public int create(Hall hall) {
        String sql = "INSERT INTO halls (name, rows_count, cols_count) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, hall.getName());
            ps.setInt(2, hall.getRowsCount());
            ps.setInt(3, hall.getColsCount());
            ps.executeUpdate();
            int hallId;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                hallId = keys.getInt(1);
            }
            generateSeats(conn, hallId, hall.getRowsCount(), hall.getColsCount());
            return hallId;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void generateSeats(Connection conn, int hallId, int rows, int cols) throws SQLException {
        String sql = "INSERT INTO seats (hall_id, row_label, seat_number) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int r = 0; r < rows; r++) {
                String rowLabel = String.valueOf((char) ('A' + r));
                for (int c = 1; c <= cols; c++) {
                    ps.setInt(1, hallId);
                    ps.setString(2, rowLabel);
                    ps.setInt(3, c);
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        }
    }

    public boolean update(Hall hall, boolean regenerateSeats) {
        String sql = "UPDATE halls SET name = ?, rows_count = ?, cols_count = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hall.getName());
            ps.setInt(2, hall.getRowsCount());
            ps.setInt(3, hall.getColsCount());
            ps.setInt(4, hall.getId());
            ps.executeUpdate();
            if (regenerateSeats) {
                try (PreparedStatement del = conn.prepareStatement("DELETE FROM seats WHERE hall_id = ?")) {
                    del.setInt(1, hall.getId());
                    del.executeUpdate();
                }
                generateSeats(conn, hall.getId(), hall.getRowsCount(), hall.getColsCount());
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int hallId) {
        String sql = "DELETE FROM halls WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hallId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Hall> findAll() {
        List<Hall> list = new ArrayList<>();
        String sql = "SELECT * FROM halls ORDER BY id";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Hall(rs.getInt("id"), rs.getString("name"),
                        rs.getInt("rows_count"), rs.getInt("cols_count")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Hall findById(int id) {
        String sql = "SELECT * FROM halls WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Hall(rs.getInt("id"), rs.getString("name"),
                            rs.getInt("rows_count"), rs.getInt("cols_count"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
