package db;

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
}
