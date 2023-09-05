package gui.versus.games.types;

import db.tables.game.DBGameItem;
import javafx.scene.layout.AnchorPane;

public abstract class Game extends AnchorPane {
    protected DBGameItem DBGameItem;

    public Game(DBGameItem DBGameItem) {
        this.DBGameItem = DBGameItem;
    }

    public DBGameItem getGameItem() {
        return DBGameItem;
    }
}
