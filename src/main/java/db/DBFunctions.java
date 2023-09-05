package db;

public enum DBFunctions {
    GetNumberOfPlayersPlayingTheGame ("GetNumberOfPlayersPlayingTheGame(?)", 1);

    private String name;

    DBFunctions(String name, int numberOfOutParameters) {
        StringBuilder outParametersStr = new StringBuilder();
        for (int i = 0; i < numberOfOutParameters; i++) {
            outParametersStr.append("?");
            if (i < numberOfOutParameters - 1) {
                outParametersStr.append(", ");
            }
        }

        String functionStr = "{ %s = call %s }";
        this.name = String.format(functionStr, outParametersStr, name);
    }

    public String getName() {
        return name;
    }
}
