import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Controller {

    @FXML
    private ListView<String> studentsListView;

    @FXML
    private ListView<String> classroomsListView;

    @FXML
    private ListView<String> lecturesListView;

    // Handle "Find" button in Students section
    @FXML
    private void handleFindStudent() {
        showAlert("Find Student", "This feature is under construction.");
    }

    // Handle "Add" button in Students section
    @FXML
    private void handleAddStudent() {
        showAlert("Add Student", "This feature is under construction.");
    }

    // Handle "Find" button in Classrooms section
    @FXML
    private void handleFindClassroom() {
        showAlert("Find Classroom", "This feature is under construction.");
    }

    // Handle "Add" button in Classrooms section
    @FXML
    private void handleAddClassroom() {
        showAlert("Add Classroom", "This feature is under construction.");
    }

    // Handle "Find" button in Lectures section
    @FXML
    private void handleFindLecture() {
        showAlert("Find Lecture", "This feature is under construction.");
    }

    // Handle "Add" button in Lectures section
    @FXML
    private void handleAddLecture() {
        showAlert("Add Lecture", "This feature is under construction.");
    }

    // Handle "About" menu item
    @FXML
    private void handleAbout() {
        showAlert("About", "This is a simple scheduling application.");
    }

    // Utility method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
