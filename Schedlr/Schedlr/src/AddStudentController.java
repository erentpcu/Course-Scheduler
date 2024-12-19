import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class AddStudentController {
    @FXML private TextField studentNameField;
    @FXML private TextField studentSurnameField;
    @FXML private TextField studentIdField;
    @FXML private Button addButton;
    @FXML private Button cancelButton;

    private List<Student> studentList;

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    @FXML
    private void AddStudentButtonAction() {
        try {
            String name = studentNameField.getText();
            String surname = studentSurnameField.getText();
            int id = Integer.parseInt(studentIdField.getText());

            // Add the new student to the list
           // Student.addStudent(studentList, name, surname, id);

            // Close the pop-up
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void CancelButtonAction() {
        // Close the pop-up without doing anything
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}