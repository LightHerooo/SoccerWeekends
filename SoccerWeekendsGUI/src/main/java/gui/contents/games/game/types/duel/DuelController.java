package gui.contents.games.game.types.duel;

import db.DBConnect;
import db.tables.game.DBGameItem;
import db.tables.game_result.DBGameResult;
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DuelController implements Initializable, FXMLController {
    private DBGameItem dbGameItem;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private GridPane gpResult;
    @FXML
    private Label lbScore;

    public DuelController(DBGameItem dbGameItem) {
        this.dbGameItem = dbGameItem;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Ищем результаты игры
        final int MAX_NUMBER_OF_OPPONENTS = 2;
        List<DBGameResultItem> gris = new ArrayList<>();
        try (Connection connection = DBConnect.getConnection();) {
            // Ищем все результаты игры
            DBGameResult table = new DBGameResult();
            ResultSet rs = table.selectWithOneParameter(connection, table.getIdGame(),
                    dbGameItem.getIdGame().getValue());
            while (rs.next()) {
                if (gris.size() == MAX_NUMBER_OF_OPPONENTS) break;
                DBGameResultItem item = new DBGameResultItem(rs);
                gris.add(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // В дуэли не должно быть более 2-х результатов
        if (gris.size() == MAX_NUMBER_OF_OPPONENTS) {
            try (Connection connection = DBConnect.getConnection();) {
                // Ищем всех участников игры
                List<DuelOpponentController> docs = new ArrayList<>();
                for (int i = 0; i < gris.size(); i++) {
                    DBOpponent table = new DBOpponent();
                    ResultSet rs = table.selectWithOneParameter(connection, table.getIdOpponent(),
                            gris.get(i).getIdOpponent().getValue());
                    if (rs.next()) {
                        DBOpponentItem item = new DBOpponentItem(rs);
                        docs.add(new DuelOpponentController(item));
                    }
                }

                // Добавляем данные об игроках
                DuelOpponentController docFirst = docs.get(0);
                DuelOpponentController docSecond = docs.get(1);
                gpResult.add(docFirst.getLoader().load(), 0, 0);
                gpResult.add(docSecond.getLoader().load(), 2, 0);

                // Устанавливаем информацию о результатах игры
                int firstScore = gris.get(0).getScore().getValue();
                int secondScore = gris.get(1).getScore().getValue();

                // Счет игры
                String score = "%d - %d";
                score = String.format(score, firstScore, secondScore);
                lbScore.setText(score);

                // Фон картинки игроков
                if (firstScore > secondScore) {
                    docFirst.setIvOpponentResultImg(OpponentResult.WINNER);
                    docSecond.setIvOpponentResultImg(OpponentResult.LOSER);
                } else if (firstScore < secondScore) {
                    docFirst.setIvOpponentResultImg(OpponentResult.LOSER);
                    docSecond.setIvOpponentResultImg(OpponentResult.WINNER);
                } else {
                    docFirst.setIvOpponentResultImg(OpponentResult.DRAW);
                    docSecond.setIvOpponentResultImg(OpponentResult.DRAW);
                }
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Duel.fxml"));
        loader.setController(this);
        return loader;
    }

    @Override
    public Pane getMainPane() {
        return mainPane;
    }
}
