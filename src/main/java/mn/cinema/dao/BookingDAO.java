package mn.cinema.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import mn.cinema.db.DBConnection;
import mn.cinema.model.Booking;

public class BookingDAO {

    private static final String SELECT_BASE = """
        SELECT b.id, b.user_id, u.username, b.event_id, e.title AS event_title,
               b.seat_id, CONCAT(s.row_label, s.seat_number) AS seat_label,
               b.booking_date, b.status
        FROM bookings b
        JOIN users u ON b.user_id = u.id
        JOIN events e ON b.event_id = e.id
        JOIN seats s ON b.seat_id = s.id
    """;

    /** Тухайн суудал сул эсэхийг шалгаад захиалга үүсгэнэ. Давхардвал false буцаана. */
    public synchronized boolean createBooking(int userId, int eventId, int seatId) {
        return createBookings(userId, eventId, List.of(seatId));
    }

    /** Олон суудлыг бүгд сул үед нэг transaction-оор захиална. */
    public synchronized boolean createBookings(int userId, int eventId, List<Integer> seatIds) {
        String checkSql = "SELECT COUNT(*) FROM bookings WHERE event_id = ? AND seat_id = ? AND status = 'BOOKED'";
        String insertSql = "INSERT INTO bookings (user_id, event_id, seat_id, status) VALUES (?, ?, ?, 'BOOKED')";
        try (Connection conn = DBConnection.getConnection()) {
            boolean previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement check = conn.prepareStatement(checkSql);
                     PreparedStatement ins = conn.prepareStatement(insertSql)) {
                    for (int seatId : seatIds) {
                        check.setInt(1, eventId);
                        check.setInt(2, seatId);
                        try (ResultSet rs = check.executeQuery()) {
                            rs.next();
                            if (rs.getInt(1) > 0) {
                                conn.rollback();
                                return false;
                            }
                        }
                        ins.setInt(1, userId);
                        ins.setInt(2, eventId);
                        ins.setInt(3, seatId);
                        ins.addBatch();
                    }
                    ins.executeBatch();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(previousAutoCommit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelBooking(int bookingId) {
        String sql = "UPDATE bookings SET status = 'CANCELLED' WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Booking> findByUser(int userId) {
        List<Booking> list = new ArrayList<>();
        String sql = SELECT_BASE + " WHERE b.user_id = ? ORDER BY b.booking_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Booking> findAll() {
        List<Booking> list = new ArrayList<>();
        String sql = SELECT_BASE + " ORDER BY b.booking_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Хэрэглэгчийн нэр эсвэл тоглолтын нэрээр хайна (админд зориулсан). */
    public List<Booking> search(String keyword) {
        List<Booking> list = new ArrayList<>();
        String sql = SELECT_BASE + " WHERE u.username LIKE ? OR e.title LIKE ? ORDER BY b.booking_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setId(rs.getInt("id"));
        b.setUserId(rs.getInt("user_id"));
        b.setUsername(rs.getString("username"));
        b.setEventId(rs.getInt("event_id"));
        b.setEventTitle(rs.getString("event_title"));
        b.setSeatId(rs.getInt("seat_id"));
        b.setSeatLabel(rs.getString("seat_label"));
        Timestamp ts = rs.getTimestamp("booking_date");
        if (ts != null) b.setBookingDate(ts.toLocalDateTime());
        b.setStatus(rs.getString("status"));
        return b;
    }
}
