package gui.contents.opponents.opponent;

import db.tables.opponent.DBOpponentItem;
import javafx.JavaFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class Opponent extends AnchorPane {
    private DBOpponentItem dbOpponentItem;
    private OpponentController controller;

    public Opponent(DBOpponentItem dbOpponentItem) {
        this.dbOpponentItem = dbOpponentItem;

        try {
            controller = new OpponentController(dbOpponentItem);
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

    public DBOpponentItem getDbOpponentItem() {
        return dbOpponentItem;
    }

    public OpponentController getController() {
        return controller;
    }
}
