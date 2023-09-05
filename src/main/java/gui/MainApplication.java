package gui;

import db.DBConnect;
import db.table.DBColumn;
import db.tables.game_result.DBGameResultItem;
import db.tables.opponent.DBOpponent;
import db.tables.opponent.DBOpponentItem;
import javafx.JavaFXUtils;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

public final class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setWidth(900);
        stage.setHeight(600);
        stage.setTitle("SoccerWeekends (development)");

        MainController msc = new MainController();
        Parent p = msc.getLoader().load(); // Прогружаем FXML
        JavaFXUtils.setZeroAnchors(p); // Устанавливаем отступы AnchorPane по 0 (нужно для растягивания на всё окно)
        Scene sc = new Scene(p);

        stage.setScene(sc);
        stage.show();

    }
}
