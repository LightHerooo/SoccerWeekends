package gui.versus;

import db.DBConnect;
import db.tables.opponent.DBOpponent;
import db.tables.opponent.DBOpponentItem;
import gui.versus.games.GamesController;
import javafx.FXMLController;
import gui.MainController;
import javafx.JavaFXUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class VersusController implements Initializable, FXMLController {
    @FXML
    private AnchorPane mainPane;
    @FXML
    private ComboBox<DBOpponentItem> cbFirstOpp;
    @FXML
    private ComboBox<DBOpponentItem> cbSecondOpp;
    @FXML
    private AnchorPane contentPane;

    // Вызывается при Load FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<DBOpponentItem> opponents = FXCollections.observableArrayList();
        try (Connection connection = DBConnect.getConnection()){
            DBOpponent table = new DBOpponent();
            ResultSet rs = table.selectAll(connection);
            while (rs.next()) {
                DBOpponentItem item = new DBOpponentItem(rs);
                opponents.add(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        cbFirstOpp.setItems(opponents);
        cbSecondOpp.setItems(opponents);
    }

    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Versus.fxml"));
        return fxmlLoader;
    }

    @Override
    public Pane getMainPane() {
        return mainPane;
    }

    @FXML
    private void showOpponentsResult(ActionEvent actionEvent) throws IOException {
        DBOpponentItem opponentFirst = cbFirstOpp.getValue();
        DBOpponentItem opponentSecond = cbSecondOpp.getValue();
        if (opponentFirst == null || opponentSecond == null) return;

        GamesController rc = new GamesController(opponentFirst, opponentSecond);
        Parent p = rc.getLoader().load();
        JavaFXUtils.setZeroAnchors(p);
        contentPane.getChildren().setAll(p);
    }

    @FXML
    private void btnBack_click(ActionEvent actionEvent) throws IOException {
        MainController mc = new MainController();
        Parent p = mc.getLoader().load();
        JavaFXUtils.setZeroAnchors(p);

        Pane mp = (Pane) mainPane.getParent();
        mp.getChildren().setAll(p);
    }
}
