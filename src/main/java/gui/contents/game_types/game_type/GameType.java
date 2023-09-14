package gui.contents.game_types.game_type;

import db.tables.game_type.DBGameTypeItem;
import javafx.JavaFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class GameType extends AnchorPane {
    private DBGameTypeItem dbGameTypeItem;
    private GameTypeController controller;

    public GameType(DBGameTypeItem dbGameTypeItem) {
        this.dbGameTypeItem = dbGameTypeItem;

        try {
            controller = new GameTypeController(dbGameTypeItem);
            FXMLLoader loader = controller.getLoader();
            loader.load();
            controller = loader.getController();

            Pane p = controller.getMainPane();
            double a = controller.getMainPane().getHeight();
            JavaFXUtils.setZeroAnchors(p);
            getChildren().add(p);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public DBGameTypeItem getDbGameTypeItem() {
        return dbGameTypeItem;
    }

    public GameTypeController getController() {
        return controller;
    }
}
