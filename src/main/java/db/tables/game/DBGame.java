package db.tables.game;

import db.QueryUtils;
import db.table.DBColumn;
import db.table.DBTable;
import db.table.DBTableItem;
import db.table.DbColumnValue;
import db.tables.opponent.DBOpponentItem;

import java.sql.*;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

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
    protected <T extends DBTable> PreparedStatement generatePreparedStatement(Connection connection, String query, DBTableItem<T> dbTableItem) throws SQLException {
        PreparedStatement ps = null;

        if (dbTableItem instanceof DBGameItem item) {
            ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            int offset = 0;
            DbColumnValue<?> columnValue;
            if (!isThereAutoIncrement()) {
                columnValue = item.getIdGame();
                ps.setObject(columnValue.getDbColumn().getIndex(), columnValue.getValue());
            } else {
                offset = 1;
            }

            columnValue = item.getIdGameType();
            ps.setObject(columnValue.getDbColumn().getIndex() - offset, columnValue.getValue());

            columnValue = item.getGameDate();
            ps.setObject(columnValue.getDbColumn().getIndex() - offset, columnValue.getValue());
        }

        return ps;
    }

    @Override
    protected <T extends DBTable> String generateUpdateQuery(DBTableItem<T> dbTableItem) {
        String query = null;

        if (dbTableItem instanceof DBGameItem item) {
            Map<Integer, String> updateSetItems = new TreeMap<>();

            DBColumn<?> dbColumn = null;
            String updateSetItem = null;
            if (!isThereAutoIncrement()) {
                dbColumn = idGame;
                updateSetItem = QueryUtils.getUpdateSetItem(dbColumn.getColumnName());
                updateSetItems.put(dbColumn.getIndex(), updateSetItem);
            }

            dbColumn = idGameType;
            updateSetItem = QueryUtils.getUpdateSetItem(dbColumn.getColumnName());
            updateSetItems.put(dbColumn.getIndex(), updateSetItem);

            dbColumn = gameDate;
            updateSetItem = QueryUtils.getUpdateSetItem(dbColumn.getColumnName());
            updateSetItems.put(dbColumn.getIndex(), updateSetItem);

            String updateSetItemsStr = String.join(", ", updateSetItems.values());
            query = "UPDATE %s SET %s WHERE %s IN (%s)";
            query = String.format(query, getTableName(), updateSetItemsStr,
                    idGame.getColumnName(), item.getIdGame().getOldValue());
        }

        return query;
    }
    @Override
    protected <T extends DBTable> String generateDeleteQuery(DBTableItem<T> dbTableItem) {
        String query = null;

        if (dbTableItem instanceof DBGameItem item) {
            // Удаляем строку с указанным id переданного dbTableItem
            query = "DELETE FROM %s WHERE %s IN (%s)";
            query = String.format(query, getTableName(),
                    idGame.getColumnName(), item.getIdGame().getValue());
        }

        return query;
    }
}
