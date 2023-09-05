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
}
