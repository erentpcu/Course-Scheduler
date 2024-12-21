import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentPopUpController {
    @FXML private Label studentNameLabel;
    @FXML private GridPane gridPane;

    private String fetchStudentNameFromDatabase(String studentId) {
        String sql = "SELECT name FROM students WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching student name: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void setStudentDetails(String studentId) {
        String studentName = fetchStudentNameFromDatabase(studentId);
        if (studentName != null) {
            studentNameLabel.setText(studentName + "'s Schedule");
            populateStudentSchedule(studentId);
        } else {
            studentNameLabel.setText("Student Not Found");
        }
    }

    private void populateStudentSchedule(String studentId) {
        String sql = """
            SELECT t.day, t.start_time, l.name
            FROM student_schedule ss
            INNER JOIN lectures l ON ss.lecture_id = l.id
            INNER JOIN time_slots t ON l.time_slot_id = t.id
            WHERE ss.student_id = ?
        """;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String day = rs.getString("day");
                String startTime = rs.getString("start_time");
                String lectureName = rs.getString("name");

                addLectureToSchedule(day, startTime, lectureName);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching student schedule: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addLectureToSchedule(String day, String startTime, String lectureName) {
        int columnIndex = getColumnIndexForDay(day);
        int rowIndex = getRowIndexForTime(startTime);

        if (columnIndex != -1 && rowIndex != -1) {
            Label lectureLabel = new Label(lectureName);
            lectureLabel.setStyle("""
                -fx-background-color: #e6e6e6; 
                -fx-padding: 5; 
                -fx-background-radius: 3; 
                -fx-alignment: center;
                -fx-max-width: infinity;
            """);
            GridPane.setFillWidth(lectureLabel, true);
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
            case "08:30" -> 2;
            case "09:25" -> 3;
            case "10:20" -> 4;
            case "11:15" -> 5;
            case "12:10" -> 6;
            case "13:05" -> 7;
            case "14:00" -> 8;
            case "14:55" -> 9;
            case "15:50" -> 10;
            case "16:45" -> 11;
            case "17:40" -> 12;
            case "18:35" -> 13;
            case "19:30" -> 14;
            case "20:25" -> 15;
            case "21:20" -> 16;
            case "22:15" -> 17;
            default -> -1;
        };
    }
}