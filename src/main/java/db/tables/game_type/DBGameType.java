package db.tables.game_type;

import db.table.DBColumn;
import db.table.DBTable;
import db.table.DBTableItem;
import db.table.DbColumnValue;

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
    public <T extends DBTable> int insert(Connection connection, DBTableItem<T> dbTableItem) throws SQLException {
        int insertId = -1;

        if (dbTableItem instanceof DBGameTypeItem item) {
            String query = getInsertQuery(connection);
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            int offset = 0;
            DbColumnValue<?> columnValue;
            if (isThereAutoIncrement()) {
                columnValue = item.getIdGameType();
                ps.setObject(columnValue.getDbColumn().getIndex(), columnValue.getValue());
                offset = 1;
            }

            columnValue = item.getTitle();
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
