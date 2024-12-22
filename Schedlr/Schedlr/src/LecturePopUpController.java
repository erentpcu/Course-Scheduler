import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LecturePopUpController {
    @FXML private Label courseCodeLabel;
    @FXML private Label lecturerLabel;
    @FXML private Label timeLabel;
    @FXML private Label durationLabel;
    @FXML private Label classroomLabel;
    @FXML private Label enrolledStudentsLabel;
    @FXML private ListView<String> studentsListView;
    @FXML private Button saveButton;
    @FXML private Button addButton;
    @FXML private Button deleteButton;

    private int enrolledStudentsCount = 0;
    private String lectureId;
    private boolean isDeleteMode = false;
    private List<String> originalStudents = new ArrayList<>();
    private List<String> removedStudents = new ArrayList<>();

    public void initialize(String lectureId) {
        this.lectureId = lectureId;
        loadLectureDetails();
        loadEnrolledStudents();
    }
    private void loadLectureDetails() {
        String sql = """
   SELECT l.name, l.id, t.day, t.start_time, t.end_time, c.id as classroom_id, l.lecturer
   FROM lectures l
   LEFT JOIN time_slots t ON l.time_slot_id = t.id
   LEFT JOIN classrooms c ON l.classroom_id = c.id
   WHERE l.id = ?
   """;
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lectureId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                courseCodeLabel.setText(rs.getString("name"));
                String startTime = rs.getString("start_time");
                timeLabel.setText("Starting time: " + startTime);
                String lecturer = rs.getString("lecturer");
                durationLabel.setText("Lecturer: " + (lecturer != null ? lecturer : "Not assigned"));
                String classroom = rs.getString("classroom_id");
                classroomLabel.setText("Classroom: " + (classroom != null ? classroom : "Not allocated"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load lecture details.");
        }

    }
    private void loadEnrolledStudents() {
        studentsListView.getItems().clear();
        String sql = """
           SELECT s.name
           FROM students s
           INNER JOIN student_schedule ss ON s.id = ss.student_id
           WHERE ss.lecture_id = ?
           ORDER BY s.name
       """;
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lectureId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                studentsListView.getItems().add(rs.getString("name"));
            }
            updateEnrolledStudentsLabel();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load enrolled students.");
        }
    }

    private void updateEnrolledStudentsLabel() {
        enrolledStudentsCount = studentsListView.getItems().size();
        enrolledStudentsLabel.setText("Enrolled Students: " + enrolledStudentsCount);
    }

    @FXML
    private void handleAddStudents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddStudentsToLecture.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Add Students to Course");
            stage.initModality(Modality.APPLICATION_MODAL);
            AddStudentsToLectureController controller = loader.getController();
            controller.initialize(lectureId);
            controller.setStage(stage);
            stage.setScene(scene);
            stage.showAndWait();
            loadEnrolledStudents(); // Refresh the list after adding
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open add students window.");
        }
    }
    @FXML
    private void handleDeleteStudents() {
        isDeleteMode = !isDeleteMode;
        saveButton.setVisible(isDeleteMode);
        if (isDeleteMode) {
            originalStudents = new ArrayList<>(studentsListView.getItems());
            updateListViewWithDeleteButtons();
        } else {
            loadEnrolledStudents();
        }
    }
    private void updateListViewWithDeleteButtons() {
        ObservableList<String> currentStudents = studentsListView.getItems();
        studentsListView.setCellFactory(lv -> new ListCell<String>() {
            private final Button deleteButton = new Button("-");
            private final HBox hbox = new HBox(10);
            {
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    String student = getItem();
                    if (student != null) {
                        currentStudents.remove(student);
                        removedStudents.add(student);
                    }
                });
            }
            @Override
            protected void updateItem(String student, boolean empty) {
                super.updateItem(student, empty);
                if (empty || student == null) {
                    setGraphic(null);
                } else {
                    hbox.getChildren().clear();
                    Label label = new Label(student);
                    hbox.getChildren().addAll(label, deleteButton);
                    setGraphic(hbox);
                }
            }
        });
    }
    @FXML
    private void handleSaveChanges() {
        if (removedStudents.isEmpty()) {
            showAlert("Warning", "No students selected for removal.");
            return;
        }
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Selected Students");
        confirmAlert.setContentText("Are you sure you want to remove " + removedStudents.size() +
                " student(s) from this lecture?");
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                saveStudentChanges();
                isDeleteMode = false;
                saveButton.setVisible(false);

                // Liste görünümünü normal haline döndür
                studentsListView.setCellFactory(lv -> new ListCell<String>() {
                    @Override
                    protected void updateItem(String student, boolean empty) {
                        super.updateItem(student, empty);
                        if (empty || student == null) {
                            setText(null);
                        } else {
                            setText(student);
                        }
                    }
                });

                loadEnrolledStudents();
                showAlert("Success", "Students have been removed successfully.");
            } else {
                // İptal edilirse silinen öğrencileri geri yükle
                removedStudents.clear();
                loadEnrolledStudents();
            }
        });
    }
    private void saveStudentChanges() {
        try (Connection conn = Database.connect()) {
            String deleteSql = "DELETE FROM student_schedule WHERE lecture_id = ? AND student_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                for (String studentName : removedStudents) {
                    String studentId = getStudentIdByName(studentName);
                    if (studentId != null) {
                        pstmt.setString(1, lectureId);
                        pstmt.setString(2, studentId);
                        pstmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save changes.");
        }
        removedStudents.clear();
        originalStudents.clear();
    }
    private String getStudentIdByName(String studentName) {
        String sql = "SELECT id FROM students WHERE name = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}