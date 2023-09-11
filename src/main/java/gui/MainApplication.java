package gui;

import db.DBConnect;
import db.tables.opponent.DBOpponent;
import db.tables.opponent.DBOpponentItem;
import javafx.JavaFXUtils;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

public final class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.setTitle("SoccerWeekends (development)");

        MainController msc = new MainController();
        Parent p = msc.getLoader().load(); // Прогружаем FXML
        JavaFXUtils.setZeroAnchors(p); // Устанавливаем отступы AnchorPane по 0 (нужно для растягивания на всё окно)
        Scene sc = new Scene(p);

        //DBPOLYGON();

        stage.setScene(sc);
        stage.show();
    }

    private void DBPOLYGON() {
        try(Connection connection = DBConnect.getConnection();) {
            DBOpponent table = new DBOpponent();

            DBOpponentItem item = new DBOpponentItem();
            item.getName().setValue("CS2");
            table.insert(connection, item);
            /*ResultSet rs = table.selectWithOneParameter(connection, table.getIdOpponent(), 7);
            if (rs.next()) {
                DBOpponentItem item = new DBOpponentItem(rs);
                table.delete(connection, item);
            }*/

            /*DBOpponentItem item = item.getName().setValue("NOOOOOOO");
            table.update(connection, item);*/
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
