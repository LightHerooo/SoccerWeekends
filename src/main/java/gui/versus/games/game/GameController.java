package gui.versus.games.game;

import db.DBConnect;
import db.DBFunctions;
import db.tables.game.DBGameItem;
import db.tables.game_type.DBGameType;
import db.tables.game_type.DBGameTypeItem;
import gui.versus.games.game.types.big_game.BigGameController;
import gui.versus.games.game.types.duel.DuelController;
import javafx.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class GameController implements FXMLController, Initializable {
    private DBGameItem dbGameItem;

    @FXML
    private AnchorPane mainPane;
    @FXML
    private GridPane gpResult;
    @FXML
    private Label lbGameType;
    @FXML
    private Label lbGameDate;

    public GameController(DBGameItem dbGameItem) {
        this.dbGameItem = dbGameItem;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try(Connection connection = DBConnect.getConnection()) {
            // Ищем нужный тип игры для заполнения информации
            DBGameType table = new DBGameType();
            ResultSet rs = table.selectWithOneParameter(connection, table.getIdGameType(),
                    dbGameItem.getIdGameType().getValue());
            if (rs.next()) {
                DBGameTypeItem gti = new DBGameTypeItem(rs);
                lbGameType.setText(gti.getTitle().getValue());

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                String dateStr = sdf.format(dbGameItem.getGameDate().getValue());
                lbGameDate.setText(dateStr);
            }

            // Считаем, сколько игроков в игре
            String query = DBFunctions.GetNumberOfPlayersPlayingTheGame.getName();
            CallableStatement cs = connection.prepareCall(query);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, dbGameItem.getIdGame().getValue());
            cs.execute();
            int countOfPlayers = cs.getInt(1);

            Parent p = null;
            // Если игроков больше 2-х - это большая игра
            if (countOfPlayers > 2) {
                BigGameController bgc = new BigGameController(dbGameItem);
                p = bgc.getLoader().load();
            } else if (countOfPlayers == 2) { // Если всего 2 игрока - это дуэль
                DuelController dc = new DuelController(dbGameItem);
                p = dc.getLoader().load();
            }

            if (p != null)
                gpResult.add(p, 1, 0);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Game.fxml"));
        loader.setController(this);
        return loader;
    }

    @Override
    public Pane getMainPane() {
        return mainPane;
    }
}
