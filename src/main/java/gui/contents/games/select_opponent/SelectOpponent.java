package gui.contents.games.select_opponent;

import javafx_utils.JavaFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class SelectOpponent extends AnchorPane {
    private SelectOpponentController controller;

    public SelectOpponent() {
        try {
            controller = new SelectOpponentController();
            FXMLLoader loader = controller.getLoader();
            loader.load(); // Прогружаем FXML (после этого поля класса не будут NULL)
            controller = loader.getController(); // Получаем прогруженный контроллер

            Pane p = controller.getMainPane(); // Получаем главный Pane для отображения
            JavaFXUtils.setZeroAnchors(p);
            getChildren().add(p);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SelectOpponentController getController() {
        return controller;
    }
}
