package db.tables.game_type;

import db.table.DBColumn;
import db.table.DBTable;
import db.table.DBTableItem;
import db.table.DbColumnValue;
import db.tables.opponent.DBOpponentItem;

import java.sql.*;

public class DBGameType extends DBTable {
    private DBColumn<Integer> idGameType = new DBColumn<>("id_game_type", 1);
    private DBColumn<String> title = new DBColumn<>("title", 2);

    public DBGameType() { super("game_type", true); }

    public DBColumn<Integer> getIdGameType() {
        return idGameType;
    }

    public DBColumn<String> getTitle() {
        return title;
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
        }

        return ps;
    }

    @Override
    protected <T extends DBTable> String generateUpdateQuery(DBTableItem<T> dbTableItem) {
        return null;
    }

    @Override
    protected <T extends DBTable> String generateDeleteQuery(DBTableItem<T> dbTableItem) {
        return null;
    }
}
