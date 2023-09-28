package utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class CustomAlerts {
    public static void showWarningAlert(StringBuilder errorItems) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("При сохранении данных произошли следующие ошибки:\n");
        errorMessage.append(errorItems);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Сохранение данных");
        alert.setContentText(errorMessage.toString());
        alert.show();
    }

    public static ButtonType showConfirmationDeleteAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Удаление элемента");
        alert.setHeaderText("Вы уверены, что хотите удалить выбранный элемент?");
        alert.showAndWait();
        return alert.getResult();
    }
}
