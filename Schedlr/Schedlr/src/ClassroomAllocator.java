import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class ClassroomAllocator {

    public static void allocateClassrooms() {
        String selectLectures = "SELECT id, time_slot_id, (SELECT COUNT(student_id) FROM student_schedule WHERE lecture_id = lectures.id) AS enrolled_students FROM lectures";

        String findAvailableClassroom = """
            SELECT id
            FROM classrooms
            WHERE capacity >= ? AND id NOT IN (
                SELECT classroom_id
                FROM classroom_schedule
                WHERE day = (SELECT day FROM time_slots WHERE id = ?)
                AND time_slot = (SELECT start_time || '-' || end_time FROM time_slots WHERE id = ?)
            ) LIMIT 1;
        """;
        String updateLectureWithClassroom = "UPDATE lectures SET classroom_id = ? WHERE id = ?";

        try (Connection conn = Database.connect()) {
            conn.setAutoCommit(false);

            // Get all lectures and their enrollment
            try (PreparedStatement stmtLectures = conn.prepareStatement(selectLectures);
                 ResultSet rsLectures = stmtLectures.executeQuery()) {

                while (rsLectures.next()) {
                    String lectureId = rsLectures.getString("id");
                    int timeSlotId = rsLectures.getInt("time_slot_id");
                    int enrolledStudents = rsLectures.getInt("enrolled_students");

                    // Find a suitable classroom
                    try (PreparedStatement stmtClassroom = conn.prepareStatement(findAvailableClassroom)) {
                        stmtClassroom.setInt(1, enrolledStudents);
                        stmtClassroom.setInt(2, timeSlotId);
                        stmtClassroom.setInt(3, timeSlotId);

                        try (ResultSet rsClassroom = stmtClassroom.executeQuery()) {
                            if (rsClassroom.next()) {
                                String classroomId = rsClassroom.getString("id");

                                // Assign classroom to the lecture
                                try (PreparedStatement stmtUpdate = conn.prepareStatement(updateLectureWithClassroom)) {
                                    stmtUpdate.setString(1, classroomId);
                                    stmtUpdate.setString(2, lectureId);
                                    stmtUpdate.executeUpdate();
                                    System.out.println("Assigned classroom " + classroomId + " to lecture " + lectureId);
                                }
                            } else {
                                System.out.println("No available classroom for lecture: " + lectureId);
                            }
                        }
                    }
                }
            }
            conn.commit();
        } catch (Exception e) {
            System.out.println("Error during classroom allocation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getDayForTimeSlot(Connection conn, int timeSlotId) throws Exception {
        String query = "SELECT day FROM time_slots WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, timeSlotId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("day");
            }
        }
        throw new Exception("Time slot ID not found: " + timeSlotId);
    }

    private static String getTimeSlotForTimeSlotId(Connection conn, int timeSlotId) throws Exception {
        String query = "SELECT start_time || '-' || end_time AS time_slot FROM time_slots WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, timeSlotId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("time_slot");
            }
        }
        throw new Exception("Time slot ID not found: " + timeSlotId);
    }

    // Helper class to store classroom details
    private static class ClassroomInfo {
        String classroomId;
        int capacity;

        ClassroomInfo(String classroomId, int capacity) {
            this.classroomId = classroomId;
            this.capacity = capacity;
        }
    }
}
