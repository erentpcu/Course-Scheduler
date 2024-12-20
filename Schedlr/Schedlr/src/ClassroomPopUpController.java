import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.Map;

public class ClassroomPopUpController {
    @FXML private Label classroomNameLabel;
    @FXML private GridPane gridPane;

    // Method to set classroom details
    public void initialize(String classroom, Map<String, Map<String, String>> schedule) {
        classroomNameLabel.setText("Classroom: " + classroom);

        // Populate the GridPane with the classroom schedule
        for (Map.Entry<String, Map<String, String>> dayEntry : schedule.entrySet()) {
            String day = dayEntry.getKey();
            Map<String, String> timeSlots = dayEntry.getValue();

            for (Map.Entry<String, String> timeSlotEntry : timeSlots.entrySet()) {
                String timeSlot = timeSlotEntry.getKey();
                String lecture = timeSlotEntry.getValue();

                // Find the correct column and row based on day and time
                int columnIndex = getColumnIndexForDay(day);
                int rowIndex = getRowIndexForTime(timeSlot);

                if (columnIndex != -1 && rowIndex != -1) {
                    Label lectureLabel = new Label(lecture);
                    gridPane.add(lectureLabel, columnIndex, rowIndex);
                }
            }
        }
    }

    private int getColumnIndexForDay(String day) {
        switch (day.toUpperCase()) {
            case "MONDAY": return 1;
            case "TUESDAY": return 2;
            case "WEDNESDAY": return 3;
            case "THURSDAY": return 4;
            case "FRIDAY": return 5;
            case "SATURDAY": return 6;
            case "SUNDAY": return 7;
            default: return -1;
        }
    }

    private int getRowIndexForTime(String time) {
        switch (time) {
            case "08:30 - 09:25": return 2;
            case "09:25 - 10:20": return 3;
            case "10:20 - 11:15": return 4;
            case "11:15 - 12:10": return 5;
            case "12:10 - 13:05": return 6;
            case "13:05 - 14:00": return 7;
            case "14:00 - 14:55": return 8;
            case "14:55 - 15:50": return 9;
            default: return -1;
        }
    }
}