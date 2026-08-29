package mn.cinema.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mn.cinema.Main;
import mn.cinema.dao.UserDAO;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private Hyperlink loginLink;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleRegister() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();
        String confirm = confirmPasswordField.getText() == null ? "" : confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Бүх талбарыг бөглөнө үү.");
            return;
        }
        if (username.length() < 3) {
            errorLabel.setText("Хэрэглэгчийн нэр 3-с дээш тэмдэгттэй байх ёстой.");
            return;
        }
        if (password.length() < 4) {
            errorLabel.setText("Нууц үг 4-с дээш тэмдэгттэй байх ёстой.");
            return;
        }
        if (!password.equals(confirm)) {
            errorLabel.setText("Нууц үг таарахгүй байна.");
            return;
        }

        boolean success = userDAO.register(username, password);
        if (!success) {
            errorLabel.setText("Энэ хэрэглэгчийн нэр аль хэдийн бүртгэлтэй байна.");
            return;
        }

        try {
            Main.showLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToLogin() {
        try {
            errorLabel.setText("");
            Main.showLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
