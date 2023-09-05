package gui.versus.games.add_game.add_opponent_result;

import db.DBConnect;
import db.tables.game_result.DBGameResultItem;
import db.tables.opponent.DBOpponent;
import db.tables.opponent.DBOpponentItem;
import javafx.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class AddOpponentResultController implements Initializable, FXMLController {
    private DBGameResultItem dbGameResultItem;

    @FXML
    private AnchorPane mainPane;
    @FXML
    private ComboBox<DBOpponentItem> cbOpponent;
    @FXML
    private Spinner<Integer> spScore;

    public AddOpponentResultController(DBGameResultItem dbGameResultItem) {
        this.dbGameResultItem = dbGameResultItem;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try (Connection connection = DBConnect.getConnection();) {
            DBOpponent table = new DBOpponent();
            ResultSet rs = table.selectAll(connection);
            while(rs.next()) {
                DBOpponentItem item = new DBOpponentItem(rs);
                cbOpponent.getItems().add(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        int minValue = 0;
        int maxValue = 100;
        spScore.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(minValue, maxValue, minValue));

        if (dbGameResultItem.getIdGameResult().getValue() == null) {
            if (cbOpponent.getItems().size() > 0) {
                cbOpponent.setValue(cbOpponent.getItems().get(0));
            }
            spScore.getValueFactory().setValue(0);
        } else {
            try (Connection connection = DBConnect.getConnection()) {
                DBOpponent table = new DBOpponent();
                ResultSet rs = table.selectWithOneParameter(connection, table.getIdOpponent(),
                        dbGameResultItem.getIdOpponent().getValue());
                if (rs.next()) {
                    DBOpponentItem item = new DBOpponentItem(rs);
                    for (DBOpponentItem oi: cbOpponent.getItems()) {
                        if (oi.equals(item)) {
                            cbOpponent.setValue(oi);
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            int score = dbGameResultItem.getScore().getValue();
            spScore.getValueFactory().setValue((score < minValue || score > maxValue) ? minValue : score);
        }

    }

    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddOpponentResult.fxml"));
        loader.setController(this);
        return loader;
    }

    @Override
    public Pane getMainPane() {
        return mainPane;
    }

    public DBOpponentItem getSelectedOpponentItem() {
        return cbOpponent.getValue();
    }

    public DBGameResultItem getDbGameResultItem() {
        dbGameResultItem.getIdOpponent().setValue(getSelectedOpponentItem().getIdOpponent().getValue());
        dbGameResultItem.getScore().setValue(spScore.getValue());
        return dbGameResultItem;
    }
}
