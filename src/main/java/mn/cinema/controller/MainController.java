package mn.cinema.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import mn.cinema.Main;
import mn.cinema.dao.UserDAO;
import mn.cinema.util.SessionManager;

import java.util.Optional;

public class MainController {

    @FXML private BorderPane rootPane;
    @FXML private Label welcomeLabel;
    @FXML private MenuItem adminMenuItem;
    @FXML private Button adminButton;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        if (SessionManager.getCurrentUser() != null) {
            welcomeLabel.setText("Сайн байна уу, " + SessionManager.getCurrentUser().getUsername() +
                    (SessionManager.isAdmin() ? " (Админ)" : ""));
        }
        boolean isAdmin = SessionManager.isAdmin();
        adminMenuItem.setVisible(isAdmin);
        adminMenuItem.setDisable(!isAdmin);
        adminButton.setVisible(isAdmin);
        adminButton.setManaged(isAdmin);
        showEventList();
    }

    @FXML
    private void showEventList() {
        loadCenter("/mn/cinema/view/EventListView.fxml");
    }

    @FXML
    private void showBookingHistory() {
        loadCenter("/mn/cinema/view/BookingHistoryView.fxml");
    }

    @FXML
    private void showAdmin() {
        if (!SessionManager.isAdmin()) return;
        loadCenter("/mn/cinema/view/AdminView.fxml");
    }

    @FXML
    private void handleLogout() {
        SessionManager.clear();
        try {
            Main.showLogin();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Гарахад алдаа гарлаа: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handleDeleteAccount() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Та өөрийн бүртгэлээ устгахдаа итгэлтэй байна уу? Энэ үйлдлийг буцаах боломжгүй.",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            int userId = SessionManager.getCurrentUser().getId();
            boolean ok = userDAO.deleteAccount(userId);
            if (ok) {
                SessionManager.clear();
                try {
                    Main.showLogin();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Бүртгэл устгахад алдаа гарлаа.").showAndWait();
            }
        }
    }

    private void loadCenter(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent node = loader.load();
            rootPane.setCenter(node);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Дэлгэц ачаалахад алдаа гарлаа: " + e.getMessage()).showAndWait();
        }
    }
}
