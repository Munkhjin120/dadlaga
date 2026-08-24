package mn.cinema.dao;

import mn.cinema.db.DBConnection;
import mn.cinema.model.Event;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    private static final String SELECT_BASE =
            "SELECT e.*, h.name AS hall_name FROM events e JOIN halls h ON e.hall_id = h.id ";

    public int create(Event ev) {
        String sql = "INSERT INTO events (title, description, event_type, event_date, event_time, price, hall_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, ev);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean update(Event ev) {
        String sql = "UPDATE events SET title=?, description=?, event_type=?, event_date=?, event_time=?, price=?, hall_id=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, ev);
            ps.setInt(8, ev.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM events WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Одоо болон удахгүй болох (өнөөдрөөс хойшхи) тоглолт, кинонуудыг буцаана. */
    public List<Event> findUpcoming() {
        List<Event> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE e.event_date >= CURDATE() ORDER BY e.event_date, e.event_time";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Event> findAll() {
        List<Event> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY e.event_date DESC, e.event_time";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Event findById(int id) {
        String sql = SELECT_BASE + "WHERE e.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void bind(PreparedStatement ps, Event ev) throws SQLException {
        ps.setString(1, ev.getTitle());
        ps.setString(2, ev.getDescription());
        ps.setString(3, ev.getEventType());
        ps.setDate(4, Date.valueOf(ev.getEventDate()));
        ps.setTime(5, Time.valueOf(ev.getEventTime()));
        ps.setBigDecimal(6, ev.getPrice());
        ps.setInt(7, ev.getHallId());
    }

    private Event mapRow(ResultSet rs) throws SQLException {
        Event ev = new Event();
        ev.setId(rs.getInt("id"));
        ev.setTitle(rs.getString("title"));
        ev.setDescription(rs.getString("description"));
        ev.setEventType(rs.getString("event_type"));
        ev.setEventDate(rs.getDate("event_date").toLocalDate());
        ev.setEventTime(rs.getTime("event_time").toLocalTime());
        ev.setPrice(rs.getBigDecimal("price"));
        ev.setHallId(rs.getInt("hall_id"));
        ev.setHallName(rs.getString("hall_name"));
        return ev;
    }
}
