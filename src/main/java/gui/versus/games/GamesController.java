package gui.versus.games;

import db.DBConnect;
import db.DBFunctions;
import db.DBProcedures;
import db.tables.game.DBGame;
import db.tables.game.DBGameItem;
import db.tables.opponent.DBOpponentItem;
import gui.versus.games.types.Game;
import javafx.FXMLController;
import gui.versus.games.add_game.AddGameController;
import gui.versus.games.types.duel.Duel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class GamesController implements Initializable, FXMLController {
    private DBOpponentItem opponentFirst;
    private DBOpponentItem opponentSecond;

    @FXML
    private AnchorPane mainPane;
    @FXML
    private ListView<Game> lvGameResults;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;

    public GamesController(DBOpponentItem opponentFirst, DBOpponentItem opponentSecond) {
        this.opponentFirst = opponentFirst;
        this.opponentSecond = opponentSecond;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectGames();

        lvGameResults.setOnMouseClicked(this::lvGameResults_mouseClicked);
        btnAdd.setOnAction(this::btnAdd_click);
        btnEdit.setOnAction(this::btnEdit_click);
        btnDelete.setOnAction(this::btnDelete_click);
    }

    /*
        Контроллер содержит поля элементов, находящихся в FXML-е
        Наличие полей, которые не содержатся в FXML-е, вызовет ошибку
        Чтобы обойти это, необходимо установить полученный контроллер load-еру.

        FXML не должен иметь свойства fx:controller
     */
    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Games.fxml"));
        fxmlLoader.setController(this);
        return fxmlLoader;
    }

    @Override
    public Pane getMainPane() {
        return mainPane;
    }
    
    private void selectGames() {
        lvGameResults.getItems().clear();

        try (Connection connection = DBConnect.getConnection();){
            // Выполняем поиск игр, где участвуют два выбранных игрока
            String query = DBProcedures.GetGamesPlayedByTwoOpponents.getQuery();
            CallableStatement cs = connection.prepareCall(query);
            cs.setInt(1, opponentFirst.getIdOpponent().getValue());
            cs.setInt(2, opponentSecond.getIdOpponent().getValue());
            cs.execute();

            ResultSet rs = cs.getResultSet();
            while(rs.next()) {
                DBGameItem item = new DBGameItem(rs);

                // Считаем, сколько игроков в игре
                query = DBFunctions.GetNumberOfPlayersPlayingTheGame.getName();
                cs = connection.prepareCall(query);
                cs.registerOutParameter(1, Types.INTEGER);
                cs.setInt(2, item.getIdGame().getValue());
                cs.execute();
                int countOfPlayers = cs.getInt(1);

                // Если игроков больше 2-х - это групповая игра
                if (countOfPlayers > 2) {


                } else if (countOfPlayers == 2) { // Если всего 2 игрока - это дуэль
                    /*Duel d = new Duel(item);
                    lvGameResults.getItems().add(d);*/
                }

                Duel d = new Duel(item);
                lvGameResults.getItems().add(d);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void btnAdd_click(ActionEvent actionEvent) {
        AddGameController addGameController = new AddGameController(new DBGameItem());
        addGameController.getStage().showAndWait();

        selectGames();
    }

    private void btnEdit_click(ActionEvent actionEvent) {
        Game gr = lvGameResults.getSelectionModel().getSelectedItem();
        if (gr != null) {
            AddGameController addGameController = new AddGameController(gr.getGameItem());
            addGameController.getStage().showAndWait();
        }

        selectGames();
    }

    private void btnDelete_click(ActionEvent actionEvent) {
        Game gr = lvGameResults.getSelectionModel().getSelectedItem();
        if (gr != null) {
            DBGameItem item = gr.getGameItem();
            if (item != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Удаление игры");
                alert.setHeaderText("Вы уверены, что хотите удалить выбранную игру?");
                alert.showAndWait();
                if (alert.getResult() == ButtonType.OK) {
                    try (Connection connection = DBConnect.getConnection();) {
                        DBGame table = new DBGame();
                        table.delete(connection, item);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        selectGames();
    }

    private void lvGameResults_mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() > 1) {
            btnEdit_click(null);
        }
    }
}
