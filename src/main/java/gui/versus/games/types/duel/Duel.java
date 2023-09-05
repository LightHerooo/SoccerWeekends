package gui.versus.games.types.duel;

import db.tables.game.DBGameItem;
import gui.versus.games.types.Game;
import javafx.JavaFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class Duel extends Game {
    private DuelController controller;

    public Duel(DBGameItem DBGameItem) {
        super(DBGameItem);

        try {
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
