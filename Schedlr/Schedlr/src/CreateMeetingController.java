import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CreateMeetingController {
    @FXML private ComboBox<String> dayComboBox;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private ListView<String> availableClassroomsListView;
    @FXML private ListView<String> availableStudentsListView;
    @FXML private ListView<String> meetingDetailsListView;

    private ObservableList<String> availableClassrooms = FXCollections.observableArrayList();
    private ObservableList<String> availableStudents = FXCollections.observableArrayList();
    private ObservableList<String> meetingDetails = FXCollections.observableArrayList();

    // Sample schedules for classrooms and students
    private Map<String, Map<String, String>> classroomSchedules = new HashMap<>();
    private Map<String, Map<String, String>> studentSchedules = new HashMap<>();

    @FXML
    public void initialize() {
        // Başlangıçta tüm sınıf ve öğrenci listesini göster
        availableClassrooms.setAll("Room 101", "Room 102", "Room 103");
        availableStudents.setAll("John Doe", "Jane Smith", "Alex Brown");

        availableClassroomsListView.setItems(availableClassrooms);
        availableStudentsListView.setItems(availableStudents);
        meetingDetailsListView.setItems(meetingDetails);

        // Filtreleme işlemi
        dayComboBox.setOnAction(event -> filterLists());
        timeComboBox.setOnAction(event -> filterLists());

        // Çift tıklama ile meeting detaylarına ekleme
        availableClassroomsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selected = availableClassroomsListView.getSelectionModel().getSelectedItem();
                if (selected != null && !meetingDetails.contains("Classroom: " + selected)) {
                    meetingDetails.add("Classroom: " + selected);
                }
            }
        });

        availableStudentsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selected = availableStudentsListView.getSelectionModel().getSelectedItem();
                if (selected != null && !meetingDetails.contains("Student: " + selected)) {
                    meetingDetails.add("Student: " + selected);
                }
            }
        });
    }

    private void filterLists() {
        String day = dayComboBox.getValue();
        String time = timeComboBox.getValue();

        if (day != null && time != null) {
            // Uygun sınıf ve öğrencileri filtrele
            availableClassrooms.setAll(getAvailableClassrooms(day, time));
            availableStudents.setAll(getAvailableStudents(day, time));
        }
    }

    private List<String> getAvailableClassrooms(String day, String time) {
        // Bu metot, uygun sınıfları döndürmelidir
        // Örnek olarak tüm sınıfları döndürüyoruz
        return List.of("Room 101", "Room 102", "Room 103");
    }

    private List<String> getAvailableStudents(String day, String time) {
        // Bu metot, uygun öğrencileri döndürmelidir
        // Örnek olarak tüm öğrencileri döndürüyoruz
        return List.of("John Doe", "Jane Smith", "Alex Brown");
    }

    @FXML
    private void handleCreateMeeting() {
        String day = dayComboBox.getValue();
        String time = timeComboBox.getValue();

        if (day != null && time != null) {
            for (String detail : meetingDetails) {
                if (detail.startsWith("Classroom: ")) {
                    String classroom = detail.substring("Classroom: ".length());
                    updateClassroomSchedule(classroom, day, time);
                } else if (detail.startsWith("Student: ")) {
                    String student = detail.substring("Student: ".length());
                    updateStudentSchedule(student, day, time);
                }
            }
            System.out.println("Meeting created with details: " + meetingDetails);
        }
    }

    private void updateClassroomSchedule(String classroom, String day, String time) {
        classroomSchedules.computeIfAbsent(classroom, k -> new HashMap<>()).put(time, "Meeting");
        System.out.println("Updated " + classroom + " schedule: " + classroomSchedules.get(classroom));
    }

    private void updateStudentSchedule(String student, String day, String time) {
        studentSchedules.computeIfAbsent(student, k -> new HashMap<>()).put(time, "Meeting - " + getSelectedClassroom());
        System.out.println("Updated " + student + " schedule: " + studentSchedules.get(student));
    }

    private String getSelectedClassroom() {
        for (String detail : meetingDetails) {
            if (detail.startsWith("Classroom: ")) {
                return detail.substring("Classroom: ".length());
            }
        }
        return "Unknown";
    }
}