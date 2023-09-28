package gui.contents.games.game.types.big_game;

import db.DBConnect;
import db.DBProcedures;
import db.tables.game.DBGameItem;
import db.tables.game_result.DBGameResultItem;
import db.tables.opponent.DBOpponent;
import db.tables.opponent.DBOpponentItem;
import gui.contents.games.game.types.OpponentResult;
import javafx_utils.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BigGameController implements Initializable, FXMLController {
    private DBGameItem dbGameItem;

    @FXML
    private AnchorPane mainPane;
    @FXML
    private Label lbOtherPlayers;
    @FXML
    private GridPane gpOpponents;

    public BigGameController(DBGameItem dbGameItem) {
        this.dbGameItem = dbGameItem;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Ищем результаты игры
        List<DBGameResultItem> gris = new ArrayList<>();
        try (Connection connection = DBConnect.getConnection();) {
            // Получаем рейтинг (результаты, отсортированные по очкам)
            String query = DBProcedures.GetGameRating.getQuery();
            CallableStatement cs = connection.prepareCall(query);
            cs.setInt(1, dbGameItem.getIdGame().getValue());
            cs.execute();

            ResultSet rs = cs.getResultSet();
            while (rs.next()) {
                DBGameResultItem item = new DBGameResultItem(rs);
                gris.add(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        final int MAX_DISPLAY_OPPONENTS = 3;
        List<BigGameOpponentController> bgocs = new ArrayList<>();
        try (Connection connection = DBConnect.getConnection();) {
            for (int i = 0; i < gris.size(); i++) {
                if (bgocs.size() == MAX_DISPLAY_OPPONENTS) break;
                DBOpponent table = new DBOpponent();
                ResultSet rs = table.selectWithOneParameter(connection, table.getIdOpponent(),
                        gris.get(i).getIdOpponent().getValue());
                if (rs.next()) {
                    DBOpponentItem item = new DBOpponentItem(rs);
                    BigGameOpponentController bgoc = new BigGameOpponentController(item,
                            gris.get(i).getScore().getValue());
                    bgocs.add(bgoc);
                }
            }

            if (bgocs.size() >= MAX_DISPLAY_OPPONENTS) {
                BigGameOpponentController bgoc = bgocs.get(0);
                if (bgoc != null) {
                    gpOpponents.add(bgoc.getLoader().load(), 0, 0);
                    bgoc.setPlaceImg(OpponentResult.FIRST);
                }

                bgoc = bgocs.get(1);
                if (bgoc != null) {
                    gpOpponents.add(bgoc.getLoader().load(), 1, 0);
                    bgoc.setPlaceImg(OpponentResult.SECOND);
                }

                bgoc = bgocs.get(2);
                if (bgoc != null) {
                    gpOpponents.add(bgoc.getLoader().load(), 2, 0);
                    bgoc.setPlaceImg(OpponentResult.THIRD);
                }

                int numberOfOtherPlayers = gris.size() - MAX_DISPLAY_OPPONENTS;
                if (numberOfOtherPlayers > 0) {
                    lbOtherPlayers.setText(String.format("+%d", numberOfOtherPlayers));
                } else {
                    Pane p = (Pane)lbOtherPlayers.getParent();
                    p.getChildren().remove(lbOtherPlayers);
                    gpOpponents.getColumnConstraints().remove(
                            gpOpponents.getColumnConstraints().get(gpOpponents.getColumnConstraints().size() - 1));
                }
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BigGame.fxml"));
        loader.setController(this);

        return loader;
    }

    @Override
    public Pane getMainPane() {
        return mainPane;
    }
}
