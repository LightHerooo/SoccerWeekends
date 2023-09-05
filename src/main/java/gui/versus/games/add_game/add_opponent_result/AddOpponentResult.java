package gui.versus.games.add_game.add_opponent_result;

import db.tables.game_result.DBGameResultItem;
import javafx.JavaFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class AddOpponentResult extends AnchorPane {
    private AddOpponentResultController controller;

    public AddOpponentResult(DBGameResultItem DBGameResultItem) {
        try {
            controller = new AddOpponentResultController(DBGameResultItem);
            FXMLLoader loader = controller.getLoader();
            loader.load(); // Прогружаем FXML (после этого поля класса не будут NULL)
            controller = loader.getController(); // Получаем прогруженный контроллер

            Pane p = controller.getMainPane(); // Получаем главный Pane для отображения
            JavaFXUtils.setZeroAnchors(p);
            getChildren().add(p);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.controller = controller;
    }

    public AddOpponentResultController getController() {
        return controller;
    }
}
