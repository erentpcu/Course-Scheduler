import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AddLectureController {
    @FXML
    private TextField lectureNameField;
    @FXML private ComboBox<String> dayComboBox;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private TextField lecturerField;
    @FXML private ListView<CheckBox> studentsListView;
    @FXML private ComboBox<String> classroomComboBox;
    @FXML private Spinner<Integer> durationSpinner;
    @FXML private Button addButton;

    @FXML
    public void initialize() {
        // Load students into ListView with checkboxes
        loadStudents();

        // Load classrooms into ComboBox
        loadClassrooms();
    }

    private void loadStudents() {
        ObservableList<CheckBox> studentCheckBoxes = FXCollections.observableArrayList();

        try (Connection conn = Database.connect()) {
            String sql = "SELECT name FROM students";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                CheckBox cb = new CheckBox(rs.getString("name"));
                studentCheckBoxes.add(cb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        studentsListView.setItems(studentCheckBoxes);
    }

    private void loadClassrooms() {
        try (Connection conn = Database.connect()) {
            String sql = "SELECT id FROM classrooms";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                classroomComboBox.getItems().add(rs.getString("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddButtonAction() {
        String name = lectureNameField.getText();
        String day = dayComboBox.getValue();
        String time = timeComboBox.getValue();
        String lecturer = lecturerField.getText();
        String classroom = classroomComboBox.getValue();
        int duration = durationSpinner.getValue();

        // Get selected students
        List<String> selectedStudents = studentsListView.getItems().stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());

        // Save to database
        saveLectureToDatabase(name, day, time, lecturer, classroom, duration, selectedStudents);

        // Close the window
        ((Stage) addButton.getScene().getWindow()).close();
    }

    private void saveLectureToDatabase(String name, String day, String time,
                                       String lecturer, String classroom,
                                       int duration, List<String> students) {
        // Implement database saving logic here
    }
}