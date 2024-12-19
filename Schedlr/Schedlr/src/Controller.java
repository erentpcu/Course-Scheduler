import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    @FXML private ListView<String> studentsListView;
    @FXML private ListView<String> classroomsListView;
    @FXML private ListView<String> lecturesListView;

    private List<Lecture> lectureList;
    private List<Student> studentList; // Student list declaration

    // Sample data for students and classrooms
    private final String[] students = {"John Doe", "Jane Smith", "Alex Brown"};
    private final String[] classrooms = {"Room 101", "Room 102", "Room 103"};

    @FXML
    public void initialize() {
        // Initialize the studentList
        studentList = new ArrayList<>(); // Initialize studentList

        // Initialize the lectureList with sample data.
        lectureList = new ArrayList<>();
        lectureList.add(new Lecture(1, "Math 101", new TimeSlot("Monday"," 10 AM", "12 PM"), 30));
        lectureList.add(new Lecture(2, "Physics 102", new TimeSlot("Tuesday", "1 PM","3 PM"), 25));
        lectureList.add(new Lecture(3, "Chemistry 103", new TimeSlot("Wednesday","9 AM","11 AM"), 20));

        // Populate the ListViews with sample data for students, classrooms, and lectures.
        studentsListView.getItems().addAll(students);
        classroomsListView.getItems().addAll(classrooms);

        for (Lecture lecture : lectureList) {
            lecturesListView.getItems().add(lecture.getName());
        }

        // Add event listeners for ListView items.
        studentsListView.setCellFactory(param -> createListCell("Student"));
        classroomsListView.setCellFactory(param -> createListCell("Classroom"));
        lecturesListView.setCellFactory(param -> createListCell("Lecture"));
    }

    // Factory method to create list cells with remove buttons.
    private ListCell<String> createListCell(String type) {
        return new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create the label for the item text (default styling).
                    Label itemLabel = new Label(item);

                    // Create a flexible spacer.
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // Create the remove button.
                    Button removeButton = new Button("X");
                    removeButton.setStyle("-fx-background-color: darkred; -fx-text-fill: white; -fx-font-weight: bold;");
                    removeButton.setOnAction(event -> {
                        getListView().getItems().remove(item); // Remove from the ListView.
                        if (type.equals("Lecture")) {
                            removeLectureByName(item); // Call method to remove from lectureList.
                        }
                    });

                    // Layout: HBox with label, spacer, and button.
                    HBox hBox = new HBox(10, itemLabel, spacer, removeButton);
                    hBox.setStyle("-fx-alignment: center-left; -fx-padding: 5;");

                    setGraphic(hBox);

                    // Double-click to open pop-up.
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

    // Removes a lecture from lectureList by its name.
    private void removeLectureByName(String lectureName) {
        Lecture lectureToRemove = null;
        for (Lecture lecture : lectureList) {
            if (lecture.getName().equals(lectureName)) {
                lectureToRemove = lecture;
                break;
            }
        }

        if (lectureToRemove != null) {
            lectureList.remove(lectureToRemove); // Remove it from the list
            lectureToRemove.removeLecture(lectureList); // Pass the entire list to remove the lecture
        }
    }

    @FXML
    private TextField lectureSearchField; // Assuming you have a text field for searching lectures
    @FXML
    private void handleAddStudentButtonAction() {
        try {
            // Load the FXML for the addLecture pop-up
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddStudent.fxml"));
            AnchorPane layout = loader.load();

            AddStudentController addStudentController = loader.getController();
            addStudentController.setStudentList(studentList); // Pass the studentlist to the controller

            // Create and configure the pop-up stage
            Stage stage = new Stage();
            stage.setScene(new Scene(layout));
            stage.setTitle("Add New Student");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh the studentsListView after adding a student
            studentsListView.getItems().clear();
            for (Student student : studentList) {
                studentsListView.getItems().add(student.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddLectureButtonAction() {
        try {
            // Load the FXML for the addLecture pop-up
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addLecturePopUpPage.fxml"));
            AnchorPane layout = loader.load();

            AddLectureController addLectureController = loader.getController();
            addLectureController.setLectureList(lectureList); // Pass the lectureList to the controller

            // Create and configure the pop-up stage
            Stage stage = new Stage();
            stage.setScene(new Scene(layout));
            stage.setTitle("Add New Lecture");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh the lectureListView after adding a lecture
            lecturesListView.getItems().clear();
            for (Lecture lecture : lectureList) {
                lecturesListView.getItems().add(lecture.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to handle opening student details in a pop-up window.
    private void openStudentDetailsWindow(String student) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentPopUpPage.fxml"));
            AnchorPane layout = loader.load();

            StudentPopUpController controller = loader.getController();
            controller.setStudentDetails(student, "12345"); // Example student ID.

            Stage stage = createPopUpStage("Student Details", layout);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to handle opening classroom details in a pop-up window.
    private void openClassroomDetailsWindow(String classroom) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("classroomPopUpPage.fxml"));
            AnchorPane layout = loader.load();

            ClassroomPopUpController controller = loader.getController();
            controller.initialize(classroom); // Pass the selected classroom to the pop-up.

            Stage stage = createPopUpStage("Classroom Details", layout);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to handle opening lecture details in a pop-up window.
    private void openLectureDetailsWindow(String lecture) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("lecturePopUpPage.fxml"));
            AnchorPane layout = loader.load();

            LecturePopUpController controller = loader.getController();
            controller.initialize(lecture); // Pass the selected lecture to the pop-up.

            Stage stage = createPopUpStage("Lecture Details", layout);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Utility method to create and configure a new Stage for pop-ups.
    private Stage createPopUpStage(String title, AnchorPane layout) {
        Stage stage = new Stage();
        stage.setScene(new Scene(layout, 400, 400));
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }
}