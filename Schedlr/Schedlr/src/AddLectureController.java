import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AddLectureController {
    @FXML
    private TextField lectureNameField;
    @FXML private ComboBox<String> dayComboBox;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private TextField lecturerField;
    @FXML private ListView<CheckBox> studentsListView;
    @FXML private ComboBox<String> classroomComboBox;
    @FXML private Spinner<Integer> durationSpinner;
    @FXML private Button addButton;

    @FXML
    public void initialize() {
        loadStudents();

        // Day ve Time ComboBox'ları değiştiğinde sınıfları güncelle
        dayComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && timeComboBox.getValue() != null) {
                updateAvailableClassrooms(newVal, timeComboBox.getValue());
            }
        });
        timeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dayComboBox.getValue() != null) {
                updateAvailableClassrooms(dayComboBox.getValue(), newVal);
            }
        });}
    private void updateAvailableClassrooms(String selectedDay, String selectedTime) {
        classroomComboBox.getItems().clear();

        String sql = """
       SELECT DISTINCT c.id 
       FROM classrooms c 
       WHERE NOT EXISTS (
           SELECT 1 
           FROM classroom_schedule cs 
           WHERE cs.classroom_id = c.id 
           AND cs.day = ? 
           AND cs.time_slot = ? 
           AND cs.available = false
       )
       ORDER BY c.id
   """;
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, selectedDay);
            pstmt.setString(2, selectedTime);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                classroomComboBox.getItems().add(rs.getString("id"));
            }

        } catch (SQLException e) {
            System.out.println("Error loading available classrooms: " + e.getMessage());
            e.printStackTrace();
        }}

    private void loadStudents() {
        ObservableList<CheckBox> studentCheckBoxes = FXCollections.observableArrayList();

        try (Connection conn = Database.connect()) {
            String sql = "SELECT name FROM students";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                CheckBox cb = new CheckBox(rs.getString("name"));
                studentCheckBoxes.add(cb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        studentsListView.setItems(studentCheckBoxes);
    }

    private void loadClassrooms() {
        try (Connection conn = Database.connect()) {
            String sql = "SELECT id FROM classrooms";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                classroomComboBox.getItems().add(rs.getString("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddButtonAction() {
        String name = lectureNameField.getText();
        String day = dayComboBox.getValue();
        String time = timeComboBox.getValue();
        String lecturer = lecturerField.getText();
        String classroom = classroomComboBox.getValue();
        int duration = durationSpinner.getValue();

        // Validation checks
        if (name.isEmpty() || day == null || time == null || lecturer.isEmpty()) {
            showAlert("Hata", "Lütfen tüm alanları doldurun!");
            return;
        }

        // Check for conflicts only if a classroom is selected
        if (classroom != null && checkConflicts(classroom, day, time, duration)) {
            showAlert("Hata", "Bu sınıf ve zaman diliminde çakışma var!");
            return;
        }

        // Get selected students
        List<String> selectedStudents = studentsListView.getItems().stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
        if (selectedStudents.isEmpty()) {
            showAlert("Uyarı", "Hiç öğrenci seçilmedi. Devam etmek istiyor musunuz?");
        }

        // Save to database
        saveLectureToDatabase(name, day, time, lecturer, classroom, duration, selectedStudents);

        // Close the window
        ((Stage) addButton.getScene().getWindow()).close();
    }
    private void saveLectureToDatabase(String name, String day, String time,
                                       String lecturer, String classroom,
                                       int duration, List<String> students) {
        try (Connection conn = Database.connect()) {
            conn.setAutoCommit(false); // Transaction başlat
            try {
                // 1. Önce time_slot'u kaydet
                String timeSlotSql = "INSERT INTO time_slots (day, start_time, end_time) VALUES (?, ?, ?)";
                int timeSlotId;
                try (PreparedStatement pstmt = conn.prepareStatement(timeSlotSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, day);
                    pstmt.setString(2, time);
                    // End time'ı duration'a göre hesapla
                    String endTime = calculateEndTime(time, duration);
                    pstmt.setString(3, endTime);
                    pstmt.executeUpdate();

                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (!rs.next()) throw new SQLException("TimeSlot ID alınamadı!");
                    timeSlotId = rs.getInt(1);
                }

                // 2. Lecture'ı kaydet
                String lectureId = generateLectureId(name);
                String lectureSql = "INSERT INTO lectures (id, name, lecturer, classroom_id, time_slot_id) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(lectureSql)) {
                    pstmt.setString(1, lectureId);
                    pstmt.setString(2, name);
                    pstmt.setString(3, lecturer);
                    pstmt.setString(4, classroom);
                    pstmt.setInt(5, timeSlotId);
                    pstmt.executeUpdate();
                }

                // 3. Öğrencileri kaydet
                String studentScheduleSql = "INSERT INTO student_schedule (student_id, lecture_id) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(studentScheduleSql)) {
                    for (String studentName : students) {
                        String studentId = getStudentIdByName(studentName);
                        if (studentId != null) {
                            pstmt.setString(1, studentId);
                            pstmt.setString(2, lectureId);
                            pstmt.executeUpdate();
                        }
                    }
                }

                // 4. Classroom schedule'ı güncelle
                String classroomScheduleSql = "INSERT INTO classroom_schedule (classroom_id, day, time_slot, lecture_id, available) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(classroomScheduleSql)) {
                    for (int i = 0; i < duration; i++) {
                        String currentTime = calculateTime(time, i);
                        pstmt.setString(1, classroom);
                        pstmt.setString(2, day);
                        pstmt.setString(3, currentTime);
                        pstmt.setString(4, lectureId);
                        pstmt.setBoolean(5, false);
                        pstmt.executeUpdate();
                    }
                }

                conn.commit(); // Transaction'ı onayla
                showAlert("Başarılı", "Ders başarıyla eklendi!");

            } catch (SQLException e) {
                conn.rollback(); // Hata durumunda geri al
                throw e;
            }
        } catch (SQLException e) {
            showAlert("Hata", "Ders eklenirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private String calculateEndTime(String startTime, int duration) {
        String[] timeParts = startTime.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        minute += duration * 55; // (duration - 1) yerine duration kullanıyoruz
        hour += minute / 60;
        minute = minute % 60;
        return String.format("%02d:%02d", hour, minute);}

        private String calculateTime(String startTime, int offset) {
            String[] timeParts = startTime.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            minute += offset * 55; // Her slot 55 dakika
            hour += minute / 60;
            minute = minute % 60;
            return String.format("%02d:%02d", hour, minute);

        }

    private String generateLectureId(String name) {
        return name.replaceAll("\\s+", "").toUpperCase() + System.currentTimeMillis() % 1000;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private boolean checkConflicts(String classroom, String day, String startTime, int duration) {
        try (Connection conn = Database.connect()) {
            // Sınıf çakışması kontrolü
            String classroomSql = """
           SELECT COUNT(*) as conflict_count 
           FROM classroom_schedule 
           WHERE classroom_id = ? AND day = ? AND time_slot = ? AND available = false
       """;
            try (PreparedStatement pstmt = conn.prepareStatement(classroomSql)) {
                for (int i = 0; i < duration; i++) {
                    String currentTime = calculateTime(startTime, i);
                    pstmt.setString(1, classroom);
                    pstmt.setString(2, day);
                    pstmt.setString(3, currentTime);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next() && rs.getInt("conflict_count") > 0) {
                        showAlert("Hata", "Bu sınıf ve zaman diliminde başka bir ders var!");
                        return true;
                    }
                }
            }
            // Öğrenci çakışması kontrolü
            String studentSql = """
           SELECT COUNT(*) as conflict_count
           FROM student_schedule ss
           JOIN lectures l ON ss.lecture_id = l.id
           JOIN time_slots t ON l.time_slot_id = t.id
           WHERE ss.student_id = ? 
           AND t.day = ?
           AND t.start_time <= ? 
           AND t.end_time >= ?
       """;
            try (PreparedStatement pstmt = conn.prepareStatement(studentSql)) {
                String endTime = calculateTime(startTime, duration - 1);

                // Seçili her öğrenci için çakışma kontrolü
                for (CheckBox cb : studentsListView.getItems()) {
                    if (cb.isSelected()) {
                        String studentId = getStudentIdByName(cb.getText());
                        if (studentId != null) {
                            pstmt.setString(1, studentId);
                            pstmt.setString(2, day);
                            pstmt.setString(3, endTime);   // Dersin bitiş saati
                            pstmt.setString(4, startTime); // Dersin başlangıç saati
                            ResultSet rs = pstmt.executeQuery();
                            if (rs.next() && rs.getInt("conflict_count") > 0) {
                                showAlert("Hata",
                                        "Öğrenci " + cb.getText() + " için çakışma var!\n" +
                                                "Bu öğrencinin seçilen zaman diliminde başka bir dersi var.");
                                return true;
                            }
                        }
                    }
                }
            }
            return false; // Çakışma yok
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Hata", "Çakışma kontrolü sırasında bir hata oluştu: " + e.getMessage());
            return true; // Hata durumunda güvenli tarafta kal
        }
    }
        private String getStudentIdByName(String studentName) {
        String sql = "SELECT id FROM students WHERE name = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return String.valueOf(rs.getInt("id")); // INTEGER olduğu için String'e çeviriyoruz
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching student ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;

    }}