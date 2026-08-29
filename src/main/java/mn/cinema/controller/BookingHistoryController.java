package mn.cinema.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import mn.cinema.dao.BookingDAO;
import mn.cinema.model.Booking;
import mn.cinema.util.SessionManager;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class BookingHistoryController {

    @FXML private TableView<Booking> bookingTable;
    @FXML private TableColumn<Booking, String> eventColumn;
    @FXML private TableColumn<Booking, String> seatColumn;
    @FXML private TableColumn<Booking, String> dateColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    @FXML private Button cancelButton;

    private final BookingDAO bookingDAO = new BookingDAO();
    private final DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        eventColumn.setCellValueFactory(new PropertyValueFactory<>("eventTitle"));
        seatColumn.setCellValueFactory(new PropertyValueFactory<>("seatLabel"));
        dateColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getBookingDate() == null ? "" : c.getValue().getBookingDate().format(dtFmt)));
        statusColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                "BOOKED".equals(c.getValue().getStatus()) ? "Захиалагдсан" : "Цуцлагдсан"));

        loadData();

        bookingTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            cancelButton.setDisable(newV == null || !"BOOKED".equals(newV.getStatus()));
        });
        cancelButton.setDisable(true);
    }

    private void loadData() {
        int userId = SessionManager.getCurrentUser().getId();
        ObservableList<Booking> list = FXCollections.observableArrayList(bookingDAO.findByUser(userId));
        bookingTable.setItems(list);
    }

    @FXML
    private void handleCancelBooking() {
        Booking selected = bookingTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Энэ тасалбарыг цуцлахдаа итгэлтэй байна уу?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            boolean ok = bookingDAO.cancelBooking(selected.getId());
            if (ok) {
                loadData();
            } else {
                new Alert(Alert.AlertType.ERROR, "Тасалбар цуцлахад алдаа гарлаа.").showAndWait();
            }
        }
    }
}
