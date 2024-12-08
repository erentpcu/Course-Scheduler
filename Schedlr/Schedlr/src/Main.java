import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application{
    public static void main(String[] args) {

        launch(args);
        // Sample Data
        List<Student> students = new ArrayList<>();
        List<Classroom> classrooms = new ArrayList<>();
        List<Lecture> lectures = new ArrayList<>();

        // Create Classrooms
        classrooms.add(new Classroom("M-101", 30));
        classrooms.add(new Classroom("C-303", 50));
        classrooms.add(new Classroom("D-208", 100));

        // Create TimeSlots
        TimeSlot slot1 = new TimeSlot("Monday", "09:00", "10:00");
        TimeSlot slot2 = new TimeSlot("Monday", "10:00", "11:00");
        TimeSlot slot3 = new TimeSlot("Monday", "11:00", "12:00");

        // Create Lectures
        lectures.add(new Lecture(1, "Math 101", slot1, 25));
        lectures.add(new Lecture(2, "Physics 101", slot2, 35));
        lectures.add(new Lecture(3, "Chemistry 101", slot3, 20));
        lectures.add(new Lecture(4, "Biology 101", slot3, 45));
        lectures.add(new Lecture(5, "English 101", slot2, 20));

        // Organize and Assign Lectures
        Scheduler scheduler = new Scheduler();
        scheduler.organizeLectures(lectures, classrooms);

        // Print Classroom Assignments
        for (Classroom classroom : classrooms) {
            System.out.println("\nClassroom " + classroom.getId() + " Lectures:");
            for (Lecture lecture : classroom.getLectures()) {
                System.out.println(" - Lecture " + lecture.getId() + ": " + lecture.getName() +
                        " at " + lecture.getTimeSlot());
            }
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Scheduler");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
        Scene scene = new Scene(loader.load());

        stage.setScene(scene);
        stage.show();
    }
}
