import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StudentPopUpController {

    @FXML private Label studentNameLabel;

    // Method to set the student details (This will be called from the main controller)
    public void setStudentDetails(String studentName) {
        studentNameLabel.setText(studentName + "in ders programı");
    }
}