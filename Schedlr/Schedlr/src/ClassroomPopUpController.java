import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ClassroomPopUpController {
    @FXML private Label classroomNameLabel;
    @FXML private GridPane gridPane;

    /**
     * Initialize the classroom schedule display for the given classroom ID.
     *
     * @param classroomId The ID of the classroom.
     */
    public void initialize(String classroomId) {
        classroomNameLabel.setText("Classroom: " + classroomId);

        // Fetch schedule from the database
        Map<String, Map<String, String>> schedule = fetchScheduleFromDatabase(classroomId);

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

    /**
     * Fetch the classroom schedule from the database.
     *
     * @param classroomId The ID of the classroom.
     * @return A nested map where the outer map's key is the day, and the inner map's key is the time slot.
     */
    private Map<String, Map<String, String>> fetchScheduleFromDatabase(String classroomId) {
        String sql = """
            SELECT cs.day, cs.time_slot, l.name AS lecture_name
            FROM classroom_schedule cs
            LEFT JOIN lectures l ON cs.lecture_id = l.id
            WHERE cs.classroom_id = ?
        """;

        Map<String, Map<String, String>> schedule = new HashMap<>();

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, classroomId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String day = rs.getString("day");
                    String timeSlot = rs.getString("time_slot");
                    String lectureName = rs.getString("lecture_name");

                    schedule.computeIfAbsent(day, k -> new HashMap<>())
                            .put(timeSlot, lectureName != null ? lectureName : "Available");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching classroom schedule: " + e.getMessage());
        }

        return schedule;
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

    /**
     * Save a new classroom to the database.
     *
     * @param id       The classroom ID.
     * @param capacity The capacity of the classroom.
     */
    public void saveClassroomToDatabase(String id, int capacity) {
        String sql = "INSERT INTO classrooms (id, capacity) VALUES (?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setInt(2, capacity);
            pstmt.executeUpdate();
            System.out.println("Classroom added to database: " + id);
        } catch (SQLException e) {
            System.out.println("Error saving classroom to database: " + e.getMessage());
        }
    }

    /**
     * Fetch classroom details from the database.
     *
     * @param classroomId The ID of the classroom.
     * @return The Classroom object if found, or null otherwise.
     */
    public Classroom fetchClassroomFromDatabase(String classroomId) {
        String sql = "SELECT * FROM classrooms WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, classroomId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("id");
                    int capacity = rs.getInt("capacity");
                    return new Classroom(id, capacity);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching classroom: " + e.getMessage());
        }
        return null;
    }
}