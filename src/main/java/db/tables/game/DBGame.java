package db.tables.game;

import db.table.DBColumn;
import db.table.DBTable;
import db.table.DBTableItem;
import db.table.DbColumnValue;

import java.sql.*;
import java.util.Date;

public class DBGame extends DBTable {
    private DBColumn<Integer> idGame = new DBColumn<>("id_game", 1);
    private DBColumn<Integer> idGameType = new DBColumn<>("id_game_type", 2);
    private DBColumn<Date> gameDate = new DBColumn<>("game_date", 3);

    public DBGame() {
        super("game", true);
    }

    public DBColumn<Integer> getIdGame() {
        return idGame;
    }

    public DBColumn<Integer> getIdGameType() {
        return idGameType;
    }

    public DBColumn<Date> getGameDate() {
        return gameDate;
    }

    @Override
    public <T extends DBTable> int insert(Connection connection, DBTableItem<T> dbTableItem) throws SQLException {
        int insertId = -1;
        if (dbTableItem instanceof DBGameItem item) {
            String query = getInsertQuery(connection);
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            int offset = 0;
            DbColumnValue<?> columnValue;
            if (isThereAutoIncrement()) {
                columnValue = item.getIdGame();
                ps.setObject(columnValue.getDbColumn().getIndex(), columnValue.getValue());
                offset = 1;
            }

            columnValue = item.getIdGameType();
            ps.setObject(columnValue.getDbColumn().getIndex() - offset, columnValue.getValue());

            columnValue = item.getGameDate();
            ps.setObject(columnValue.getDbColumn().getIndex() - offset, columnValue.getValue());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                insertId = rs.getInt(1);
            }
        }

        return insertId;
    }
    @Override
    public <T extends DBTable> int update(Connection connection, DBTableItem<T> dbTableItem) throws SQLException {
        return 0;
    }

    @Override
    public <T extends DBTable> int delete(Connection connection, DBTableItem<T> dbTableItem) throws SQLException {
        return 0;
    }
}
