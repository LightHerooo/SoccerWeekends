package db.table;

import db.QueryUtils;

import java.sql.*;
import java.util.ArrayList;

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
            st.execute(query);

            rs = st.getResultSet();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return rs;
    }

    // Вывод значений с одним условием
    public <R> ResultSet selectWithOneParameter(Connection connection, DBColumn<R> dbColumn, R value) {
        ResultSet rs = null;
        try {
            String query = "SELECT * FROM %s WHERE %s = ?";
            query = String.format(query, tableName, dbColumn.getColumnName());
            PreparedStatement st = connection.prepareStatement(query);
            st.setObject(1, value);
            st.execute();

            rs = st.getResultSet();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return rs;
    }

    public String getInsertQuery(Connection connection) {
        int numberOfColumns = getNumberOfColumns(connection);
        if (isThereAutoIncrement()) numberOfColumns--;

        String parametersStr = QueryUtils.getParametersStr(numberOfColumns);
        String query = "INSERT INTO %s VALUES (%s)";
        return String.format(query, tableName, parametersStr);
    }

    public abstract <T extends DBTable> int insert(Connection connection, DBTableItem<T> dbTableItem) throws SQLException;
    public abstract <T extends DBTable> int update(Connection connection, DBTableItem<T> dbTableItem) throws SQLException;
    public abstract <T extends DBTable> int delete(Connection connection, DBTableItem<T> dbTableItem) throws SQLException;
}