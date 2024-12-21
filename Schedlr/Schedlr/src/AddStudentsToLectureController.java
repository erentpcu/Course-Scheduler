import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AddStudentsToLectureController {
    @FXML private TextField searchField;
    @FXML private ListView<CheckBox> availableStudentsListView;

    private String lectureId;
    private Stage stage;

    public void initialize(String lectureId) {
        this.lectureId = lectureId;

        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                filterStudents(newValue));

        loadAvailableStudents();
    }

    private void loadAvailableStudents() {
        String sql = """
        SELECT s.id, s.name 
        FROM students s 
        WHERE s.id NOT IN (
            SELECT ss.student_id 
            FROM student_schedule ss 
            WHERE ss.lecture_id = ?
        )
        AND s.id NOT IN (
            SELECT ss2.student_id 
            FROM student_schedule ss2 
            INNER JOIN lectures l2 ON ss2.lecture_id = l2.id 
            INNER JOIN time_slots ts2 ON l2.time_slot_id = ts2.id 
            WHERE ts2.day = (
                SELECT ts1.day 
                FROM lectures l1 
                INNER JOIN time_slots ts1 ON l1.time_slot_id = ts1.id 
                WHERE l1.id = ?
            )
            AND ts2.start_time = (
                SELECT ts1.start_time 
                FROM lectures l1 
                INNER JOIN time_slots ts1 ON l1.time_slot_id = ts1.id 
                WHERE l1.id = ?
            )
        )
        ORDER BY s.name
    """;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, lectureId);
            pstmt.setString(2, lectureId);
            pstmt.setString(3, lectureId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String studentId = rs.getString("id");
                String studentName = rs.getString("name");

                CheckBox cb = new CheckBox(studentName);
                cb.setUserData(studentId);
                availableStudentsListView.getItems().add(cb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load available students.");
        }
    }

    private void filterStudents(String searchText) {
        for (CheckBox cb : availableStudentsListView.getItems()) {
            cb.setVisible(cb.getText().toLowerCase()
                    .contains(searchText.toLowerCase()));
        }
    }

    @FXML
    private void handleAddSelectedStudents() {
        List<String> selectedStudentIds = availableStudentsListView.getItems().stream()
                .filter(CheckBox::isSelected)
                .map(cb -> (String)cb.getUserData())
                .collect(Collectors.toList());

        if (selectedStudentIds.isEmpty()) {
            showAlert("No Selection", "Please select at least one student.");
            return;
        }

        addStudentsToLecture(selectedStudentIds);
        stage.close();
    }

    private void addStudentsToLecture(List<String> studentIds) {
        String sql = "INSERT INTO student_schedule (student_id, lecture_id) VALUES (?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (String studentId : studentIds) {
                pstmt.setString(1, studentId);
                pstmt.setString(2, lectureId);
                pstmt.addBatch();
            }

            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add students to lecture.");
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}