import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClassroomPopUpController {
    @FXML private Label classroomNameLabel;
    @FXML private GridPane gridPane;
    public void initialize(String classroomId) {
        System.out.println("Initializing classroom schedule for: " + classroomId); // Debug için

        // Sınıf adı ve kapasitesini al
        String sql = "SELECT id, capacity FROM classrooms WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, classroomId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int capacity = rs.getInt("capacity");
                classroomNameLabel.setText("Classroom " + classroomId + " (Capacity: " + capacity + ") Schedule");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        populateClassroomSchedule(classroomId);
    }
    private void populateClassroomSchedule(String classroomId) {
        String sql = """
       SELECT day, start_time, name, duration
       FROM (
           -- Dersler için sorgu
           SELECT t.day, t.start_time, l.name,
               CAST((strftime('%s', t.end_time) - strftime('%s', t.start_time)) / (55 * 60) AS INTEGER) as duration
           FROM lectures l
           INNER JOIN time_slots t ON l.time_slot_id = t.id
           WHERE l.classroom_id = ?
           
           UNION ALL
           
           -- Toplantılar için sorgu
           SELECT m.day, m.start_time, 'Meeting' as name,
               1 as duration  -- Toplantılar için 1 slot süre
           FROM meeting_schedule ms
           JOIN meetings m ON ms.meeting_id = m.id
           WHERE ms.classroom_id = ?
       ) combined
       ORDER BY day, start_time
   """;
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, classroomId);
            pstmt.setString(2, classroomId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String day = rs.getString("day");
                String startTime = rs.getString("start_time");
                String activityName = rs.getString("name");
                int duration = rs.getInt("duration");

                addLectureToSchedule(day, startTime, activityName, duration);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching classroom schedule: " + e.getMessage());
            e.printStackTrace();
        }

    }
    private void addLectureToSchedule(String day, String startTime, String lectureName, int duration) {
        int columnIndex = getColumnIndexForDay(day);
        int startRowIndex = getRowIndexForTime(startTime);
        if (columnIndex != -1 && startRowIndex != -1) {
            VBox lectureBox = new VBox();
            lectureBox.setStyle("""
               -fx-background-color: #e6e6e6;
               -fx-padding: 5;
               -fx-background-radius: 3;
               -fx-alignment: center;
               -fx-spacing: 2;
           """);
            Label nameLabel = new Label(lectureName);
            nameLabel.setWrapText(true);
            nameLabel.setStyle("-fx-font-weight: bold;");
            Label timeLabel = new Label(startTime);
            timeLabel.setStyle("-fx-font-size: 10;");
            lectureBox.getChildren().addAll(nameLabel, timeLabel);
            GridPane.setRowSpan(lectureBox, duration);
            GridPane.setFillWidth(lectureBox, true);
            GridPane.setFillHeight(lectureBox, true);
            gridPane.add(lectureBox, columnIndex, startRowIndex);
        }
    }
    private int getColumnIndexForDay(String day) {
        return switch (day) {
            case "Monday" -> 1;
            case "Tuesday" -> 2;
            case "Wednesday" -> 3;
            case "Thursday" -> 4;
            case "Friday" -> 5;
            default -> {
                System.out.println("Invalid day: " + day);
                yield -1;
            }
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