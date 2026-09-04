package mn.cinema.controller;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import mn.cinema.dao.BookingDAO;
import mn.cinema.dao.HallDAO;
import mn.cinema.dao.SeatDAO;
import mn.cinema.model.Event;
import mn.cinema.model.Hall;
import mn.cinema.model.Seat;
import mn.cinema.util.SessionManager;

public class SeatSelectionController {

    @FXML private Label eventTitleLabel;
    @FXML private Label eventInfoLabel;
    @FXML private GridPane seatGrid;
    @FXML private Label selectedSeatLabel;
    @FXML private Button confirmButton;
    @FXML private VBox legendBox;

    private final HallDAO hallDAO = new HallDAO();
    private final SeatDAO seatDAO = new SeatDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    private Event event;
    private final Map<Integer, Seat> selectedSeats = new LinkedHashMap<>();

    public void setEvent(Event event) {
        this.event = event;
        render();
    }

    private void render() {
        eventTitleLabel.setText(event.getTitle());
        DateTimeFormatter dFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter tFmt = DateTimeFormatter.ofPattern("HH:mm");
        eventInfoLabel.setText(event.getTypeLabel() + " | " + event.getEventDate().format(dFmt) + " " +
                event.getEventTime().format(tFmt) + " | Танхим: " + event.getHallName() +
                " | Үнэ: " + event.getPrice() + "₮");

        seatGrid.getChildren().clear();
        selectedSeats.clear();
        selectedSeatLabel.setText("Сонгосон суудал: -");
        confirmButton.setDisable(true);

        Hall hall;
        List<Seat> seats;
        try {
            hall = hallDAO.findById(event.getHallId());
            seats = seatDAO.findByHallAndEvent(event.getHallId(), event.getId());
        } catch (Exception e) {
            e.printStackTrace();
            seatGrid.add(new Label("Суудлын мэдээлэл татахад алдаа гарлаа: " + e.getMessage()), 0, 0);
            return;
        }

        if (hall == null) {
            seatGrid.add(new Label("Энэ тоглолтод холбогдсон танхим олдсонгүй. Админтай холбогдоно уу."), 0, 0);
            return;
        }
        if (seats.isEmpty()) {
            seatGrid.add(new Label("'" + hall.getName() + "' танхимд суудал тохируулаагүй байна.\n" +
                    "Админ панелаас Танхим засаж, мөр/баганы тоог дахин оруулна уу."), 0, 0);
            return;
        }

        // Дэлгэц заавар
        Label screenLabel = new Label("---------- ДЭЛГЭЦ / ТАЙЗ ----------");
        screenLabel.getStyleClass().add("screen-label");
        seatGrid.add(screenLabel, 0, 0, hall.getColsCount(), 1);

        for (Seat seat : seats) {
            int rowIndex = seat.getRowLabel().charAt(0) - 'A' + 1; // 0-р мөр screen
            int colIndex = seat.getSeatNumber() - 1;

            ToggleButton btn = new ToggleButton(seat.getLabel());
            btn.getStyleClass().add("seat-button");
            if (seat.isBooked()) {
                btn.getStyleClass().add("seat-booked");
                btn.setDisable(true);
            } else {
                btn.getStyleClass().add("seat-free");
                btn.setOnAction(e -> handleSeatClick(btn, seat));
            }
            seatGrid.add(btn, colIndex, rowIndex);
        }
    }

    private void handleSeatClick(ToggleButton clicked, Seat seat) {
        if (clicked.isSelected()) {
            selectedSeats.put(seat.getId(), seat);
        } else {
            selectedSeats.remove(seat.getId());
        }
        updateSelectionSummary();
    }

    private void updateSelectionSummary() {
        if (selectedSeats.isEmpty()) {
            selectedSeatLabel.setText("Сонгосон суудал: -");
            confirmButton.setDisable(true);
            return;
        }
        String labels = selectedSeats.values().stream()
                .map(Seat::getLabel)
                .reduce((left, right) -> left + ", " + right)
                .orElse("-");
        selectedSeatLabel.setText("Сонгосон суудал (" + selectedSeats.size() + "): " + labels);
        confirmButton.setDisable(false);
    }

    @FXML
    private void handleConfirmBooking() {
        if (selectedSeats.isEmpty() || event == null) return;
        int userId = SessionManager.getCurrentUser().getId();
        boolean success = bookingDAO.createBookings(userId, event.getId(),
            selectedSeats.keySet().stream().toList());
        if (success) {
            new Alert(Alert.AlertType.INFORMATION, "Тасалбар амжилттай захиалагдлаа!\nСуудал: " +
                selectedSeats.values().stream().map(Seat::getLabel)
                    .reduce((left, right) -> left + ", " + right).orElse("-")).showAndWait();
            render(); // дахин ачаалж, шинэ төлвийг харуулна
        } else {
            new Alert(Alert.AlertType.WARNING, "Уучлаарай, энэ суудал өөр хэрэглэгчээр захиалагдчихсан байна. " +
                    "Дэлгэц шинэчлэгдэж байна.").showAndWait();
            render();
        }
    }
}
