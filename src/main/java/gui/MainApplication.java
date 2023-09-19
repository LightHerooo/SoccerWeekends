package gui;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx_utils.JavaFXUtils;

public final class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.setTitle("SoccerWeekends (development)");

        MainController msc = new MainController();
        Parent p = msc.getLoader().load(); // Прогружаем FXML
        JavaFXUtils.setZeroAnchors(p); // Устанавливаем отступы AnchorPane по 0 (нужно для растягивания на всё окно)
        Scene sc = new Scene(p);

        stage.setScene(sc);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
