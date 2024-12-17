import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
        studentsListView.setCellFactory(param -> createListCell("Student"));
        classroomsListView.setCellFactory(param -> createListCell("Classroom"));
        lecturesListView.setCellFactory(param -> createListCell("Lecture"));
    }

    // Factory method to create list cells with remove buttons
    private ListCell<String> createListCell(String type) {
        return new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create the label for the item text (default styling)
                    Label itemLabel = new Label(item);

                    // Create a flexible spacer
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // Create the remove button
                    Button removeButton = new Button("X");
                    removeButton.setStyle("-fx-background-color: darkred; -fx-text-fill: white; -fx-font-weight: bold;");
                    removeButton.setOnAction(event -> getListView().getItems().remove(item));

                    // Layout: HBox with label, spacer, and button
                    HBox hBox = new HBox(10, itemLabel, spacer, removeButton);
                    hBox.setStyle("-fx-alignment: center-left; -fx-padding: 5;");

                    setGraphic(hBox);

                    // Double-click to open pop-up
                    setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2) {
                            if (type.equals("Student")) {
                                openStudentDetailsWindow(item);
                            } else if (type.equals("Classroom")) {
                                openClassroomDetailsWindow(item);
                            } else if (type.equals("Lecture")) {
                                openLectureDetailsWindow(item);
                            }
                        }
                    });
                }
            }
        };
    }


    // Method to handle opening student details in a pop-up window
    private void openStudentDetailsWindow(String student) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentPopUpPage.fxml"));
            AnchorPane layout = loader.load();

            StudentPopUpController controller = loader.getController();
            controller.setStudentDetails(student, "12345"); // Example student ID

            Stage stage = createPopUpStage("Student Details", layout);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to handle opening classroom details in a pop-up window
    private void openClassroomDetailsWindow(String classroom) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("classroomPopUpPage.fxml"));
            AnchorPane layout = loader.load();

            ClassroomPopUpController controller = loader.getController();
            controller.initialize(classroom); // Pass the selected classroom to the pop-up

            Stage stage = createPopUpStage("Classroom Details", layout);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to handle opening lecture details in a pop-up window
    private void openLectureDetailsWindow(String lecture) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("lecturePopUpPage.fxml"));
            AnchorPane layout = loader.load();

            LecturePopUpController controller = loader.getController();
            controller.initialize(lecture); // Pass the selected lecture to the pop-up

            Stage stage = createPopUpStage("Lecture Details", layout);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Utility method to create and configure a new Stage for pop-ups
    private Stage createPopUpStage(String title, AnchorPane layout) {
        Stage stage = new Stage();
        stage.setScene(new Scene(layout, 400, 400));
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }
}
