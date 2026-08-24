package mn.cinema.dao;

import mn.cinema.db.DBConnection;
import mn.cinema.model.Seat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {

    /** Тухайн танхимын бүх суудлыг, тухайн тоглолтод захиалагдсан эсэхийг тэмдэглэсэн байдлаар авна. */
    public List<Seat> findByHallAndEvent(int hallId, int eventId) {
        List<Seat> list = new ArrayList<>();
        String sql = """
            SELECT s.id, s.hall_id, s.row_label, s.seat_number,
                   CASE WHEN b.id IS NULL THEN 0 ELSE 1 END AS is_booked
            FROM seats s
            LEFT JOIN bookings b
                   ON b.seat_id = s.id
                  AND b.event_id = ?
                  AND b.status = 'BOOKED'
            WHERE s.hall_id = ?
            ORDER BY s.row_label, s.seat_number
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, hallId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Seat s = new Seat(rs.getInt("id"), rs.getInt("hall_id"),
                            rs.getString("row_label"), rs.getInt("seat_number"));
                    s.setBooked(rs.getInt("is_booked") == 1);
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Seat findById(int id) {
        String sql = "SELECT * FROM seats WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Seat(rs.getInt("id"), rs.getInt("hall_id"),
                            rs.getString("row_label"), rs.getInt("seat_number"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
