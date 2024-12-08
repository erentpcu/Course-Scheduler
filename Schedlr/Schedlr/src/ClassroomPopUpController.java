import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ClassroomPopUpController {
    @FXML private Label classroomNameLabel;
    @FXML private TableView<String> scheduleTable;
    @FXML private TableColumn<String, String> dayColumn;
    @FXML private TableColumn<String, String> timeColumn;

    // Method to set classroom details
    public void initialize(String classroom) {
        classroomNameLabel.setText("Classroom: " + classroom);

        // Sample data for the classroom schedule
        ObservableList<String> schedule = FXCollections.observableArrayList(
                "Monday 9:00 AM - Math 101",
                "Tuesday 10:00 AM - Physics 102",
                "Wednesday 1:00 PM - Chemistry 103"
        );
        scheduleTable.setItems(schedule);
    }
}
