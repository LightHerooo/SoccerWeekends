package db.tables.opponent;

import db.QueryUtils;
import db.table.DBColumn;
import db.table.DBTable;
import db.table.DBTableItem;
import db.table.DbColumnValue;

import java.sql.*;
import java.util.*;

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
    protected <T extends DBTable> PreparedStatement generatePreparedStatement(Connection connection, String query, DBTableItem<T> dbTableItem) throws SQLException {
        PreparedStatement ps = null;

          if (dbTableItem instanceof DBOpponentItem item) {
            ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            int offset = 0;
            DbColumnValue<?> columnValue;

            // Проверяем, есть ли auto_increment
            if (!isThereAutoIncrement()) {
                // Если нет, то мы должны добавить параметр в начало запроса (туда будет сохраняться указанный id)
                columnValue = item.getIdOpponent();
                ps.setObject(columnValue.getDbColumn().getIndex(), columnValue.getValue());
            } else {
                // Если есть, параметр не нужен, указываем смещение всех параметров на 1
                offset = 1;
            }

            columnValue = item.getName();
            ps.setObject(columnValue.getDbColumn().getIndex() - offset, columnValue.getValue());

            columnValue = item.getNameImage();
            ps.setObject(columnValue.getDbColumn().getIndex() - offset, columnValue.getValue());
        }

        return ps;
    }
    @Override
    protected <T extends DBTable> String generateUpdateQuery(DBTableItem<T> dbTableItem) {
        String query = null;

        if (dbTableItem instanceof DBOpponentItem item) {
            /* Создаем мапу, которая будет сортировать SET выражения по индексу колонок
                Это необходимо, чтобы корректно вставить будущие параметры */
            Map<Integer, String> updateSetItems = new TreeMap<>();

            DBColumn<?> dbColumn;
            String updateSetItem;
            if (!isThereAutoIncrement()) {
                // Если нет auto-increment, добавляем параметр изменения id в начало выражения SET
                dbColumn = idOpponent;
                updateSetItem = QueryUtils.getUpdateSetItem(dbColumn.getColumnName());
                updateSetItems.put(dbColumn.getIndex(), updateSetItem);
            }

            /* Нужно добавить все поля таблицы в мапу, чтобы собрать запрос изменения
                (Поля id, name и так далее)*/
            dbColumn = nameImage;
            /* Генерируем SET выражение, которое в будущем будем объединять с остальными через запятую
                Генерируется: <название_колонки> = ? (? - параметр для вставки значения, используется в PreparedStatement)*/
            updateSetItem = QueryUtils.getUpdateSetItem(dbColumn.getColumnName());
            updateSetItems.put(dbColumn.getIndex(), updateSetItem);

            dbColumn = name;
            updateSetItem = QueryUtils.getUpdateSetItem(dbColumn.getColumnName());
            updateSetItems.put(dbColumn.getIndex(), updateSetItem);

            /* Собираем полученную мапу и разделяем запятыми
                Получится общее выражение SET с */
            String updateSetItemsStr = String.join(", ", updateSetItems.values());
            query = "UPDATE %s SET %s WHERE %s IN (%s)";
            query = String.format(query, getTableName(), updateSetItemsStr,
                    idOpponent.getColumnName(), item.getIdOpponent().getOldValue());
        }

        return query;
    }
    @Override
    protected <T extends DBTable> String generateDeleteQuery(DBTableItem<T> dbTableItem) {
        String query = null;

        if (dbTableItem instanceof DBOpponentItem item) {
            // Удаляем строку с указанным id переданного dbTableItem
            query = "DELETE FROM %s WHERE %s IN (%s)";
            query = String.format(query, getTableName(),
                    idOpponent.getColumnName(), item.getIdOpponent().getValue());
        }

        return query;
    }
}
