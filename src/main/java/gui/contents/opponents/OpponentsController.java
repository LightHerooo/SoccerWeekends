package gui.contents.opponents;

import db.DBConnect;
import db.tables.opponent.DBOpponent;
import db.tables.opponent.DBOpponentItem;
import gui.contents.opponents.add_opponent.AddOpponentController;
import gui.contents.opponents.opponent.Opponent;
import javafx.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class OpponentsController implements Initializable, FXMLController {
    @FXML
    private AnchorPane mainPane;
    @FXML
    private ListView<Opponent> lvOpponents;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectOpponents();
    }

    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Opponents.fxml"));
        return loader;
    }

    @Override
    public Pane getMainPane() {
        return mainPane;
    }

    @FXML
    private void btnAdd_click(ActionEvent actionEvent) {
        AddOpponentController aoc = new AddOpponentController(new DBOpponentItem());
        aoc.getStage().showAndWait();
        selectOpponents();
    }

    @FXML
    private void btnEdit_click(ActionEvent actionEvent) {
        Opponent o = lvOpponents.getSelectionModel().getSelectedItem();
        if (o != null) {
            AddOpponentController aoc = new AddOpponentController(o.getDbOpponentItem());
            aoc.getStage().showAndWait();
            selectOpponents();
        }
    }

    @FXML
    private void lvOpponents_mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() > 1) {
            btnEdit_click(null);
        }
    }

    public void selectOpponents() {
        lvOpponents.getItems().clear();

        try (Connection connection = DBConnect.getConnection();) {
            DBOpponent table = new DBOpponent();
            ResultSet rs = table.selectAll(connection);

            while (rs.next()) {
                DBOpponentItem item = new DBOpponentItem(rs);
                Opponent opponent = new Opponent(item);
                lvOpponents.getItems().add(opponent);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
