package gui.contents.game_types;

import db.DBConnect;
import db.tables.game_type.DBGameType;
import db.tables.game_type.DBGameTypeItem;
import gui.contents.game_types.add_game_type.AddGameTypeController;
import gui.contents.game_types.game_type.GameType;
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

public class GameTypesController implements Initializable, FXMLController {

    @FXML
    private AnchorPane mainPane;
    @FXML
    private ListView<GameType> lvGameTypes;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnEdit;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectGameTypes();
    }

    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GameTypes.fxml"));
        return loader;
    }

    @Override
    public Pane getMainPane() {
        return mainPane;
    }

    @FXML
    public void btnAdd_click(ActionEvent actionEvent) {
        AddGameTypeController agtc = new AddGameTypeController(new DBGameTypeItem());
        agtc.getStage().showAndWait();
        selectGameTypes();
    }

    @FXML
    public void btnEdit_click(ActionEvent actionEvent) {
        GameType gt = lvGameTypes.getSelectionModel().getSelectedItem();
        if (gt != null) {
            AddGameTypeController agtc = new AddGameTypeController(gt.getDbGameTypeItem());
            agtc.getStage().showAndWait();

            selectGameTypes();
        }
    }

    @FXML
    public void lvGameTypes_mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() > 1) {
            btnEdit_click(null);
        }
    }

    public void selectGameTypes() {
        lvGameTypes.getItems().clear();

        try (Connection connection = DBConnect.getConnection();){
            DBGameType table = new DBGameType();
            ResultSet rs = table.selectAll(connection);
            while(rs.next()) {
                DBGameTypeItem item = new DBGameTypeItem(rs);
                GameType gt = new GameType(item);
                lvGameTypes.getItems().add(gt);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
