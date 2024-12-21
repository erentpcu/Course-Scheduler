import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AddLectureController {
    @FXML private TextField lectureNameField;
    @FXML private TextField lectureIdField;
    @FXML private TextField timeSlotField;
    @FXML private Button addButton;
    @FXML private Button cancelButton;

    private List<Lecture> lectureList;

    public void setLectureList(List<Lecture> lectureList) {
        this.lectureList = lectureList;
    }

    @FXML
    private void handleAddButtonAction() {
        try {
            // Gather input data
            String name = capitalizeInitials(lectureNameField.getText().trim());
            String id = lectureIdField.getText().trim();
            String[] timeSlotParts = timeSlotField.getText().split(","); // Format: "Monday,09:00,11:00"

            if (timeSlotParts.length != 3) {
                throw new IllegalArgumentException("Time slot must be in the format: 'Day,StartTime,EndTime'");
            }

            String day = timeSlotParts[0].trim();
            String startTime = timeSlotParts[1].trim();
            String endTime = timeSlotParts[2].trim();
            TimeSlot timeSlot = new TimeSlot(day, startTime, endTime);
            int capacity = 30; // Example capacity

            // Save the new lecture to the database
            saveLectureToDatabase(id, name, timeSlot);

            // Optionally, add the new lecture to the in-memory list if needed
            if (lectureList != null) {
                Lecture.addLecture(lectureList, name, id, timeSlot, capacity);
            }

            // Close the pop-up
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveLectureToDatabase(String id, String name, TimeSlot timeSlot) {
        String insertLectureSql = """
            INSERT INTO lectures (id, name, classroom_id, time_slot_id)
            VALUES (?, ?, NULL, ?)
        """;

        String insertTimeSlotSql = """
            INSERT INTO time_slots (day, start_time, end_time)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = Database.connect()) {
            // Step 1: Save the time slot
            PreparedStatement timeSlotStmt = conn.prepareStatement(insertTimeSlotSql, PreparedStatement.RETURN_GENERATED_KEYS);
            timeSlotStmt.setString(1, timeSlot.getDay());
            timeSlotStmt.setString(2, timeSlot.getStartTime());
            timeSlotStmt.setString(3, timeSlot.getEndTime());
            timeSlotStmt.executeUpdate();

            // Get the generated time slot ID
            int timeSlotId;
            try (ResultSet rs = timeSlotStmt.getGeneratedKeys()) {
                if (rs.next()) {
                    timeSlotId = rs.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve generated time slot ID.");
                }
            }

            // Step 2: Save the lecture with the time slot ID
            PreparedStatement lectureStmt = conn.prepareStatement(insertLectureSql);
            lectureStmt.setString(1, id);
            lectureStmt.setString(2, name);
            lectureStmt.setInt(3, timeSlotId);
            lectureStmt.executeUpdate();

            System.out.println("Lecture added to the database: " + name);

        } catch (SQLException e) {
            System.out.println("Error saving lecture to database: " + e.getMessage());
        }
    }

    /**
     * Capitalizes the initials of each word in the given string.
     *
     * @param input The input string.
     * @return The string with capitalized initials.
     */
    private String capitalizeInitials(String input) {
        String[] words = input.split(" ");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                capitalized.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return capitalized.toString().trim();
    }

    @FXML
    private void handleCancelButtonAction() {
        // Close the pop-up without doing anything
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
