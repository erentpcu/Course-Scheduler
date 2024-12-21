import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Classroom {
    private String id;
    private int capacity;
    private List<Lecture> lectures;
    private Map<String, Map<String, Boolean>> schedule; // Availability by day and time slot


    public Classroom(String id, int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.lectures = new ArrayList<>();
        this.schedule = new HashMap<>();

        // Initialize schedule with all time slots marked as available (true)
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
        String[] times = {
                "08:30 - 09:25", "09:25 - 10:20", "10:20 - 11:15", "11:15 - 12:10",
                "12:10 - 13:05", "13:05 - 14:00", "14:00 - 14:55", "14:55 - 15:50"
        };

        for (String day : days) {
            Map<String, Boolean> daySchedule = new HashMap<>();
            for (String time : times) {
                daySchedule.put(time, true); // All slots available initially
            }
            schedule.put(day, daySchedule);
        }
    }

    public void addLecture(Lecture lecture) {
        lectures.add(lecture);
    }

    public List<Lecture> getLectures() {
        return lectures;
    }

    public String getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    // Fetch all classrooms from the database
    public static List<Classroom> fetchAllClassrooms() {
        String sql = "SELECT * FROM classrooms";
        List<Classroom> classrooms = new ArrayList<>();

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                int capacity = rs.getInt("capacity");
                classrooms.add(new Classroom(id, capacity));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching classrooms: " + e.getMessage());
        }

        return classrooms;
    }

    public boolean isClassroomAvailable(String day, String time) {
        String sql = "SELECT * FROM classroom_schedule WHERE classroom_id = ? AND day = ? AND time_slot = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.id);
            pstmt.setString(2, day);
            pstmt.setString(3, time);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("available");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking availability: " + e.getMessage());
        }
        return false; // Default to unavailable
    }

    public static Classroom findClassroomById(List<Classroom> classrooms, String id) {
        for (Classroom classroom : classrooms) {
            if (classroom.getId().equals(id)) {
                return classroom;
            }
        }
        return null;
    }

    public boolean isAvailable(String day, String time) {
        // Check if the specified day and time slot is available
        Map<String, Boolean> daySchedule = schedule.get(day.toUpperCase());
        if (daySchedule != null) {
            Boolean available = daySchedule.get(time);
            return available != null && available;
        }
        return false;
    }

    public boolean reserve(String day, String startTime, String endTime, String lectureName, String lectureId, int studentCount) {
        // Reserve the specified day and time slot and associate it with a lecture
        Map<String, Boolean> daySchedule = schedule.get(day.toUpperCase());
        String time = startTime + " - " + endTime;

        if (daySchedule != null && daySchedule.containsKey(time)) {
            if (daySchedule.get(time)) {
                daySchedule.put(time, false); // Mark as reserved
                TimeSlot timeSlot = new TimeSlot(1, day, startTime, endTime);
                Lecture newLecture = new Lecture(lectureId, lectureName, timeSlot, studentCount);
                addLecture(newLecture);
                System.out.println("Classroom " + id + " reserved for " + lectureName + " on " + day + " from " + startTime + " to " + endTime);
                return true;
            } else {
                System.out.println("Classroom " + id + " is already reserved for " + day + " from " + startTime + " to " + endTime);
                return false;
            }
        }
        System.out.println("Invalid day or time slot.");
        return false;
    }

    public boolean release(String day, String startTime, String endTime) {
        // Release the specified day and time slot
        Map<String, Boolean> daySchedule = schedule.get(day.toUpperCase());
        String time = startTime + " - " + endTime;

        if (daySchedule != null && daySchedule.containsKey(time)) {
            if (!daySchedule.get(time)) {
                daySchedule.put(time, true); // Mark as available

                // Remove the associated lecture
                lectures.removeIf(lecture -> lecture.getTimeSlot().getDay().equalsIgnoreCase(day)
                        && lecture.getTimeSlot().getStartTime().equals(startTime)
                        && lecture.getTimeSlot().getEndTime().equals(endTime));

                System.out.println("Classroom " + id + " is now available for " + day + " from " + startTime + " to " + endTime);
                return true;
            } else {
                System.out.println("Classroom " + id + " is already available for " + day + " from " + startTime + " to " + endTime);
                return false;
            }
        }
        System.out.println("Invalid day or time slot.");
        return false;
    }

    public void saveToDatabase() {
        String sql = "INSERT INTO classrooms (id, capacity) VALUES (?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.id);
            pstmt.setInt(2, this.capacity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving classroom to database: " + e.getMessage());
        }




        //todo availability attribute
        //todo isAvailable()
        //todo reserve()
    }
}