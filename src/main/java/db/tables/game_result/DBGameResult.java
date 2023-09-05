package db.tables.game_result;

import db.table.DBColumn;
import db.table.DBTable;
import db.table.DBTableItem;
import db.table.DbColumnValue;

import java.sql.*;

public class DBGameResult extends DBTable {

    private DBColumn<Integer> idGameResult = new DBColumn<>("id_game_result", 1);
    private DBColumn<Integer> idGame = new DBColumn<>("id_game", 2);
    private DBColumn<Integer> idOpponent = new DBColumn<>("id_opponent", 3);
    private DBColumn<Integer> score = new DBColumn<>("score", 4);

    public DBGameResult() {
        super("game_result", true);
    }

    public DBColumn<Integer> getIdGameResult() {
        return idGameResult;
    }

    public DBColumn<Integer> getIdGame() {
        return idGame;
    }

    public DBColumn<Integer> getIdOpponent() {
        return idOpponent;
    }

    public DBColumn<Integer> getScore() {
        return score;
    }

    @Override
    public <T extends DBTable> int insert(Connection connection, DBTableItem<T> dbTableItem) throws SQLException {
        int insertId = -1;

        if (dbTableItem instanceof DBGameResultItem item) {
            String query = getInsertQuery(connection);
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            int offset = 0;
            DbColumnValue<?> columnValue;
            if (isThereAutoIncrement()) {
                columnValue = item.getIdGameResult();
                ps.setObject(columnValue.getDbColumn().getIndex(), columnValue.getValue());
                offset = 1;
            }

            columnValue = item.getIdGame();
            ps.setObject(columnValue.getDbColumn().getIndex() - offset, columnValue.getValue());

            columnValue = item.getIdOpponent();
            ps.setObject(columnValue.getDbColumn().getIndex() - offset, columnValue.getValue());

            columnValue = item.getScore();
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
