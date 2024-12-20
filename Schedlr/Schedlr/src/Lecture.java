import java.util.ArrayList;
import java.util.List;

public class Lecture {
    private int id;
    private String name;
    private TimeSlot timeSlot;
    private int studentCount;
    private List<Student> enrolledStudents;
    private Classroom assignedClassroom;

    public Lecture(int id, String name, TimeSlot timeSlot, int studentCount) {
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

    public int getId() {
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

    public static Lecture findLectureById(List<Lecture> lectures, int id) {
        for (Lecture lecture : lectures) {
            if (lecture.getId() == id) {
                return lecture;
            }
        }
        return null;
    }

    public void assignClassroom(Classroom classroom) {
        this.assignedClassroom = classroom;
    }

    // Method to remove a lecture from a list of lectures
    public void removeLecture(List<Lecture> lectures) {
        // Remove this lecture from the provided list
        lectures.remove(this);
    }

    // Static method to add a new lecture to the lecture list
    public static void addLecture(List<Lecture> lectureList, String name, int id, TimeSlot timeSlot, int capacity) {
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
}
