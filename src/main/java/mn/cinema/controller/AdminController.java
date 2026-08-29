package mn.cinema.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import mn.cinema.dao.*;
import mn.cinema.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class AdminController {

    // ---- Events tab ----
    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, String> evTitleCol;
    @FXML private TableColumn<Event, String> evTypeCol;
    @FXML private TableColumn<Event, String> evDateCol;
    @FXML private TableColumn<Event, String> evTimeCol;
    @FXML private TableColumn<Event, String> evHallCol;
    @FXML private TableColumn<Event, String> evPriceCol;

    @FXML private TextField evTitleField;
    @FXML private TextArea evDescField;
    @FXML private ComboBox<String> evTypeCombo;
    @FXML private DatePicker evDatePicker;
    @FXML private TextField evTimeField; // HH:mm
    @FXML private TextField evPriceField;
    @FXML private ComboBox<Hall> evHallCombo;
    @FXML private Label evErrorLabel;

    // ---- Halls tab ----
    @FXML private TableView<Hall> hallTable;
    @FXML private TableColumn<Hall, String> hallNameCol;
    @FXML private TableColumn<Hall, Integer> hallRowsCol;
    @FXML private TableColumn<Hall, Integer> hallColsCol;
    @FXML private TableColumn<Hall, Integer> hallSeatCountCol;

    @FXML private TextField hallNameField;
    @FXML private TextField hallRowsField;
    @FXML private TextField hallColsField;
    @FXML private Label hallErrorLabel;

    // ---- Bookings tab ----
    @FXML private TextField bookingSearchField;
    @FXML private TableView<Booking> bookingTable;
    @FXML private TableColumn<Booking, String> bkUserCol;
    @FXML private TableColumn<Booking, String> bkEventCol;
    @FXML private TableColumn<Booking, String> bkSeatCol;
    @FXML private TableColumn<Booking, String> bkDateCol;
    @FXML private TableColumn<Booking, String> bkStatusCol;

    // ---- Users tab ----
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> usernameCol;
    @FXML private TableColumn<User, String> roleCol;

    private final EventDAO eventDAO = new EventDAO();
    private final HallDAO hallDAO = new HallDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final UserDAO userDAO = new UserDAO();

    private final DateTimeFormatter dFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter tFmt = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        initEventsTab();
        initHallsTab();
        initBookingsTab();
        initUsersTab();
    }

    // ===================== EVENTS =====================
    private void initEventsTab() {
        evTypeCombo.setItems(FXCollections.observableArrayList("MOVIE", "CONCERT"));
        evTypeCombo.getSelectionModel().selectFirst();

        evTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        evTypeCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTypeLabel()));
        evDateCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getEventDate() == null ? "" : c.getValue().getEventDate().format(dFmt)));
        evTimeCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getEventTime() == null ? "" : c.getValue().getEventTime().format(tFmt)));
        evHallCol.setCellValueFactory(new PropertyValueFactory<>("hallName"));
        evPriceCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getPrice() == null ? "" : c.getValue().getPrice().toString()));

        refreshEvents();
        refreshHallCombo();

        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) fillEventForm(newV);
        });
    }

    private void refreshEvents() {
        ObservableList<Event> list = FXCollections.observableArrayList(eventDAO.findAll());
        eventTable.setItems(list);
    }

    private void refreshHallCombo() {
        ObservableList<Hall> halls = FXCollections.observableArrayList(hallDAO.findAll());
        evHallCombo.setItems(halls);
    }

    private void fillEventForm(Event ev) {
        evTitleField.setText(ev.getTitle());
        evDescField.setText(ev.getDescription());
        evTypeCombo.getSelectionModel().select(ev.getEventType());
        evDatePicker.setValue(ev.getEventDate());
        evTimeField.setText(ev.getEventTime() == null ? "" : ev.getEventTime().format(tFmt));
        evPriceField.setText(ev.getPrice() == null ? "" : ev.getPrice().toString());
        for (Hall h : evHallCombo.getItems()) {
            if (h.getId() == ev.getHallId()) {
                evHallCombo.getSelectionModel().select(h);
                break;
            }
        }
    }

    @FXML
    private void handleClearEventForm() {
        eventTable.getSelectionModel().clearSelection();
        evTitleField.clear();
        evDescField.clear();
        evTypeCombo.getSelectionModel().selectFirst();
        evDatePicker.setValue(null);
        evTimeField.clear();
        evPriceField.clear();
        evHallCombo.getSelectionModel().clearSelection();
        evErrorLabel.setText("");
    }

    @FXML
    private void handleAddEvent() {
        Event ev = buildEventFromForm();
        if (ev == null) return;
        int id = eventDAO.create(ev);
        if (id > 0) {
            refreshEvents();
            handleClearEventForm();
        } else {
            evErrorLabel.setText("Кино/тоглолт нэмэхэд алдаа гарлаа.");
        }
    }

    @FXML
    private void handleUpdateEvent() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            evErrorLabel.setText("Засах мөрөө сонгоно уу.");
            return;
        }
        Event ev = buildEventFromForm();
        if (ev == null) return;
        ev.setId(selected.getId());
        boolean ok = eventDAO.update(ev);
        if (ok) {
            refreshEvents();
            handleClearEventForm();
        } else {
            evErrorLabel.setText("Шинэчлэхэд алдаа гарлаа.");
        }
    }

    @FXML
    private void handleDeleteEvent() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            evErrorLabel.setText("Устгах мөрөө сонгоно уу.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "'" + selected.getTitle() + "' устгах уу? Холбогдох захиалгууд ч устана.", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            eventDAO.delete(selected.getId());
            refreshEvents();
            handleClearEventForm();
        }
    }

    private Event buildEventFromForm() {
        evErrorLabel.setText("");
        String title = evTitleField.getText() == null ? "" : evTitleField.getText().trim();
        String timeText = evTimeField.getText() == null ? "" : evTimeField.getText().trim();
        String priceText = evPriceField.getText() == null ? "" : evPriceField.getText().trim();
        Hall hall = evHallCombo.getSelectionModel().getSelectedItem();
        LocalDate date = evDatePicker.getValue();

        if (title.isEmpty() || date == null || timeText.isEmpty() || priceText.isEmpty() || hall == null) {
            evErrorLabel.setText("Бүх талбарыг бөглөнө үү (Цаг: HH:mm форматтай).");
            return null;
        }
        LocalTime time;
        BigDecimal price;
        try {
            time = LocalTime.parse(timeText, tFmt);
        } catch (Exception e) {
            evErrorLabel.setText("Цагийн формат буруу байна. Жишээ: 18:30");
            return null;
        }
        try {
            price = new BigDecimal(priceText);
        } catch (Exception e) {
            evErrorLabel.setText("Үнийн формат буруу байна.");
            return null;
        }

        Event ev = new Event();
        ev.setTitle(title);
        ev.setDescription(evDescField.getText());
        ev.setEventType(evTypeCombo.getSelectionModel().getSelectedItem());
        ev.setEventDate(date);
        ev.setEventTime(time);
        ev.setPrice(price);
        ev.setHallId(hall.getId());
        return ev;
    }

    // ===================== HALLS =====================
    private void initHallsTab() {
        hallNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        hallRowsCol.setCellValueFactory(new PropertyValueFactory<>("rowsCount"));
        hallColsCol.setCellValueFactory(new PropertyValueFactory<>("colsCount"));
        hallSeatCountCol.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getSeatCount()));

        refreshHalls();

        hallTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                hallNameField.setText(newV.getName());
                hallRowsField.setText(String.valueOf(newV.getRowsCount()));
                hallColsField.setText(String.valueOf(newV.getColsCount()));
            }
        });
    }

    private void refreshHalls() {
        ObservableList<Hall> list = FXCollections.observableArrayList(hallDAO.findAll());
        hallTable.setItems(list);
        refreshHallCombo();
    }

    @FXML
    private void handleClearHallForm() {
        hallTable.getSelectionModel().clearSelection();
        hallNameField.clear();
        hallRowsField.clear();
        hallColsField.clear();
        hallErrorLabel.setText("");
    }

    @FXML
    private void handleAddHall() {
        Hall hall = buildHallFromForm();
        if (hall == null) return;
        int id = hallDAO.create(hall);
        if (id > 0) {
            refreshHalls();
            handleClearHallForm();
        } else {
            hallErrorLabel.setText("Танхим нэмэхэд алдаа гарлаа.");
        }
    }

    @FXML
    private void handleUpdateHall() {
        Hall selected = hallTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            hallErrorLabel.setText("Засах мөрөө сонгоно уу.");
            return;
        }
        Hall hall = buildHallFromForm();
        if (hall == null) return;
        hall.setId(selected.getId());
        boolean seatsChanged = hall.getRowsCount() != selected.getRowsCount() || hall.getColsCount() != selected.getColsCount();
        if (seatsChanged) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Мөр/баганы тоо өөрчлөгдвөл энэ танхимын бүх суудал болон холбогдох захиалгууд дахин үүснэ. Үргэлжлүүлэх үү?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText(null);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.YES) return;
        }
        boolean ok = hallDAO.update(hall, seatsChanged);
        if (ok) {
            refreshHalls();
            handleClearHallForm();
        } else {
            hallErrorLabel.setText("Шинэчлэхэд алдаа гарлаа.");
        }
    }

    @FXML
    private void handleDeleteHall() {
        Hall selected = hallTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            hallErrorLabel.setText("Устгах мөрөө сонгоно уу.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "'" + selected.getName() + "' танхимыг устгах уу? Холбогдох кино/тоглолт, захиалгууд ч устана.",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            hallDAO.delete(selected.getId());
            refreshHalls();
            refreshEvents();
            handleClearHallForm();
        }
    }

    private Hall buildHallFromForm() {
        hallErrorLabel.setText("");
        String name = hallNameField.getText() == null ? "" : hallNameField.getText().trim();
        String rowsText = hallRowsField.getText() == null ? "" : hallRowsField.getText().trim();
        String colsText = hallColsField.getText() == null ? "" : hallColsField.getText().trim();
        if (name.isEmpty() || rowsText.isEmpty() || colsText.isEmpty()) {
            hallErrorLabel.setText("Бүх талбарыг бөглөнө үү.");
            return null;
        }
        int rows, cols;
        try {
            rows = Integer.parseInt(rowsText);
            cols = Integer.parseInt(colsText);
            if (rows <= 0 || cols <= 0 || rows > 26) {
                hallErrorLabel.setText("Мөр 1-26, багана эерэг тоо байх ёстой.");
                return null;
            }
        } catch (NumberFormatException e) {
            hallErrorLabel.setText("Мөр/багана тоо байх ёстой.");
            return null;
        }
        return new Hall(0, name, rows, cols);
    }

    // ===================== BOOKINGS =====================
    private void initBookingsTab() {
        bkUserCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        bkEventCol.setCellValueFactory(new PropertyValueFactory<>("eventTitle"));
        bkSeatCol.setCellValueFactory(new PropertyValueFactory<>("seatLabel"));
        bkDateCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getBookingDate() == null ? "" : c.getValue().getBookingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        bkStatusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                "BOOKED".equals(c.getValue().getStatus()) ? "Захиалагдсан" : "Цуцлагдсан"));
        refreshBookings();
    }

    private void refreshBookings() {
        ObservableList<Booking> list = FXCollections.observableArrayList(bookingDAO.findAll());
        bookingTable.setItems(list);
    }

    @FXML
    private void handleSearchBookings() {
        String keyword = bookingSearchField.getText() == null ? "" : bookingSearchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshBookings();
        } else {
            ObservableList<Booking> list = FXCollections.observableArrayList(bookingDAO.search(keyword));
            bookingTable.setItems(list);
        }
    }

    // ===================== USERS =====================
    private void initUsersTab() {
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        ObservableList<User> list = FXCollections.observableArrayList(userDAO.findAll());
        userTable.setItems(list);
    }
}
