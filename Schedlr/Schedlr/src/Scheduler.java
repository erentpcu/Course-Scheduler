import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Scheduler {

    public void organizeLectures(List<Lecture> lectures, List<Classroom> classrooms) {
        // Sort lectures by student count in descending order
        lectures.sort((l1, l2) -> l2.getStudentCount() - l1.getStudentCount());

        // Assign classrooms to lectures
        for (Lecture lecture : lectures) {
            Classroom selectedClassroom = null;

            for (Classroom classroom : classrooms) {
                if (classroom.getCapacity() >= lecture.getStudentCount() &&
                        isClassroomAvailable(classroom, lecture.getTimeSlot())) {
                    selectedClassroom = classroom;
                    break;
                }
            }

            if (selectedClassroom != null) {
                selectedClassroom.addLecture(lecture);
                lecture.assignClassroom(selectedClassroom);

                // Save the assignment to the database
                saveClassroomAssignmentToDatabase(selectedClassroom, lecture);

                System.out.println("Assigned Lecture " + lecture.getId() +
                        " (" + lecture.getName() + ") to Classroom " + selectedClassroom.getId());
            } else {
                System.out.println("No available classroom for Lecture " + lecture.getId() +
                        " (" + lecture.getName() + ")");
            }
        }
    }

    private boolean isClassroomAvailable(Classroom classroom, TimeSlot timeSlot) {
        try {
            return classroom.isClassroomAvailable(timeSlot.getDay(), timeSlot.getStartTime() + " - " + timeSlot.getEndTime());
        } catch (Exception e) {
            System.out.println("Error checking classroom availability: " + e.getMessage());
            return false;
        }
    }

    private void saveClassroomAssignmentToDatabase(Classroom classroom, Lecture lecture) {
        String sql = """
            INSERT INTO classroom_schedule (classroom_id, day, time_slot, lecture_id, available)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, classroom.getId());
            pstmt.setString(2, lecture.getTimeSlot().getDay());
            pstmt.setString(3, lecture.getTimeSlot().getStartTime() + " - " + lecture.getTimeSlot().getEndTime());
            pstmt.setString(4, lecture.getId());
            pstmt.setBoolean(5, false); // Mark the slot as reserved
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving classroom assignment to database: " + e.getMessage());
        }
    }
}
