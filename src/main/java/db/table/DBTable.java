package db.table;

import db.QueryUtils;

import java.sql.*;

public abstract class DBTable{
    private String tableName;
    private boolean isThereAutoIncrement;

    public DBTable(String tableName, boolean isThereAutoIncrement) {
        this.tableName = tableName;
        this.isThereAutoIncrement = isThereAutoIncrement;
    }

    public String getTableName() {
        return tableName;
    }

    public boolean isThereAutoIncrement() {
        return isThereAutoIncrement;
    }

    public int getNumberOfColumns(Connection connection) {
        int numberOfColumns = 0;
        try {
            ResultSet rs = selectAll(connection);
            ResultSetMetaData rsmd = rs.getMetaData();
            numberOfColumns = rsmd.getColumnCount();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return numberOfColumns;
    }

    // Вывод всех значений в таблице
    public ResultSet selectAll(Connection connection) {
        ResultSet rs = null;
        try {
            String query = "SELECT * FROM %s";
            query = String.format(query, tableName);
            Statement st = connection.createStatement();
            rs = st.executeQuery(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return rs;
    }

    // Вывод значений с одним условием
    public <T> ResultSet selectWithOneParameter(Connection connection, DBColumn<T> dbColumn, T... value) {
        ResultSet rs = null;
        try {
            String parametersStr = QueryUtils.getParametersStr(value.length);
            String query = "SELECT * FROM %s WHERE %s IN (%s)";
            query = String.format(query, tableName, dbColumn.getColumnName(), parametersStr);
            PreparedStatement st = connection.prepareStatement(query);
            for (int i = 0; i < value.length; i++) {
                st.setObject(i + 1, value[i]);
            }
            rs = st.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return rs;
    }

    // Генерация запроса с параметрами, установка значений DBTableItem-а в запрос.
    protected abstract <T extends DBTable> PreparedStatement generatePreparedStatement(Connection connection, String query, DBTableItem<T> dbTableItem) throws SQLException;
    protected abstract <T extends DBTable> String generateUpdateQuery(DBTableItem<T> dbTableItem);
    protected abstract <T extends DBTable> String generateDeleteQuery(DBTableItem<T> dbTableItem);

    public <T extends DBTable> int insert(Connection connection, DBTableItem<T> dbTableItem) throws SQLException {
        int numberOfColumns = getNumberOfColumns(connection);
        if (isThereAutoIncrement()) numberOfColumns--;

        String parametersStr = QueryUtils.getParametersStr(numberOfColumns);
        String query = "INSERT INTO %s VALUES (%s)";
        query = String.format(query, tableName, parametersStr);

        int insertId = -1;
        PreparedStatement ps = generatePreparedStatement(connection, query, dbTableItem);
        if (ps != null) {
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                insertId = rs.getInt(1);
            }
        }

        return insertId;
    };
    public <T extends DBTable> void update(Connection connection, DBTableItem<T> dbTableItem) throws SQLException {
        String query = generateUpdateQuery(dbTableItem);
        PreparedStatement ps = generatePreparedStatement(connection, query, dbTableItem);
        ps.executeUpdate();
    }
    public <T extends DBTable> void delete(Connection connection, DBTableItem<T> dbTableItem) throws SQLException {
        String query = generateDeleteQuery(dbTableItem);
        Statement s = connection.createStatement();
        s.executeUpdate(query);
    }
}