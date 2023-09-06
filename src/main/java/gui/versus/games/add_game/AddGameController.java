package gui.versus.games.add_game;

import db.DBConnect;
import db.QueryUtils;
import db.table.DBTable;
import db.tables.game.DBGame;
import db.tables.game.DBGameItem;
import db.tables.game_result.DBGameResult;
import db.tables.game_result.DBGameResultItem;
import db.tables.game_type.DBGameType;
import db.tables.game_type.DBGameTypeItem;
import javafx.FXMLController;
import javafx.Stageable;
import gui.versus.games.add_game.opponent_game_result.OpponentGameResult;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private ListView<OpponentGameResult> lvOpponentGameResults;
    @FXML
    private Button btnDeleteOpponentGameResult;
    @FXML
    private Button btnAddOpponentGameResult;
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

            btnAddOpponentResult_click(null);
            btnAddOpponentResult_click(null);
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
                    OpponentGameResult aor = new OpponentGameResult(item);
                    lvOpponentGameResults.getItems().add(aor);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        // Привязываем к Node-ам их события
        btnDeleteOpponentGameResult.setOnAction(this::btnDeleteOpponentGameResult_click);
        btnAddOpponentGameResult.setOnAction(this::btnAddOpponentResult_click);
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

    private void btnDeleteOpponentGameResult_click(ActionEvent actionEvent) {
        OpponentGameResult ogr = lvOpponentGameResults.getSelectionModel().getSelectedItem();
        if (ogr != null) {
            lvOpponentGameResults.getItems().remove(ogr);
        }
    }

    private void btnAddOpponentResult_click(ActionEvent actionEvent) {
        OpponentGameResult ogr = new OpponentGameResult(new DBGameResultItem());
        lvOpponentGameResults.getItems().add(ogr);
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
        for (OpponentGameResult ogr: lvOpponentGameResults.getItems()) {
            opponentNames.add(ogr.getController().getSelectedOpponentItem().getName().getValue());
        }
        if (opponentNames.size() != lvOpponentGameResults.getItems().size()) {
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
            dbGameItem.getIdGameType().setValue(gti.getIdGameType().getValue());
            dbGameItem.getGameDate().setValue(d);

            // Если игра новая - добавляем
            if (dbGameItem.getIdGame().getValue() == null) {
                try (Connection connection = DBConnect.getConnection();) {
                    // Добавляем игру в БД
                    DBTable table = new DBGame();
                    int gameId = table.insert(connection, dbGameItem);

                    // Добавляем результаты игры
                    table = new DBGameResult();
                    for (OpponentGameResult ogr: lvOpponentGameResults.getItems()) {
                        DBGameResultItem item = ogr.getController().getDbGameResultItem();
                        item.getIdGame().setValue(gameId);
                        table.insert(connection, item);
                    }

                    btnCancel_click(null);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // Если игра не новая - изменяем
                try (Connection connection = DBConnect.getConnection();) {
                    // Изменяем игру в БД
                    DBGame gameTable = new DBGame();
                    gameTable.update(connection, dbGameItem);
                    int gameId = dbGameItem.getIdGame().getValue();

                    // Добавляем/изменяем результаты игр
                    DBGameResult gameResultTable = new DBGameResult();
                    List<Integer> griIds = new ArrayList<>();
                    for (OpponentGameResult ogr: lvOpponentGameResults.getItems()) {
                        DBGameResultItem item = ogr.getController().getDbGameResultItem();
                        item.getIdGame().setValue(gameId);
                        int griId = -1;
                        if (item.getIdGameResult().getValue() == null) {
                            griId = gameResultTable.insert(connection, item);
                        } else {
                            gameResultTable.update(connection, item);
                            griId = item.getIdGameResult().getValue();
                        }
                        griIds.add(griId);
                    }

                    // Удаляем результаты, которые были удалены пользователем
                    String parametersStr = QueryUtils.getParametersStr(griIds.size());
                    String query = "SELECT * FROM %s WHERE %s IN (%s) AND %s NOT IN (%s)";
                    query = String.format(query, gameResultTable.getTableName(), gameResultTable.getIdGame().getColumnName(),
                            gameId, gameResultTable.getIdGameResult().getColumnName(), parametersStr);

                    PreparedStatement ps = connection.prepareStatement(query);
                    for (int i = 0; i < griIds.size(); i++) {
                        ps.setObject(i + 1, griIds.get(i));
                    }

                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        DBGameResultItem item = new DBGameResultItem(rs);
                        gameResultTable.delete(connection, item);
                    }

                    btnCancel_click(null);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void btnCancel_click(ActionEvent actionEvent) {
        Stage stage = (Stage)mainPane.getScene().getWindow();
        stage.close();
    }
}
