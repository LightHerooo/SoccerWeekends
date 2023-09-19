package gui.contents.games;

import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import db.DBConnect;
import db.DBProcedures;
import db.tables.game.DBGame;
import db.tables.game.DBGameItem;
import db.tables.opponent.DBOpponentItem;
import gui.contents.games.add_game.AddGameController;
import gui.contents.games.game.Game;
import gui.contents.games.select_opponent.SelectOpponent;
import javafx_utils.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import utils.CustomAlerts;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class GamesController implements Initializable, FXMLController {
    @FXML
    private AnchorPane mainPane;
    @FXML
    private VBox vbOpponents;
    @FXML
    private AnchorPane apGames;
    @FXML
    private Button btnAddOpponent;
    @FXML
    private ListView<Game> lvGames;
    @FXML
    private Button btnAddGame;
    @FXML
    private Button btnEditGame;
    @FXML
    private Button btnDeleteGame;

    // Вызывается при Load FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnAddOpponent_click(null);
        lvGames.setOnMouseClicked(this::lvGameResults_mouseClicked);
    }
    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Games.fxml"));
        return fxmlLoader;
    }
    @Override
    public Pane getMainPane() {
        return mainPane;
    }
    @FXML
    public void btnAddOpponent_click(ActionEvent actionEvent) {
        SelectOpponent so = new SelectOpponent();
        so.getController().getCbOpponent().getSelectionModel().selectedItemProperty().addListener(changeListener -> {
            selectGames();
        });

        so.getController().getBtnDelete().setOnAction(event -> {
            vbOpponents.getChildren().remove(so);
            if (so.getController().getSelectedOpponentItem() != null)
                selectGames();
            changeSelectOpponentNumeration();
        });

        vbOpponents.getChildren().add(so);
        changeSelectOpponentNumeration();
    }
    @FXML
    private void btnAddGame_click(ActionEvent actionEvent) {
        AddGameController addGameController = new AddGameController(new DBGameItem());
        addGameController.getStage().showAndWait();

        selectGames();
    }
    @FXML
    private void btnEditGame_click(ActionEvent actionEvent) {
        Game g = lvGames.getSelectionModel().getSelectedItem();
        if (g != null) {
            AddGameController addGameController = new AddGameController(g.getDbGameItem());
            addGameController.getStage().showAndWait();

            selectGames();
        }
    }
    @FXML
    private void btnDeleteGame_click(ActionEvent actionEvent) {
        Game g = lvGames.getSelectionModel().getSelectedItem();
        if (g != null) {
            DBGameItem item = g.getDbGameItem();
            if (item != null) {
                if (CustomAlerts.showConfirmationDeleteAlert() == ButtonType.OK) {
                    try (Connection connection = DBConnect.getConnection();) {
                        DBGame table = new DBGame();
                        table.delete(connection, item);
                        selectGames();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private void lvGameResults_mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() > 1) {
            btnEditGame_click(null);
        }
    }
    public void changeSelectOpponentNumeration() {
        int index = 1;
        for (Node p: vbOpponents.getChildren()) {
            if (p instanceof SelectOpponent so) {
                so.getController().setOpponentNumber(index);
                index++;
            }
        }
    }
    private void selectGames() {
        List<DBOpponentItem> ois = new ArrayList<>();
        for (Node n: vbOpponents.getChildren()) {
            if (n instanceof SelectOpponent so) {
                ois.add(so.getController().getSelectedOpponentItem());
                so.getController().updateOpponentStatistics();
            }
        }

        lvGames.getItems().clear();

        try (Connection connection = DBConnect.getConnection();){
            SQLServerDataTable id_table = new SQLServerDataTable();
            id_table.setTvpName("ID_TABLE");
            id_table.addColumnMetadata("id", Types.INTEGER);
            for (DBOpponentItem oi: ois) {
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
}
