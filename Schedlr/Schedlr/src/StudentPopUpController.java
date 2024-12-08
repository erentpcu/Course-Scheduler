import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class StudentPopUpController {

    @FXML private Label studentNameLabel;
    @FXML private Label studentIdLabel;
    @FXML private TableView<String> scheduleTable;
    @FXML private TableColumn<String, String> dayColumn;
    @FXML private TableColumn<String, String> timeColumn;
    @FXML private Button closeButton;

    // Sample schedule data (This should be dynamically populated based on the selected student)
    private final String[] scheduleData = {
            "Monday 9:00 AM - Math 101",
            "Tuesday 10:00 AM - Physics 102",
            "Wednesday 1:00 PM - Chemistry 103"
    };

    @FXML
    public void initialize() {
        // Set up the schedule table columns
        dayColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().split(" - ")[0]));
        timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().split(" - ")[1]));

        // Add schedule data to the table
        scheduleTable.getItems().addAll(scheduleData);
    }

    // Method to close the pop-up window
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    // Method to set the student details (This will be called from the main controller)
    public void setStudentDetails(String studentName, String studentId) {
        studentNameLabel.setText("Student: " + studentName);
        studentIdLabel.setText("ID: " + studentId);
    }
}
