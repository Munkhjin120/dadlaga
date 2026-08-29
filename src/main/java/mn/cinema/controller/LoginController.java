package mn.cinema.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mn.cinema.Main;
import mn.cinema.dao.UserDAO;
import mn.cinema.model.User;
import mn.cinema.util.SessionManager;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Hyperlink registerLink;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Хэрэглэгчийн нэр, нууц үгээ оруулна уу.");
            return;
        }

        User user = userDAO.login(username, password);
        if (user == null) {
            errorLabel.setText("Хэрэглэгчийн нэр эсвэл нууц үг буруу байна.");
            return;
        }

        SessionManager.setCurrentUser(user);
        try {
            Main.showMain();
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Үндсэн цэсийг ачаалахад алдаа гарлаа.");
        }
    }

    @FXML
    private void handleGoToRegister() {
        try {
            errorLabel.setText("");
            Main.showRegister();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
