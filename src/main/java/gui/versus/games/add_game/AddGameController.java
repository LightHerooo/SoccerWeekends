package gui.versus.games.add_game;

import db.DBConnect;
import db.table.DBTable;
import db.tables.game.DBGame;
import db.tables.game.DBGameItem;
import db.tables.game_result.DBGameResult;
import db.tables.game_result.DBGameResultItem;
import db.tables.game_type.DBGameType;
import db.tables.game_type.DBGameTypeItem;
import javafx.FXMLController;
import javafx.Stageable;
import gui.versus.games.add_game.add_opponent_result.AddOpponentResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.ResourceBundle;

public class AddGameController implements Initializable, FXMLController, Stageable {
    private DBGameItem dbGameItem;

    @FXML
    private AnchorPane mainPane;
    @FXML
    private ComboBox<DBGameTypeItem> cbGameType;
    @FXML
    private DatePicker dpGameDate;
    @FXML
    private ListView<AddOpponentResult> lvAddOpponentResults;
    @FXML
    private Button btnAddNewOpponentResult;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSave;

    public AddGameController(DBGameItem dbGameItem) {
        this.dbGameItem = dbGameItem;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Заполняем список типов игр
        try (Connection connection = DBConnect.getConnection();) {
            DBGameType table = new DBGameType();
            ResultSet rs = table.selectAll(connection);
            while(rs.next()) {
                DBGameTypeItem item = new DBGameTypeItem(rs);
                cbGameType.getItems().add(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Устанавливаем значения
        if (dbGameItem.getIdGame().getValue() == null) {
            // Если игра новая - ставим по умолчанию
            if (cbGameType.getItems().size() > 0) {
                cbGameType.setValue(cbGameType.getItems().get(0));
            }
            dpGameDate.setValue(LocalDate.now());

            btnAddNewOpponentResult_click(null);
            btnAddNewOpponentResult_click(null);
        } else { // Если игра уже существует - изменяем
            // Устанавливаем тип игры
            try (Connection connection = DBConnect.getConnection()) {
                DBGameType table = new DBGameType();
                ResultSet rs = table.selectWithOneParameter(connection, table.getIdGameType(),
                        dbGameItem.getIdGameType().getValue());
                if (rs.next()) {
                    DBGameTypeItem item = new DBGameTypeItem(rs);
                    for (DBGameTypeItem gti : cbGameType.getItems()) {
                        if (gti.equals(item)) {
                            cbGameType.setValue(gti);
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // Устанавливаем дату проведения
            dpGameDate.setValue(new java.sql.Date(dbGameItem.getGameDate().getValue().getTime()).toLocalDate());

            // Добавляем игроков с их результатом
            try (Connection connection = DBConnect.getConnection()) {
                DBGameResult table = new DBGameResult();
                ResultSet rs = table.selectWithOneParameter(connection, table.getIdGame(),
                        dbGameItem.getIdGame().getValue());
                while (rs.next()) {
                    DBGameResultItem item = new DBGameResultItem(rs);
                    AddOpponentResult aor = new AddOpponentResult(item);
                    lvAddOpponentResults.getItems().add(aor);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        // Привязываем к Node-ам их события
        btnAddNewOpponentResult.setOnAction(this::btnAddNewOpponentResult_click);
        btnCancel.setOnAction(this::btnCancel_click);
        btnSave.setOnAction(this::btnSave_click);
    }

    @Override
    public FXMLLoader getLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddGame.fxml"));
        loader.setController(this);
        return loader;
    }

    @Override
    public Pane getMainPane() {
        return mainPane;
    }
    @Override
    public Stage getStage() {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Добавить игру");
        try {
            Scene sc = new Scene(getLoader().load());
            stage.setScene(sc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return stage;
    }

    private void btnAddNewOpponentResult_click(ActionEvent actionEvent) {
        AddOpponentResult aor = new AddOpponentResult(new DBGameResultItem());
        lvAddOpponentResults.getItems().add(aor);
    }

    private void btnSave_click(ActionEvent actionEvent) {
        int errorItemIndex = 0;
        String errorItemPattern = "%d. %s\n";
        StringBuilder errorItems = new StringBuilder();

        DBGameTypeItem gti = cbGameType.getSelectionModel().getSelectedItem();
        Date d = Date.valueOf(dpGameDate.getValue());
        if (gti == null) {
            errorItemIndex++;
            errorItems.append(String.format(errorItemPattern, errorItemIndex,
                    "Не выбран тип игры"));
        }

        if (d.after(Date.valueOf(LocalDate.now()))) {
            errorItemIndex++;
            errorItems.append(String.format(errorItemPattern, errorItemIndex,
                    "Указанная дата больше системной"));
        }

        HashSet<String> opponentNames = new HashSet<>();
        for (AddOpponentResult aor: lvAddOpponentResults.getItems()) {
            opponentNames.add(aor.getController().getSelectedOpponentItem().getName().getValue());
        }
        if (opponentNames.size() != lvAddOpponentResults.getItems().size()) {
            errorItemIndex++;
            errorItems.append(String.format(errorItemPattern, errorItemIndex,
                    "Участники не должны повторяться"));
        }

        if (errorItemIndex > 0) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("При сохранении данных произошли следующие ошибки:\n");
            errorMessage.append(errorItems);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка сохранения игры");
            alert.setContentText(errorMessage.toString());
            alert.show();
        } else {
            if (dbGameItem.getIdGame().getValue() == null) {
                try (Connection connection = DBConnect.getConnection();) {
                    dbGameItem.getIdGameType().setValue(gti.getIdGameType().getValue());
                    dbGameItem.getGameDate().setValue(d);

                    // Добавляем игру в БД
                    DBTable table = new DBGame();
                    int gameId = table.insert(connection, dbGameItem);

                    table = new DBGameResult();
                    for (AddOpponentResult aor: lvAddOpponentResults.getItems()) {
                        DBGameResultItem item = aor.getController().getDbGameResultItem();
                        item.getIdGame().setValue(gameId);
                        table.insert(connection, item);
                    }

                    btnCancel_click(null);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {

            }
        }
    }

    private void btnCancel_click(ActionEvent actionEvent) {
        Stage stage = (Stage)mainPane.getScene().getWindow();
        stage.close();
    }
}
