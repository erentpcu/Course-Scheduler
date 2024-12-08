import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller {

    // Students Section
    @FXML
    private TextField studentsSearchBar;
    @FXML
    private TextArea studentsTextArea;
    @FXML
    private Button studentsAddButton;

    // Classrooms Section
    @FXML
    private TextField classroomsSearchBar;
    @FXML
    private TextArea classroomsTextArea;
    @FXML
    private Button classroomsAddButton;

    // Lectures Section
    @FXML
    private TextField lecturesSearchBar;
    @FXML
    private TextArea lecturesTextArea;
    @FXML
    private Button lecturesAddButton;

    @FXML
    public void initialize() {
        // Search functionality for Students
        studentsSearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Searching students: " + newValue);
            // Add student search logic here
        });

        // Search functionality for Classrooms
        classroomsSearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Searching classrooms: " + newValue);
            // Add classroom search logic here
        });

        // Search functionality for Lectures
        lecturesSearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Searching lectures: " + newValue);
            // Add lecture search logic here
        });

        // Add functionality for Students
        studentsAddButton.setOnAction(event -> {
            System.out.println("Add button clicked for Students");
            // Implement the logic for adding a student here
        });

        // Add functionality for Classrooms
        classroomsAddButton.setOnAction(event -> {
            System.out.println("Add button clicked for Classrooms");
            // Implement the logic for adding a classroom here
        });

        // Add functionality for Lectures
        lecturesAddButton.setOnAction(event -> {
            System.out.println("Add button clicked for Lectures");
            // Implement the logic for adding a lecture here
        });
    }
}
