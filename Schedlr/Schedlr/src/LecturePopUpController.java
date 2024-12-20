import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;

public class LecturePopUpController {
    @FXML private Label lectureNameLabel;
    @FXML private ListView<String> studentsListView;

    // Set lecture data in the pop-up
    public void initialize(String lecture, List<String> registeredStudents) {
        lectureNameLabel.setText("Lecture: " + lecture);

        // Populate the ListView with registered students
        studentsListView.getItems().addAll(registeredStudents);
    }
}