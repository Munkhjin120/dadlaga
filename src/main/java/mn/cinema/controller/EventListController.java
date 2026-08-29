package mn.cinema.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import mn.cinema.dao.EventDAO;
import mn.cinema.model.Event;

import java.time.format.DateTimeFormatter;

public class EventListController {

    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, String> typeColumn;
    @FXML private TableColumn<Event, String> titleColumn;
    @FXML private TableColumn<Event, String> dateColumn;
    @FXML private TableColumn<Event, String> timeColumn;
    @FXML private TableColumn<Event, String> hallColumn;
    @FXML private TableColumn<Event, String> priceColumn;
    @FXML private TextArea descriptionArea;
    @FXML private Button selectButton;

    private final EventDAO eventDAO = new EventDAO();
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        typeColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTypeLabel()));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getEventDate() == null ? "" : c.getValue().getEventDate().format(dateFmt)));
        timeColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getEventTime() == null ? "" : c.getValue().getEventTime().format(timeFmt)));
        hallColumn.setCellValueFactory(new PropertyValueFactory<>("hallName"));
        priceColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getPrice() == null ? "" : c.getValue().getPrice().toString() + "₮"));

        ObservableList<Event> events = FXCollections.observableArrayList(eventDAO.findUpcoming());
        eventTable.setItems(events);

        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean has = newVal != null;
            selectButton.setDisable(!has);
            descriptionArea.setText(has ? (newVal.getDescription() == null ? "" : newVal.getDescription()) : "");
        });
        selectButton.setDisable(true);
    }

    @FXML
    private void handleSelectEvent() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mn/cinema/view/SeatSelectionView.fxml"));
            Parent node = loader.load();
            SeatSelectionController controller = loader.getController();
            controller.setEvent(selected);
            BorderPane rootPane = (BorderPane) eventTable.getScene().lookup("#rootPane");
            if (rootPane != null) {
                rootPane.setCenter(node);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
