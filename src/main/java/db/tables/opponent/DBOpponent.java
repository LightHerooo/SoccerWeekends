package db.tables.opponent;

import db.table.DBColumn;
import db.table.DBTable;
import db.table.DBTableItem;
import db.table.DbColumnValue;

import java.sql.*;

public class DBOpponent extends DBTable {
    private DBColumn<Integer> idOpponent = new DBColumn<>("id_opponent", 1);
    private DBColumn<String> name = new DBColumn<>("name", 2);
    private DBColumn<String> nameImage = new DBColumn<>("name_image", 3);

    public DBOpponent() {
        super("opponent", true);
    }

    public DBColumn<Integer> getIdOpponent() {
        return idOpponent;
    }
    public DBColumn<String> getName() {
        return name;
    }
    public DBColumn<String> getNameImage() {
        return nameImage;
    }
    @Override
    public <T extends DBTable> int insert(Connection connection, DBTableItem<T> dbTableItem) throws SQLException {
        int insertId = -1;

        if (dbTableItem instanceof DBOpponentItem item) {
            String query = getInsertQuery(connection);
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            int offset = 0;
            DbColumnValue<?> columnValue;
            if (isThereAutoIncrement()) {
                columnValue = item.getIdOpponent();
                ps.setObject(columnValue.getDbColumn().getIndex(), columnValue.getValue());
                offset = 1;
            }

            columnValue = item.getName();
            ps.setObject(columnValue.getDbColumn().getIndex() - offset, columnValue.getValue());

            columnValue = item.getNameImage();
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
