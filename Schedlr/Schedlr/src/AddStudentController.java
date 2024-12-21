import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.Arrays;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.stream.Collectors;

public class AddStudentController {
    @FXML private TextField studentNameField;
    @FXML private TextField studentSurnameField;
    @FXML private TextField studentIdField;
    @FXML private Button addButton;
    @FXML private Button cancelButton;

    public static String capitalizeName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return Arrays.stream(name.trim().toLowerCase().split("\\s+"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
    }
    @FXML
    private void AddStudentButtonAction() {
        try {
            String firstName = studentNameField.getText(); // First Name Field
            String lastName = studentSurnameField.getText(); // Surname Field

            // Capitalize first and last names separately
            String capitalizedFirstName = capitalizeName(firstName);
            String capitalizedLastName = capitalizeName(lastName);

            // Combine the names
            String fullName = capitalizedFirstName + " " + capitalizedLastName;

            int id = Integer.parseInt(studentIdField.getText());

            // Save the new student to the database
            saveStudentToDatabase(id, fullName);

            // Close the pop-up
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveStudentToDatabase(int id, String name) {
        String sql = "INSERT INTO students (id, name) VALUES (?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name); // Save the formatted name
            pstmt.executeUpdate();
            System.out.println("Student added to the database: " + name);
        } catch (Exception e) {
            System.out.println("Error saving student to database: " + e.getMessage());
        }
    }

    @FXML
    private void CancelButtonAction() {
        // Close the pop-up without doing anything
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
