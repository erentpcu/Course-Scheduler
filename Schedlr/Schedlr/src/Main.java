import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {

        //testDatabaseConfiguration();
        DatabaseInitializer.initializeDatabase();
        ClassroomAllocator.allocateClassrooms();

        //initializeSampleData();
        launch(args);
        // Launch the JavaFX application

    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Scheduler");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainPage.fxml"));
        Scene scene = new Scene(loader.load());

        stage.setScene(scene);
        stage.show();
    }

    /*private static void initializeSampleData() {
        try {
            // Add Classrooms
            new Classroom("M-101", 30).saveToDatabase();
            new Classroom("C-303", 50).saveToDatabase();
            new Classroom("D-208", 100).saveToDatabase();

            // Add TimeSlots
            TimeSlot slot1 = new TimeSlot(1, "Monday", "09:00", "10:00");
            TimeSlot slot2 = new TimeSlot(2, "Monday", "10:00", "11:00");
            TimeSlot slot3 = new TimeSlot(3, "Monday", "11:00", "12:00");
            slot1.saveToDatabase();
            slot2.saveToDatabase();
            slot3.saveToDatabase();

            // Add Lectures
            new Lecture("1", "Math 101", slot1, 25).saveToDatabase();
            new Lecture("2", "Physics 101", slot2, 35).saveToDatabase();
            new Lecture("3", "Chemistry 101", slot3, 20).saveToDatabase();
            new Lecture("4", "Biology 101", slot3, 45).saveToDatabase();
            new Lecture("5", "English 101", slot2, 20).saveToDatabase();

            // Add Students
            new Student(1, "John Doe").saveToDatabase();
            new Student(2, "Jane Smith").saveToDatabase();
            new Student(3, "Robert Brown").saveToDatabase();
        } catch (Exception e) {
            System.out.println("Error initializing sample data: " + e.getMessage());
        }
    }*/
}
