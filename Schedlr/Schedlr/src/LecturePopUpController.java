import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LecturePopUpController {
    @FXML private Label lectureNameLabel;
    @FXML private TableView<String> scheduleTable;
    @FXML private TableColumn<String, String> dayColumn;
    @FXML private TableColumn<String, String> timeColumn;

    // Set lecture data in the pop-up
    public void initialize(String lecture) {
        lectureNameLabel.setText("Lecture: " + lecture);

        // Sample data for the lecture schedule
        scheduleTable.getItems().add("Monday 9:00 AM - Math 101");
        scheduleTable.getItems().add("Tuesday 10:00 AM - Physics 102");
        scheduleTable.getItems().add("Wednesday 1:00 PM - Chemistry 103");
    }
}
