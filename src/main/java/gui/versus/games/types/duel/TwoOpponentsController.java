package gui.versus.games.types.duel;

import db.DBConnect;
import db.tables.game.DBGameItem;
import db.tables.game_result.DBGameResult;
import db.tables.game_result.DBGameResultItem;
import db.tables.opponent.DBOpponent;
import db.tables.opponent.DBOpponentItem;
import javafx.FXMLController;
import gui.versus.games.types.OpponentController;
import gui.versus.games.types.ResultColor;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TwoOpponentsController implements Initializable, FXMLController {
    private DBGameItem dbGameItem;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private GridPane gpResult;
    @FXML
    private Label lbScore;

    public TwoOpponentsController(DBGameItem dbGameItem) {
        this.dbGameItem = dbGameItem;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        final int MAX_NUMBER_OF_OPPONENTS = 2;
        List<DBGameResultItem> gris = new ArrayList<>();
        try (Connection connection = DBConnect.getConnection();) {
            // Ищем все результаты игры
            DBGameResult table = new DBGameResult();
            ResultSet rs = table.selectWithOneParameter(connection, table.getIdGame(),
                    dbGameItem.getIdGame().getValue());
            while (rs.next() || gris.size() < MAX_NUMBER_OF_OPPONENTS) {
                DBGameResultItem item = new DBGameResultItem(rs);
                gris.add(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (gris.size() == MAX_NUMBER_OF_OPPONENTS) {
            try (Connection connection = DBConnect.getConnection();) {
                // Ищем всех участников игры
                List<OpponentController> ocs = new ArrayList<>();
                for (int i = 0; i < gris.size(); i++) {
                    DBOpponent table = new DBOpponent();
                    ResultSet rs = table.selectWithOneParameter(connection, table.getIdOpponent(),
                            gris.get(i).getIdOpponent().getValue());
                    if (rs.next()) {
                        DBOpponentItem item = new DBOpponentItem(rs);
                        ocs.add(new OpponentController(item));
                    }
                }

                // Добавляем данные об игроках
                OpponentController ocFirst = ocs.get(0);
                OpponentController ocSecond = ocs.get(1);
                gpResult.add(ocFirst.getLoader().load(), 0, 0);
                gpResult.add(ocSecond.getLoader().load(), 2, 0);

                // Устанавливаем информацию о результатах игры
                int firstScore = gris.get(0).getScore().getValue();
                int secondScore = gris.get(1).getScore().getValue();

                // Счет игры
                String score = "%d - %d";
                score = String.format(score, firstScore, secondScore);
                lbScore.setText(score);

                // Фон картинки игроков
                if (firstScore > secondScore) {
                    ocFirst.setBorderColorInImage(ResultColor.WIN.getColor());
                    ocSecond.setBorderColorInImage(ResultColor.LOSE.getColor());
                } else if (firstScore < secondScore) {
                    ocFirst.setBorderColorInImage(ResultColor.LOSE.getColor());
                    ocSecond.setBorderColorInImage(ResultColor.WIN.getColor());
                } else {
                    ocFirst.setBorderColorInImage(ResultColor.DRAW.getColor());
                    ocSecond.setBorderColorInImage(ResultColor.DRAW.getColor());
                }


            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TwoOpponents.fxml"));
        loader.setController(this);
        return loader;
    }

    @Override
    public Pane getMainPane() {
        return mainPane;
    }
}
