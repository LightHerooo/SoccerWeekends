package db;

import db.table.DBTable;
import db.table.DBTableItem;
import db.table.DbColumnValue;

import java.util.ArrayList;

public class QueryUtils {
    public static String getParametersStr(int numberOfParameters) {
        ArrayList<String> parameters = new ArrayList<>();
        for (int i = 0; i < numberOfParameters; i++) {
            parameters.add("?");
        }

        return String.join(",", parameters);
    }

    public static String getUpdateSetItem(String columnName) {
        String parametersStr = getParametersStr(1);
        String updateSetItemPattern = "%s = %s";
        return String.format(updateSetItemPattern, columnName, parametersStr);
    }

    public static <T> String collectUpdateQuery(String dbTableName, String updateSetItems, DbColumnValue<T> dbColumnValue) {
        String query = "UPDATE %s SET %s WHERE %s IN (%s)";
        return String.format(query, dbTableName, updateSetItems, dbColumnValue.getDbColumn().getColumnName(), dbColumnValue.getOldValue());
    }

    public static <T> String collectDeleteQuery(String dbTableName, DbColumnValue<T> dbColumnValue) {
        String query = "DELETE FROM %s WHERE %s IN (%s)";
        return String.format(query, dbTableName, dbColumnValue.getDbColumn().getColumnName(), dbColumnValue.getValue());
    }
}
