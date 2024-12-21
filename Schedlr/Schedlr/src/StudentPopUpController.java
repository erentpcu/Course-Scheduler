import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentPopUpController {

    @FXML private Label studentNameLabel;
    @FXML private GridPane gridPane; // Reference to the GridPane

    /**
     * Sets the student's details and populates the schedule.
     *
     * @param studentId The ID of the student.
     */
    public void setStudentDetails(String studentId) {
        System.out.println("Received studentId: " + studentId); // Debug
        String studentName = fetchStudentNameFromDatabase(studentId);
        if (studentName != null) {
            studentNameLabel.setText(studentName + "'s Weekly Schedule");
            populateStudentSchedule(studentId);
        } else {
            studentNameLabel.setText("Student not found");
        }
    }


    /**
     * Fetch the student's name from the database using the student ID.
     *
     * @param studentId The ID of the student.
     * @return The name of the student, or null if not found.
     */
    private String fetchStudentNameFromDatabase(String studentId) {
        String sql = "SELECT name FROM students WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            System.out.println("Executing query: " + pstmt.toString()); // Debug
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching student name: " + e.getMessage());
        }

        return null;
    }


    /**
     * Populates the student's schedule into the GridPane.
     *
     * @param studentId The ID of the student.
     */
    private void populateStudentSchedule(String studentId) {
        String sql = """
            SELECT t.day, t.start_time, t.end_time, l.name
            FROM student_schedule ss
            INNER JOIN lectures l ON ss.lecture_id = l.id
            INNER JOIN time_slots t ON l.time_slot_id = t.id
            WHERE ss.student_id = ?
        """;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String day = rs.getString("day");
                    String startTime = rs.getString("start_time");
                    String endTime = rs.getString("end_time");
                    String lectureName = rs.getString("name");

                    addLectureToSchedule(day, startTime, endTime, lectureName);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching student schedule: " + e.getMessage());
        }
    }

    /**
     * Adds a lecture to the GridPane based on the day and time slot.
     *
     * @param day         The day of the lecture.
     * @param startTime   The start time of the lecture.
     * @param endTime     The end time of the lecture.
     * @param lectureName The name of the lecture.
     */
    private void addLectureToSchedule(String day, String startTime, String endTime, String lectureName) {
        int columnIndex = getColumnIndexForDay(day);
        int rowIndex = getRowIndexForTime(startTime, endTime);

        if (columnIndex != -1 && rowIndex != -1) {
            Label lectureLabel = new Label(lectureName);
            gridPane.add(lectureLabel, columnIndex, rowIndex);
        }
    }

    /**
     * Maps a day to the corresponding column index.
     *
     * @param day The day of the week.
     * @return The column index, or -1 if the day is invalid.
     */
    private int getColumnIndexForDay(String day) {
        return switch (day.toUpperCase()) {
            case "MONDAY" -> 1;
            case "TUESDAY" -> 2;
            case "WEDNESDAY" -> 3;
            case "THURSDAY" -> 4;
            case "FRIDAY" -> 5;
            case "SATURDAY" -> 6;
            case "SUNDAY" -> 7;
            default -> -1;
        };
    }

    /**
     * Maps a time slot to the corresponding row index.
     *
     * @param startTime The start time of the lecture.
     * @param endTime   The end time of the lecture.
     * @return The row index, or -1 if the time slot is invalid.
     */
    private int getRowIndexForTime(String startTime, String endTime) {
        return switch (startTime + " - " + endTime) {
            case "08:30 - 09:25" -> 2;
            case "09:25 - 10:20" -> 3;
            case "10:20 - 11:15" -> 4;
            case "11:15 - 12:10" -> 5;
            case "12:10 - 13:05" -> 6;
            case "13:05 - 14:00" -> 7;
            case "14:00 - 14:55" -> 8;
            case "14:55 - 15:50" -> 9;
            default -> -1;
        };
    }

}
