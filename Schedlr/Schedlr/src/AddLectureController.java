import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class AddLectureController {
    @FXML private TextField lectureNameField;
    @FXML private TextField lectureIdField;
    @FXML private TextField timeSlotField;
    @FXML private Button addButton;
    @FXML private Button cancelButton;

    private List<Lecture> lectureList;

    public void setLectureList(List<Lecture> lectureList) {
        this.lectureList = lectureList;
    }

    @FXML
    private void handleAddButtonAction() {
        try {
            String name = lectureNameField.getText();
            int id = Integer.parseInt(lectureIdField.getText());
            String[] timeSlotParts = timeSlotField.getText().split(",");
            TimeSlot timeSlot = new TimeSlot(timeSlotParts[0], timeSlotParts[1], timeSlotParts[2]);

            // Add the new lecture to the list
            Lecture.addLecture(lectureList, name, id, timeSlot, 30); // Example capacity is 30

            // Close the pop-up
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelButtonAction() {
        // Close the pop-up without doing anything
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
