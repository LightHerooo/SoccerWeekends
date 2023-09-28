package db.tables.game_type;

import db.QueryUtils;
import db.table.DBColumn;
import db.table.DBTable;
import db.table.DBTableItem;
import db.table.DbColumnValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;

public class DBGameType extends DBTable {
    private DBColumn<Integer> idGameType = new DBColumn<>("id_game_type", 1);
    private DBColumn<String> title = new DBColumn<>("title", 2);
    private DBColumn<String> imageName = new DBColumn<>("image_name", 3);
    private DBColumn<String> info = new DBColumn<>("info", 4);

    public DBGameType() { super("game_type", true); }

    public DBColumn<Integer> getIdGameType() {
        return idGameType;
    }

    public DBColumn<String> getTitle() {
        return title;
    }
    public DBColumn<String> getImageName() {
        return imageName;
    }
    public DBColumn<String> getInfo() {
        return info;
    }

    @Override
    protected <T extends DBTable> PreparedStatement generatePreparedStatement(Connection connection, String query, DBTableItem<T> dbTableItem) throws SQLException {
        PreparedStatement ps = null;

        if (dbTableItem instanceof DBGameTypeItem item) {
            ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            int offset = 0;
            DbColumnValue<?> columnValue;
            if (!isThereAutoIncrement()) {
                columnValue = item.getIdGameType();
                ps.setObject(columnValue.getDbColumn().getIndex(), columnValue.getValue());
            } else {
                offset = 1;
            }

            columnValue = item.getTitle();
            ps.setObject(columnValue.getDbColumn().getIndex() - offset, columnValue.getValue());

            columnValue = item.getImageName();
            ps.setObject(columnValue.getDbColumn().getIndex() - offset, columnValue.getValue());

            columnValue = item.getInfo();
            ps.setObject(columnValue.getDbColumn().getIndex() - offset, columnValue.getValue());
        }

        return ps;
    }

    @Override
    protected <T extends DBTable> String generateUpdateQuery(DBTableItem<T> dbTableItem) {
        String query = null;

        if (dbTableItem instanceof DBGameTypeItem item) {
            Map<Integer, String> updateSetItems = new TreeMap<>();

            DBColumn<?> dbColumn;
            String updateSetItem;
            if (!isThereAutoIncrement()) {
                dbColumn = idGameType;
                updateSetItem = QueryUtils.getUpdateSetItem(dbColumn.getColumnName());
                updateSetItems.put(dbColumn.getIndex(), updateSetItem);
            }

            dbColumn = title;
            updateSetItem = QueryUtils.getUpdateSetItem(dbColumn.getColumnName());
            updateSetItems.put(dbColumn.getIndex(), updateSetItem);

            dbColumn = imageName;
            updateSetItem = QueryUtils.getUpdateSetItem(dbColumn.getColumnName());
            updateSetItems.put(dbColumn.getIndex(), updateSetItem);

            dbColumn = info;
            updateSetItem = QueryUtils.getUpdateSetItem(dbColumn.getColumnName());
            updateSetItems.put(dbColumn.getIndex(), updateSetItem);

            String updateSetItemsStr = String.join(", ", updateSetItems.values());
            query = QueryUtils.collectUpdateQuery(getTableName(), updateSetItemsStr, item.getIdGameType());
        }

        return query;
    }

    @Override
    protected <T extends DBTable> String generateDeleteQuery(DBTableItem<T> dbTableItem) {
        return null;
    }
}
