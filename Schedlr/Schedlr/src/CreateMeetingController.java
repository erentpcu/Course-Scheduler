import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CreateMeetingController {
    @FXML private ComboBox<String> dayComboBox;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private ListView<String> availableClassroomsListView;
    @FXML private ListView<String> availableStudentsListView;
    @FXML private ListView<String> meetingDetailsListView;
    private ObservableList<String> availableClassrooms = FXCollections.observableArrayList();
    private ObservableList<String> availableStudents = FXCollections.observableArrayList();
    private ObservableList<String> meetingDetails = FXCollections.observableArrayList();
    @FXML
    public void initialize() {
        loadAllClassrooms();
        loadAllStudents();
        availableClassroomsListView.setItems(availableClassrooms);
        availableStudentsListView.setItems(availableStudents);
        meetingDetailsListView.setItems(meetingDetails);
        // Filtreleme işlemi
        dayComboBox.setOnAction(event -> filterAvailability());
        timeComboBox.setOnAction(event -> filterAvailability());
        // Çift tıklama ile meeting detaylarına ekleme
        availableClassroomsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selected = availableClassroomsListView.getSelectionModel().getSelectedItem();
                if (selected != null && meetingDetails.stream()
                        .noneMatch(detail -> detail.startsWith("Classroom:"))) {
                    meetingDetails.add("Classroom: " + selected);
                }
            }
        });
        availableStudentsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selected = availableStudentsListView.getSelectionModel().getSelectedItem();
                if (selected != null && !meetingDetails.contains("Student: " + selected)) {
                    meetingDetails.add("Student: " + selected);
                }
            }
        });
    }
    private void loadAllClassrooms() {
        String sql = "SELECT id, capacity FROM classrooms ORDER BY id";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            availableClassrooms.clear();
            while (rs.next()) {
                String id = rs.getString("id");
                int capacity = rs.getInt("capacity");
                availableClassrooms.add(String.format("%s (Capacity: %d)", id, capacity));
            }
        } catch (SQLException e) {
            System.out.println("Error loading classrooms: " + e.getMessage());
        }
    }
    private void loadAllStudents() {
        String sql = "SELECT name FROM students ORDER BY name";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            availableStudents.clear();
            while (rs.next()) {
                availableStudents.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("Error loading students: " + e.getMessage());
        }
    }
    private void filterAvailability() {
        String day = dayComboBox.getValue();
        String timeSlot = timeComboBox.getValue();
        if (day != null && timeSlot != null) {
            String startTime = timeSlot.split(" - ")[0];
            filterAvailableClassrooms(day, startTime);
            filterAvailableStudents(day, startTime);
        }
    }
    private void filterAvailableClassrooms(String day, String startTime) {
        String sql = """
           SELECT DISTINCT c.id, c.capacity 
           FROM classrooms c 
           WHERE NOT EXISTS (
               SELECT 1 FROM classroom_schedule cs 
               WHERE cs.classroom_id = c.id 
               AND cs.day = ? 
               AND cs.time_slot = ?
           )
           AND NOT EXISTS (
               SELECT 1 FROM meeting_schedule ms 
               WHERE ms.classroom_id = c.id 
               AND ms.day = ? 
               AND ms.time_slot = ?
           )
           ORDER BY c.id
       """;
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, day);
            pstmt.setString(2, startTime);
            pstmt.setString(3, day);
            pstmt.setString(4, startTime);
            ResultSet rs = pstmt.executeQuery();
            availableClassrooms.clear();
            while (rs.next()) {
                String id = rs.getString("id");
                int capacity = rs.getInt("capacity");
                availableClassrooms.add(String.format("%s (Capacity: %d)", id, capacity));
            }
        } catch (SQLException e) {
            System.out.println("Error filtering classrooms: " + e.getMessage());
        }
    }
    private void filterAvailableStudents(String day, String startTime) {
        String sql = """
           SELECT DISTINCT s.name 
           FROM students s 
           WHERE NOT EXISTS (
               SELECT 1 FROM student_schedule ss 
               JOIN lectures l ON ss.lecture_id = l.id 
               JOIN time_slots t ON l.time_slot_id = t.id 
               WHERE ss.student_id = s.id 
               AND t.day = ? 
               AND t.start_time = ?
           )
           AND NOT EXISTS (
               SELECT 1 FROM meeting_participants mp 
               JOIN meetings m ON mp.meeting_id = m.id 
               WHERE mp.student_id = s.id 
               AND m.day = ? 
               AND m.start_time = ?
           )
           ORDER BY s.name
       """;
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, day);
            pstmt.setString(2, startTime);
            pstmt.setString(3, day);
            pstmt.setString(4, startTime);
            ResultSet rs = pstmt.executeQuery();
            availableStudents.clear();
            while (rs.next()) {
                availableStudents.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("Error filtering students: " + e.getMessage());
        }
    }
    @FXML
    private void handleCreateMeeting() {
        String day = dayComboBox.getValue();
        String timeSlot = timeComboBox.getValue();
        if (day == null || timeSlot == null || meetingDetails.isEmpty()) {
            return;
        }
        String startTime = timeSlot.split(" - ")[0];
        String endTime = timeSlot.split(" - ")[1];
        String meetingId = "M" + System.currentTimeMillis();
        try (Connection conn = Database.connect()) {
            conn.setAutoCommit(false);
            try {
                // Insert into meetings table
                insertMeeting(conn, meetingId, day, startTime, endTime);
                // Update classroom_schedule
                String classroomId = getSelectedClassroom().split(" \\(")[0];
                updateClassroomSchedule(conn, classroomId, meetingId, day, startTime);
                // Update student_schedule for each selected student
                for (String detail : meetingDetails) {
                    if (detail.startsWith("Student: ")) {
                        String studentName = detail.substring("Student: ".length());
                        updateStudentSchedule(conn, studentName, meetingId);
                    }
                }
                conn.commit();
                ((Stage) dayComboBox.getScene().getWindow()).close();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Error creating meeting: " + e.getMessage());
        }
    }
    private void insertMeeting(Connection conn, String meetingId, String day, String startTime, String endTime)
            throws SQLException {
        String sql = """
           INSERT INTO meetings (id, day, start_time, end_time, classroom_id) 
           VALUES (?, ?, ?, ?, ?)
       """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, meetingId);
            pstmt.setString(2, day);
            pstmt.setString(3, startTime);
            pstmt.setString(4, endTime);
            pstmt.setString(5, getSelectedClassroom().split(" \\(")[0]);
            pstmt.executeUpdate();
        }
    }
    private void updateClassroomSchedule(Connection conn, String classroomId, String meetingId,
                                         String day, String startTime) throws SQLException {
        // Önce classroom_schedule tablosuna ekle
        String sql = """
       INSERT INTO classroom_schedule (classroom_id, day, time_slot, lecture_id, available) 
       VALUES (?, ?, ?, ?, false)
   """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, classroomId);
            pstmt.setString(2, day);
            pstmt.setString(3, startTime);
            pstmt.setString(4, meetingId);  // meeting_id'yi lecture_id olarak kullan
            pstmt.executeUpdate();
        }
        // Ayrıca meeting_schedule tablosuna da ekle
        sql = """
       INSERT INTO meeting_schedule (classroom_id, day, time_slot, meeting_id, available) 
       VALUES (?, ?, ?, ?, false)
   """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, classroomId);
            pstmt.setString(2, day);
            pstmt.setString(3, startTime);
            pstmt.setString(4, meetingId);
            pstmt.executeUpdate();
        }
    }
    private void updateStudentSchedule(Connection conn, String studentName, String meetingId)
            throws SQLException {
        String studentId = getStudentIdByName(conn, studentName);
        if (studentId == null) {
            throw new SQLException("Student not found: " + studentName);
        }
        // Önce student_schedule tablosuna ekle
        String sql = """
       INSERT INTO student_schedule (student_id, lecture_id) 
       VALUES (?, ?)
   """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, meetingId);  // meeting_id'yi lecture_id olarak kullan
            pstmt.executeUpdate();
        }
        // Ayrıca meeting_participants tablosuna da ekle
        sql = """
       INSERT INTO meeting_participants (meeting_id, student_id) 
       VALUES (?, ?)
   """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, meetingId);
            pstmt.setString(2, studentId);
            pstmt.executeUpdate();
        }
    }
    private String getStudentIdByName(Connection conn, String studentName) throws SQLException {
        String sql = "SELECT id FROM students WHERE name = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("id");
            }
        }
        return null;
    }
    private String getSelectedClassroom() {
        for (String detail : meetingDetails) {
            if (detail.startsWith("Classroom: ")) {
                return detail.substring("Classroom: ".length());
            }
        }
        return "Unknown";
    }
}