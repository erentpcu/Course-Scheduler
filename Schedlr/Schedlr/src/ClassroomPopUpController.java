import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClassroomPopUpController {
    @FXML private Label classroomNameLabel;
    @FXML private GridPane gridPane;

    public void initialize(String classroomId) {
        classroomNameLabel.setText("Classroom " + classroomId + " Schedule");
        populateClassroomSchedule(classroomId);
    }

    private void populateClassroomSchedule(String classroomId) {
        String sql = """
            SELECT t.day, t.start_time, l.name AS lecture_name
            FROM lectures l
            INNER JOIN time_slots t ON l.time_slot_id = t.id
            WHERE l.classroom_id = ?
        """;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, classroomId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String day = rs.getString("day");
                String startTime = rs.getString("start_time");
                String lectureName = rs.getString("lecture_name");

                addLectureToSchedule(day, startTime, lectureName);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching classroom schedule: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addLectureToSchedule(String day, String startTime, String lectureName) {
        int columnIndex = getColumnIndexForDay(day);
        int rowIndex = getRowIndexForTime(startTime);

        if (columnIndex != -1 && rowIndex != -1) {
            Label lectureLabel = new Label(lectureName);
            lectureLabel.setStyle("""
                -fx-background-color: #f0f0f0;
                -fx-padding: 10;
                -fx-alignment: center;
                -fx-max-width: infinity;
                -fx-max-height: infinity;
            """);

            GridPane.setFillWidth(lectureLabel, true);
            GridPane.setFillHeight(lectureLabel, true);
            gridPane.add(lectureLabel, columnIndex, rowIndex);
        }
    }

    private int getColumnIndexForDay(String day) {
        return switch (day.toUpperCase()) {
            case "MONDAY" -> 1;
            case "TUESDAY" -> 2;
            case "WEDNESDAY" -> 3;
            case "THURSDAY" -> 4;
            case "FRIDAY" -> 5;
            default -> -1;
        };
    }

    private int getRowIndexForTime(String startTime) {
        return switch (startTime) {
            case "08:30" -> 1;
            case "09:25" -> 2;
            case "10:20" -> 3;
            case "11:15" -> 4;
            case "12:10" -> 5;
            case "13:05" -> 6;
            case "14:00" -> 7;
            case "14:55" -> 8;
            case "15:50" -> 9;
            case "16:45" -> 10;
            case "17:40" -> 11;
            case "18:35" -> 12;
            case "19:30" -> 13;
            case "20:25" -> 14;
            case "21:20" -> 15;
            case "22:15" -> 16;
            default -> -1;
        };
    }
}