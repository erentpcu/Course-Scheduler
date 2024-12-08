import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class Controller {
    @FXML private ListView<String> studentsListView;
    @FXML private ListView<String> classroomsListView;
    @FXML private ListView<String> lecturesListView;

    // Sample data (Replace with actual data from your system)
    private final String[] students = {"John Doe", "Jane Smith", "Alex Brown"};
    private final String[] classrooms = {"Room 101", "Room 102", "Room 103"};
    private final String[] lectures = {"Math 101", "Physics 102", "Chemistry 103"};

    @FXML
    public void initialize() {
        // Initialize the ListViews with sample data
        studentsListView.getItems().addAll(students);
        classroomsListView.getItems().addAll(classrooms);
        lecturesListView.getItems().addAll(lectures);

        // Add event listeners for ListView items
        studentsListView.setOnMouseClicked(event -> openStudentDetailsWindow(studentsListView.getSelectionModel().getSelectedItem()));
        classroomsListView.setOnMouseClicked(event -> openClassroomDetailsWindow(classroomsListView.getSelectionModel().getSelectedItem()));
        lecturesListView.setOnMouseClicked(event -> openLectureDetailsWindow(lecturesListView.getSelectionModel().getSelectedItem()));
    }

    // Method to handle opening student details in a pop-up window
    private void openStudentDetailsWindow(String student) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentPopUpPage.fxml"));
            Stage studentDetailsStage = new Stage();

            // Load the AnchorPane, not VBox
            AnchorPane layout = loader.load();

            // Pass the student data to the controller
            StudentPopUpController controller = loader.getController();
            controller.setStudentDetails(student, "12345");  // Example student ID

            Scene scene = new Scene(layout, 400, 400);
            studentDetailsStage.setScene(scene);
            studentDetailsStage.setTitle("Student Details");
            studentDetailsStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to handle opening classroom details in a pop-up window
    private void openClassroomDetailsWindow(String classroom) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("classroomPopUpPage.fxml"));
            Stage classroomDetailsStage = new Stage();
            AnchorPane layout = (AnchorPane) loader.load();

            ClassroomPopUpController controller = loader.getController();
            controller.initialize(classroom);  // Pass the selected classroom to the pop-up

            Scene scene = new Scene(layout, 400, 400);
            classroomDetailsStage.setScene(scene);
            classroomDetailsStage.setTitle("Classroom Details");
            classroomDetailsStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to handle opening lecture details in a pop-up window
    private void openLectureDetailsWindow(String lecture) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("lecturePopUpPage.fxml"));
            Stage lectureDetailsStage = new Stage();
            AnchorPane layout = (AnchorPane) loader.load();

            LecturePopUpController controller = loader.getController();
            controller.initialize(lecture);  // Pass the selected lecture to the pop-up

            Scene scene = new Scene(layout, 400, 400);
            lectureDetailsStage.setScene(scene);
            lectureDetailsStage.setTitle("Lecture Details");
            lectureDetailsStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
