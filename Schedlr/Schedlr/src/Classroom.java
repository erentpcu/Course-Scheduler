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

    public boolean reserve(String day, String startTime, String endTime, String lectureName, int lectureId, int studentCount) {
        // Reserve the specified day and time slot and associate it with a lecture
        Map<String, Boolean> daySchedule = schedule.get(day.toUpperCase());
        String time = startTime + " - " + endTime;

        if (daySchedule != null && daySchedule.containsKey(time)) {
            if (daySchedule.get(time)) {
                daySchedule.put(time, false); // Mark as reserved
                TimeSlot timeSlot = new TimeSlot(day, startTime, endTime);
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


    //todo availability attribute
    //todo isAvailable()
    //todo reserve()
}
