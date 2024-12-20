import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Student {
    private int id;
    private String name;
    private List<Lecture> schedule;

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
        this.schedule = new ArrayList<>();
    }

    public void addLecture(Lecture lecture) {
        schedule.add(lecture);
        lecture.enrollStudent(this);

        // Save lecture enrollment in the database
        saveEnrollmentToDatabase(lecture);
    }


    // Save student to the database
    public void saveToDatabase() {
        String sql = "INSERT INTO students (id, name) VALUES (?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.id);
            pstmt.setString(2, this.name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving student to database: " + e.getMessage());
        }
    }

    // Save lecture enrollment to the database
    private void saveEnrollmentToDatabase(Lecture lecture) {
        String sql = "INSERT INTO student_schedule (student_id, lecture_id) VALUES (?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.id);
            pstmt.setString(2, lecture.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving enrollment: " + e.getMessage());
        }
    }

    // Remove lecture enrollment from the database
    private void removeEnrollmentFromDatabase(Lecture lecture) {
        String sql = "DELETE FROM student_schedule WHERE student_id = ? AND lecture_id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.id);
            pstmt.setString(2, lecture.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error removing enrollment: " + e.getMessage());
        }
    }

    // Fetch the student's schedule from the database
    private List<Lecture> fetchScheduleFromDatabase() {
        String sql = """
            SELECT l.id, l.name, l.time_slot_id
            FROM lectures l
            INNER JOIN student_schedule ss ON l.id = ss.lecture_id
            WHERE ss.student_id = ?
        """;
        List<Lecture> fetchedSchedule = new ArrayList<>();

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String lectureId = rs.getString("id");
                    String lectureName = rs.getString("name");
                    int timeSlotId = rs.getInt("time_slot_id");

                    TimeSlot timeSlot = TimeSlot.fetchById(timeSlotId); // Assuming TimeSlot has a fetchById method
                    Lecture lecture = new Lecture(lectureId, lectureName, timeSlot, 0);
                    fetchedSchedule.add(lecture);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching schedule: " + e.getMessage());
        }

        return fetchedSchedule;
    }


    // Fetch student details from the database
    public static Student findStudentById(List<Student> students, int id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    return new Student(id, name);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching student: " + e.getMessage());
        }
        return null;
    }
    public void removeLecture(Lecture lecture) {
        if (schedule.remove(lecture)) {
            lecture.getEnrolledStudents().remove(this);
            lecture.setStudentCount(lecture.getStudentCount() - 1);

            // Remove lecture enrollment from the database
            removeEnrollmentFromDatabase(lecture);
        } else {
            System.out.println("Lecture not found in the student's schedule.");
        }
    }

    public List<Lecture> getSchedule() {
        if (schedule.isEmpty()) {
            schedule = fetchScheduleFromDatabase();
        }
        return schedule;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
