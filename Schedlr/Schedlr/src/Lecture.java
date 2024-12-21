import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Lecture {
    private String id;
    private String name;
    private TimeSlot timeSlot;
    private int studentCount;
    private List<Student> enrolledStudents;
    private Classroom assignedClassroom;

    public Lecture(String id, String name, TimeSlot timeSlot, int studentCount) {
        this.id = id;
        this.name = name;
        this.timeSlot = timeSlot;
        this.studentCount = studentCount;
        this.enrolledStudents = new ArrayList<>();
    }

    public void enrollStudent(Student student) {
        enrolledStudents.add(student);
        student.addLecture(this);
        studentCount++; // Increment student count when a student is enrolled
    }

    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public static Lecture fetchById(String lectureId) {
        String sql = "SELECT id, name, time_slot_id, student_count FROM lectures WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lectureId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("id");
                    String name = rs.getString("name");
                    int timeSlotId = rs.getInt("time_slot_id");
                    int studentCount = rs.getInt("student_count");

                    TimeSlot timeSlot = TimeSlot.fetchById(timeSlotId); // Assuming TimeSlot has fetchById
                    return new Lecture(id, name, timeSlot, studentCount);
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching lecture: " + e.getMessage());
        }
        return null; // Return null if lecture not found
    }

    public void updateToDatabase() {
        String sql = "UPDATE lectures SET name = ?, student_count = ?, time_slot_id = ? WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.name);
            pstmt.setInt(2, this.studentCount);
            pstmt.setInt(3, this.timeSlot.getId());
            pstmt.setString(4, this.id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating lecture: " + e.getMessage());
        }
    }

    public void deleteFromDatabase() {
        String sql = "DELETE FROM lectures WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting lecture: " + e.getMessage());
        }
    }

    public void assignClassroom(Classroom classroom) {
        if (classroom != null) {
            this.assignedClassroom = classroom;
        }
    }


    // Method to remove a lecture from a list of lectures
    public void removeLecture(List<Lecture> lectures) {
        // Remove this lecture from the provided list
        lectures.remove(this);
    }

    // Static method to add a new lecture to the lecture list
    public static void addLecture(List<Lecture> lectureList, String name, String id, TimeSlot timeSlot, int capacity) {
        Lecture newLecture = new Lecture(id, name, timeSlot, capacity);
        lectureList.add(newLecture);
    }

    // Method to check if the student count exceeds the classroom's capacity
    public boolean isCapacityExceed() {
        if (assignedClassroom == null) {
            System.out.println("No classroom assigned to lecture: " + name);
            return false; // Assuming no capacity exceeded if no classroom assigned
        }
        return studentCount > assignedClassroom.getCapacity();
    }

    public void saveToDatabase() {
        String sql = "INSERT INTO lectures (id, name, time_slot_id) VALUES (?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.id);
            pstmt.setString(2, this.name);
            pstmt.setInt(3, this.timeSlot.getId()); // Ensure timeSlot is valid
            pstmt.executeUpdate();
            System.out.println("Lecture saved: " + this.name); // Debug log
        } catch (SQLException e) {
            System.out.println("Error saving lecture to database: " + e.getMessage());
        }
    }
}
