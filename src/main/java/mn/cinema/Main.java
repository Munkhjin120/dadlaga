package mn.cinema;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mn.cinema.db.DBInitializer;

public class Main extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        try {
            DBInitializer.initialize();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Өгөгдлийн сантай холбогдож чадсангүй.\n" +
                    "db.properties файлыг шалгана уу.\n\nАлдаа: " + e.getMessage());
            alert.showAndWait();
            return;
        }

        try {
            showLogin();
            stage.setTitle("Кино театр ба тоглолтын тасалбар захиалга");
            try {
                stage.getIcons().add(new Image(Main.class.getResourceAsStream("/mn/cinema/images/icon.png")));
            } catch (Exception ignored) {
                // icon сонголтоор байх тул алдаа гарвал үл тоомсорлоно
            }
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showLogin() throws Exception {
        setRoot("/mn/cinema/view/LoginView.fxml", 480, 400);
    }

    public static void showRegister() throws Exception {
        setRoot("/mn/cinema/view/RegisterView.fxml", 480, 420);
    }

    public static void showMain() throws Exception {
        setRoot("/mn/cinema/view/MainView.fxml", 1000, 650);
    }

    private static void setRoot(String fxmlPath, double width, double height) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(Main.class.getResource("/mn/cinema/css/style.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
