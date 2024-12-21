import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Controller {
    @FXML private ListView<String> studentsListView;
    @FXML private ListView<String> classroomsListView;
    @FXML private ListView<String> lecturesListView;
    @FXML private TextField lecturesSearchBar;
    @FXML private TextField studentsSearchBar;
    @FXML private TextField classroomsSearchBar;


    @FXML
    public void initialize() {

        // Initialize the list views
        refreshStudentsListView();
        refreshClassroomsListView();
        refreshLecturesListView();

        // Add listeners for search bars
        lecturesSearchBar.textProperty().addListener((observable, oldValue, newValue) -> searchLecture(newValue));
        studentsSearchBar.textProperty().addListener((observable, oldValue, newValue) -> searchStudent(newValue));
        classroomsSearchBar.textProperty().addListener((observable, oldValue, newValue) -> searchClassroom(newValue));

        // Add cell factories to handle interactions
        studentsListView.setCellFactory(param -> createListCell("Student"));
        classroomsListView.setCellFactory(param -> createListCell("Classroom"));
        lecturesListView.setCellFactory(param -> createListCell("Lecture"));
    }


    // Refresh the students list view by fetching data from the database
    private void refreshStudentsListView() {
        studentsListView.getItems().clear();
        String sql = "SELECT name FROM students";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                studentsListView.getItems().add(name); // Names are already formatted
            }

        } catch (Exception e) {
            System.out.println("Error refreshing students list: " + e.getMessage());
        }
    }

    // Refresh the classrooms list view by fetching data from the database
    private void refreshClassroomsListView() {
        classroomsListView.getItems().clear();
        String sql = "SELECT id FROM classrooms";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                classroomsListView.getItems().add(rs.getString("id"));
            }

        } catch (SQLException e) {
            System.out.println("Error refreshing classrooms list: " + e.getMessage());
        }
    }

    // Refresh the lectures list view by fetching data from the database
    private void refreshLecturesListView() {
        lecturesListView.getItems().clear();
        String sql = "SELECT name FROM lectures";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                lecturesListView.getItems().add(rs.getString("name"));
            }

        } catch (SQLException e) {
            System.out.println("Error refreshing lectures list: " + e.getMessage());
        }
    }

    private String getStudentIdByName(String studentName) {
        String sql = "SELECT id FROM students WHERE name = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("id");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching student ID: " + e.getMessage());
        }
        return null; // Return null if student not found
    }


    // Search lectures and update the list view
    private void searchLecture(String query) {
        lecturesListView.getItems().clear();
        String sql = "SELECT name FROM lectures WHERE name LIKE ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + query + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lecturesListView.getItems().add(rs.getString("name"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error searching lectures: " + e.getMessage());
        }
    }

    // Search students and update the list view
    private void searchStudent(String query) {
        studentsListView.getItems().clear();
        String sql = "SELECT name FROM students WHERE name LIKE ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + query + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    studentsListView.getItems().add(rs.getString("name"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error searching students: " + e.getMessage());
        }
    }

    // Search classrooms and update the list view
    private void searchClassroom(String query) {
        classroomsListView.getItems().clear();
        String sql = "SELECT id FROM classrooms WHERE id LIKE ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + query + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    classroomsListView.getItems().add(rs.getString("id"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error searching classrooms: " + e.getMessage());
        }
    }

    // Handle adding a new student
    @FXML
    private void handleAddStudentButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddStudent.fxml"));
            AnchorPane layout = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(layout));
            stage.setTitle("Add New Student");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            refreshStudentsListView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Handle adding a new lecture
    @FXML
    private void handleAddLectureButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addLecturePopUpPage.fxml"));
            VBox layout = loader.load(); // AnchorPane yerine VBox kullan

            Stage stage = new Stage();
            stage.setScene(new Scene(layout));
            stage.setTitle("Add New Lecture");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            refreshLecturesListView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Handle opening student details in a pop-up
    private void openStudentDetailsWindow(String studentName) {
        try {
            System.out.println("Opening details for studentName: " + studentName); // Debugging line

            // Fetch the studentId based on the studentName
            String studentId = getStudentIdByName(studentName);
            if (studentId == null) {
                System.out.println("Student not found for name: " + studentName);
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("StudentPopUpPage.fxml"));
            GridPane layout = loader.load();

            StudentPopUpController controller = loader.getController();
            controller.setStudentDetails(studentId);

            Stage stage = new Stage();
            stage.setScene(new Scene(layout));
            stage.setTitle("Student Details");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // Handle opening lecture details in a pop-up
    private void openLectureDetailsWindow(String lectureName) {
        try {
            // Fetch the lecture ID based on the lecture name
            String lectureId = fetchLectureIdByName(lectureName);

            if (lectureId == null) {
                System.out.println("Lecture not found for name: " + lectureName);
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("LecturePopUpPage.fxml"));
            GridPane layout = loader.load();

            LecturePopUpController controller = loader.getController();
            controller.initialize(lectureId);

            Stage stage = new Stage();
            stage.setScene(new Scene(layout));
            stage.setTitle("Lecture Details");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to fetch the lecture ID by name
    private String fetchLectureIdByName(String lectureName) {
        String sql = "SELECT id FROM lectures WHERE name = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lectureName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("id");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching lecture ID: " + e.getMessage());
        }
        return null;
    }



    // Handle opening classroom details in a pop-up
    private void openClassroomDetailsWindow(String classroomId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ClassroomPopUpPage.fxml"));
            GridPane layout = loader.load();

            ClassroomPopUpController controller = loader.getController();
            controller.initialize(classroomId);

            Stage stage = new Stage();
            stage.setScene(new Scene(layout));
            stage.setTitle("Classroom Details");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCreateMeetingMenuAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("createMeetingPopUp.fxml"));
            VBox layout = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(layout));
            stage.setTitle("Create a Meeting");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            System.out.println("Error loading Create Meeting pop-up: " + e.getMessage());
        }
    }


    private ListCell<String> createListCell(String type) {
        return new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Label for the item text
                    Label itemLabel = new Label(item);

                    // Spacer for flexible layout
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // Remove button
                    Button removeButton = new Button("X");
                    removeButton.setStyle("-fx-background-color: darkred; -fx-text-fill: white;");
                    removeButton.setOnAction(event -> {
                        getListView().getItems().remove(item); // Remove from the ListView
                        if ("Lecture".equals(type)) {
                            // Additional logic for lectures
                            //removeLectureByName(item);
                        }
                    });

                    // HBox for layout
                    HBox cellLayout = new HBox(10, itemLabel, spacer, removeButton);
                    cellLayout.setStyle("-fx-alignment: center-left;");

                    setGraphic(cellLayout);

                    // Double-click handler
                    setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2) {
                            switch (type) {
                                case "Student" -> openStudentDetailsWindow(item);
                                case "Classroom" -> openClassroomDetailsWindow(item);
                                case "Lecture" -> openLectureDetailsWindow(item);
                            }
                        }
                    });
                }
            }
        };
    }

}
