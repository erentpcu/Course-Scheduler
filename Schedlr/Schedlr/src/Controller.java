import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Controller {
    @FXML private ListView<String> studentsListView;
    @FXML private ListView<String> classroomsListView;
    @FXML private ListView<String> lecturesListView;
    @FXML private TextField lecturesSearchBar;
    @FXML private TextField studentsSearchBar; // Added for student search
    @FXML private TextField classroomsSearchBar; // Added for classroom search

    private List<Lecture> lectureList; // Added lectureList to store Lecture objects.
    private List<Student> studentList; // Added studentList for search functionality
    private List<Classroom> classroomList; // Added classroomList for search functionality


    // Sample data for students and classrooms
    private final String[] students = {"John Doe", "Jane Smith", "Alex Brown"};
    private final String[] classrooms = {"Room 101", "Room 102", "Room 103"};

    @FXML
    public void initialize() {
        // Initialize the lectureList with sample data.
        lectureList = new ArrayList<>();
        lectureList.add(new Lecture(1, "Math 101", new TimeSlot("Monday"," 10 AM", "12 PM"), 30));
        lectureList.add(new Lecture(2, "Physics 102", new TimeSlot("Tuesday", "1 PM","3 PM"), 25));
        lectureList.add(new Lecture(3, "Chemistry 103", new TimeSlot("Wednesday","9 AM","11 AM"), 20));

        // Initialize studentList and classroomList with sample data.
        studentList = new ArrayList<>();
        studentList.add(new Student(1,"John Doe"));
        studentList.add(new Student(2,"Jane Smith"));
        studentList.add(new Student(3,"Alex Brown"));

        classroomList = new ArrayList<>();
        classroomList.add(new Classroom("Room 101",20));
        classroomList.add(new Classroom("Room 102",50));
        classroomList.add(new Classroom("Room 103",35));

        refreshLectureListView();
        refreshStudentListView();
        refreshClassroomListView();


        // Populate the ListViews with sample data for students and classrooms.
        studentsListView.getItems().addAll(students);
        classroomsListView.getItems().addAll(classrooms);

        // Add listener to the lecturesSearchBar for filtering lectures,classrooms and students.
        lecturesSearchBar.textProperty().addListener((observable, oldValue, newValue) -> searchLecture(newValue));
        studentsSearchBar.textProperty().addListener((observable, oldValue, newValue) -> searchStudent(newValue));
        classroomsSearchBar.textProperty().addListener((observable, oldValue, newValue) -> searchClassroom(newValue));



        // Add listener to the lecturesSearchBar for filtering
        lecturesSearchBar.textProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                searchLecture(newValue);
            }
        });

        // Add listener to the lecturesSearchBar for filtering
        studentsSearchBar.textProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                searchStudent(newValue);
            }
        });

        // Add listener to the lecturesSearchBar for filtering
        classroomsSearchBar.textProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                searchClassroom(newValue);
            }
        });


        // Populate the ListViews with sample data for students, classrooms, and lectures.
        studentsListView.getItems().addAll(students);
        classroomsListView.getItems().addAll(classrooms);


        for (Lecture lecture : lectureList) {
            lecturesListView.getItems().add(lecture.getName());
        }

        for (Student student : studentList) {
            studentsListView.getItems().add(student.getName());
        }

        for (Classroom classroom : classroomList) {
            classroomsListView.getItems().add(classroom.getId());
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

    // Method to refresh the lecturesListView based on the current lectureList
    private void refreshLectureListView() {
        lecturesListView.getItems().clear();
        for (Lecture lecture : lectureList) {
            lecturesListView.getItems().add(lecture.getName());
        }
    }

    // Method to refresh the studentListView based on the current studentList
    private void refreshStudentListView() {
        studentsListView.getItems().clear();
        for (Student student : studentList) {
            lecturesListView.getItems().add(student.getName());
        }
    }

    // Method to refresh the classroomListView based on the current classrommList
    private void refreshClassroomListView() {
        classroomsListView.getItems().clear();
        for (Classroom classroom : classroomList) {
            classroomsListView.getItems().add(classroom.getId());
        }
    }

    // Method to search for lectures by name and update the list view
    private void searchLecture(String query) {
        // Filter lectures based on the query (case insensitive)
        List<String> filteredLectures = lectureList.stream()
                .map(Lecture::getName)
                .filter(name -> name.toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        // Update the lecturesListView with the filtered list
        lecturesListView.getItems().setAll(filteredLectures);
    }

    // Method to search for students and update the ListView
    private void searchStudent(String query) {
        List<Student> filteredStudents = studentList.stream()
                .filter(student -> student.getName().toLowerCase().contains(query.toLowerCase()))
                .toList();

        // Update the ListView with filtered student names
        studentsListView.getItems().setAll(filteredStudents.stream()
                .map(Student::getName)
                .collect(Collectors.toList()));
    }

    // Method to search for classrooms and update the ListView
    private void searchClassroom(String query) {
        List<Classroom> filteredClassroom = classroomList.stream()
                .filter(classroom -> classroom.getId().toLowerCase().contains(query.toLowerCase()))
                .toList();

        // Update the ListView with filtered student names
        classroomsListView.getItems().setAll(filteredClassroom.stream()
                .map(Classroom::getId)
                .collect(Collectors.toList()));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentPopUpPage.fxml")); // Dosya yolunu kontrol edin
            GridPane layout = loader.load();

            StudentPopUpController controller = loader.getController();
            controller.setStudentDetails(student); // Set the student name

            Stage stage = createPopUpStage(student + "in ders programı", layout);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to handle opening classroom details in a pop-up window.
    private void openClassroomDetailsWindow(String classroom) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/classroomPopUpPage.fxml")); // Dosya yolunu kontrol edin
            GridPane layout = loader.load();

            ClassroomPopUpController controller = loader.getController();

            // Assuming you have a method to get the schedule for a classroom
            Map<String, Map<String, String>> classroomSchedule = getScheduleForClassroom(classroom);
            controller.initialize(classroom, classroomSchedule); // Pass the selected classroom and schedule to the pop-up.

            Stage stage = createPopUpStage("Classroom Details", layout);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Example method to get the schedule for a classroom
    private Map<String, Map<String, String>> getScheduleForClassroom(String classroom) {
        // This should return a map of day to time slots and lectures for the given classroom
        // For now, returning a sample map
        return Map.of(
                "Monday", Map.of("08:30 - 09:25", "Math 101"),
                "Tuesday", Map.of("09:25 - 10:20", "Physics 102"),
                "Wednesday", Map.of("10:20 - 11:15", "Chemistry 103")
        );
    }

    // Method to handle opening lecture details in a pop-up window.
    private void openLectureDetailsWindow(String lecture) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/lecturePopUpPage.fxml")); // Dosya yolunu kontrol edin
            GridPane layout = loader.load();

            LecturePopUpController controller = loader.getController();

            // Assuming you have a method to get registered students for a lecture
            List<String> registeredStudents = getRegisteredStudentsForLecture(lecture);
            controller.initialize(lecture, registeredStudents); // Pass the selected lecture and students to the pop-up.

            Stage stage = createPopUpStage("Lecture Details", layout);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Example method to get registered students for a lecture
    private List<String> getRegisteredStudentsForLecture(String lecture) {
        // This should return a list of student names registered for the given lecture
        // For now, returning a sample list
        return List.of("John Doe", "Jane Smith", "Alex Brown");
    }

    // Utility method to create and configure a new Stage for pop-ups.
    private Stage createPopUpStage(String title, GridPane layout) {
        Stage stage = new Stage();
        stage.setScene(new Scene(layout, 400, 400));
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }
}