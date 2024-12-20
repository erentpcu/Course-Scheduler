import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LecturePopUpController {
    @FXML private Label lectureNameLabel;
    @FXML private ListView<String> studentsListView;

    /**
     * Initialize the lecture details in the pop-up window.
     *
     * @param lectureId The ID of the lecture.
     */
    public void initialize(String lectureId) {
        // Fetch lecture name and students dynamically
        String lectureName = fetchLectureNameFromDatabase(lectureId);
        List<String> registeredStudents = fetchStudentsForLectureFromDatabase(lectureId);

        // Set lecture name in the pop-up
        lectureNameLabel.setText("Lecture: " + lectureName);

        // Populate the ListView with registered students
        studentsListView.getItems().addAll(registeredStudents);
    }

    /**
     * Fetch the lecture name from the database.
     *
     * @param lectureId The ID of the lecture.
     * @return The name of the lecture.
     */
    private String fetchLectureNameFromDatabase(String lectureId) {
        String sql = "SELECT name FROM lectures WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lectureId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching lecture name: " + e.getMessage());
        }
        return "Unknown Lecture"; // Default if no lecture is found
    }

    /**
     * Fetch the list of students registered for the lecture from the database.
     *
     * @param lectureId The ID of the lecture.
     * @return A list of student names.
     */
    private List<String> fetchStudentsForLectureFromDatabase(String lectureId) {
        String sql = """
            SELECT s.name
            FROM students s
            INNER JOIN student_schedule ss ON s.id = ss.student_id
            WHERE ss.lecture_id = ?
        """;
        List<String> students = new ArrayList<>();

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lectureId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(rs.getString("name"));
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching students for lecture: " + e.getMessage());
        }
        return students;
    }
}
