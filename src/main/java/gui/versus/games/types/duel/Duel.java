package gui.versus.games.types.duel;

import db.tables.game.DBGameItem;
import gui.versus.games.types.Game;
import javafx.JavaFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

/*
    Класс предназначен для добавления Node-а в ListView с поддержкой FXML-а
    Класс хранит в себе прогруженный controller, который после прогрузки добавляется в AnchorPane
    Полученный AnchorPane (внутри которого прогруженный контроллер) добавляется как элемент в ListView
 */
public class Duel extends Game {
    private DuelController controller;

    public Duel(DBGameItem DBGameItem) {
        super(DBGameItem);

        try {
            /*
               1. Получаем FXML
               2. Прогружаем (метод load())
               3. Получаем контроллер из прогруженного FXML-а
               4. Получаем из контроллера mainPane
               5. Добавляем mainPane в AnchorPane, который будет добавлен как элемент ListView
            */

            controller = new DuelController(DBGameItem);
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

    public DuelController getController() {
        return controller;
    }
}
