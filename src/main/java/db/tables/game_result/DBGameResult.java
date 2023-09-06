package db.tables.game_result;

import db.QueryUtils;
import db.table.DBColumn;
import db.table.DBTable;
import db.table.DBTableItem;
import db.table.DbColumnValue;
import db.tables.game.DBGameItem;
import db.tables.opponent.DBOpponentItem;

import java.sql.*;
import java.util.Map;
import java.util.TreeMap;

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
    protected <T extends DBTable> PreparedStatement generatePreparedStatement(Connection connection, String query, DBTableItem<T> dbTableItem) throws SQLException {
        PreparedStatement ps = null;

        if (dbTableItem instanceof DBGameResultItem item) {
            ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            int offset = 0;
            DbColumnValue<?> columnValue;
            if (!isThereAutoIncrement()) {
                columnValue = item.getIdGameResult();
                ps.setObject(columnValue.getDbColumn().getIndex(), columnValue.getValue());
            } else {
                offset = 1;
            }

            columnValue = item.getIdGame();
            ps.setObject(columnValue.getDbColumn().getIndex() - offset, columnValue.getValue());

            columnValue = item.getIdOpponent();
            ps.setObject(columnValue.getDbColumn().getIndex() - offset, columnValue.getValue());

            columnValue = item.getScore();
            ps.setObject(columnValue.getDbColumn().getIndex() - offset, columnValue.getValue());
        }

        return ps;
    }

    @Override
    protected <T extends DBTable> String generateUpdateQuery(DBTableItem<T> dbTableItem) {
        String query = null;

        if (dbTableItem instanceof DBGameResultItem item) {
            Map<Integer, String> updateSetItems = new TreeMap<>();

            DBColumn<?> dbColumn = null;
            String updateSetItem = null;
            if (!isThereAutoIncrement()) {
                dbColumn = idGameResult;
                updateSetItem = QueryUtils.getUpdateSetItem(dbColumn.getColumnName());
                updateSetItems.put(dbColumn.getIndex(), updateSetItem);
            }

            dbColumn = idGame;
            updateSetItem = QueryUtils.getUpdateSetItem(dbColumn.getColumnName());
            updateSetItems.put(dbColumn.getIndex(), updateSetItem);

            dbColumn = idOpponent;
            updateSetItem = QueryUtils.getUpdateSetItem(dbColumn.getColumnName());
            updateSetItems.put(dbColumn.getIndex(), updateSetItem);

            dbColumn = score;
            updateSetItem = QueryUtils.getUpdateSetItem(dbColumn.getColumnName());
            updateSetItems.put(dbColumn.getIndex(), updateSetItem);

            String updateSetItemsStr = String.join(", ", updateSetItems.values());
            query = "UPDATE %s SET %s WHERE %s IN (%s)";
            query = String.format(query, getTableName(), updateSetItemsStr,
                    idGameResult.getColumnName(), item.getIdGameResult().getOldValue());
        }

        return query;
    }

    @Override
    protected <T extends DBTable> String generateDeleteQuery(DBTableItem<T> dbTableItem) {
        String query = null;

        if (dbTableItem instanceof DBGameResultItem item) {
            query = "DELETE FROM %s WHERE %s IN (%s)";
            query = String.format(query, getTableName(),
                    idGameResult.getColumnName(), item.getIdGameResult().getValue());
        }

        return query;
    }
}
