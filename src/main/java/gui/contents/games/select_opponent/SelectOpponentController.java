package gui.contents.games.select_opponent;

import db.DBConnect;
import db.tables.opponent.DBOpponent;
import db.tables.opponent.DBOpponentItem;
import gui.contents.games.select_opponent.statistics.StatisticsController;
import javafx_utils.FXMLController;
import javafx_utils.JavaFXUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SelectOpponentController implements Initializable, FXMLController {
    @FXML
    private AnchorPane mainPane;
    @FXML
    private Label lbOpponentNumber;
    @FXML
    private Button btnDelete;
    @FXML
    private ComboBox<DBOpponentItem> cbOpponent;
    @FXML
    private AnchorPane apStatistics;

    public ComboBox<DBOpponentItem> getCbOpponent() {
        return cbOpponent;
    }

    public Button getBtnDelete() {
        return btnDelete;
    }

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

        cbOpponent.setItems(opponents);
        cbOpponent.setOnAction(this::cbOpponent_select);
    }

    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SelectOpponent.fxml"));
        loader.setController(this);

        return loader;
    }

    @Override
    public Pane getMainPane() {
        return mainPane;
    }
    private void cbOpponent_select(ActionEvent actionEvent) {
        updateOpponentStatistics();
    }

    public DBOpponentItem getSelectedOpponentItem() {
        return cbOpponent.getSelectionModel().getSelectedItem();
    }

    public void setOpponentNumber(int number) {
        lbOpponentNumber.setText(String.format("Оппонент №%d:", number));
    }

    public void updateOpponentStatistics() {
        apStatistics.getChildren().clear();

        try {
            DBOpponentItem item = getSelectedOpponentItem();
            if (item != null) {
                StatisticsController osc = new StatisticsController(item);
                Parent p = osc.getLoader().load();
                JavaFXUtils.setZeroAnchors(p);
                apStatistics.getChildren().add(p);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
