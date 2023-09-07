package gui.versus.games.game;

import db.tables.game.DBGameItem;
import javafx.JavaFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

/*
    Класс предназначен для добавления Node-а в ListView с поддержкой FXML-а
    Класс хранит в себе прогруженный controller, который после прогрузки добавляется в AnchorPane
    Полученный AnchorPane (внутри которого прогруженный контроллер) добавляется как элемент в ListView
 */
public class Game extends AnchorPane {
    private DBGameItem dbGameItem;
    private GameController controller;

    public Game(DBGameItem dBGameItem) {
        this.dbGameItem = dBGameItem;
        try {
            /*
               1. Получаем FXML
               2. Прогружаем (метод load())
               3. Получаем контроллер из прогруженного FXML-а
               4. Получаем из контроллера mainPane
               5. Добавляем mainPane в AnchorPane, который будет добавлен как элемент ListView
            */

            controller = new GameController(dBGameItem);
            FXMLLoader loader = controller.getLoader();
            loader.load();
            controller = loader.getController();

            Pane p = controller.getMainPane();
            JavaFXUtils.setZeroAnchors(p);
            getChildren().add(p);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public DBGameItem getDbGameItem() {
        return dbGameItem;
    }

    public GameController getController() {
        return controller;
    }
}
