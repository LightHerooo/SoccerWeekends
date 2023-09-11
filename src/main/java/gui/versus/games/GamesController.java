package gui.versus.games;

import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import db.DBConnect;
import db.DBProcedures;
import db.tables.game.DBGame;
import db.tables.game.DBGameItem;
import db.tables.opponent.DBOpponentItem;
import gui.versus.games.add_game.AddGameController;
import gui.versus.games.game.Game;
import javafx.FXMLController;
import javafx.event.ActionEvent;
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
import java.util.List;
import java.util.ResourceBundle;

public class GamesController implements Initializable, FXMLController {
    private List<DBOpponentItem> dbOpponentItems;

    @FXML
    private AnchorPane mainPane;
    @FXML
    private ListView<Game> lvGames;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;

    public GamesController(List<DBOpponentItem> dbOpponentItems) {
        this.dbOpponentItems = dbOpponentItems;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectGames();

        lvGames.setOnMouseClicked(this::lvGameResults_mouseClicked);
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
        lvGames.getItems().clear();

        try (Connection connection = DBConnect.getConnection();){
            SQLServerDataTable id_table = new SQLServerDataTable();
            id_table.setTvpName("ID_TABLE");
            id_table.addColumnMetadata("id", Types.INTEGER);
            for (DBOpponentItem oi: dbOpponentItems) {
                if (oi != null) {
                    id_table.addRow(oi.getIdOpponent().getValue());
                }
            }

            String query = DBProcedures.GetGamesBetweenOpponents.getQuery();
            CallableStatement cs = connection.prepareCall(query);
            cs.setObject(1, id_table);

            ResultSet rs = cs.executeQuery();
            while(rs.next()) {
                DBGameItem item = new DBGameItem(rs);
                Game g = new Game(item);
                lvGames.getItems().add(g);
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
        Game g = lvGames.getSelectionModel().getSelectedItem();
        if (g != null) {
            AddGameController addGameController = new AddGameController(g.getDbGameItem());
            addGameController.getStage().showAndWait();

            selectGames();
        }
    }

    private void btnDelete_click(ActionEvent actionEvent) {
        Game g = lvGames.getSelectionModel().getSelectedItem();
        if (g != null) {
            DBGameItem item = g.getDbGameItem();
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

            selectGames();
        }
    }

    private void lvGameResults_mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() > 1) {
            btnEdit_click(null);
        }
    }
}
