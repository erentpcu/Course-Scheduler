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
    }

    public List<Lecture> getSchedule() {
        return schedule;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static Student findStudentById(List<Student> students, int id) {
        for (Student student : students) {
            if (student.getId() == id) {
                return student;
            }
        }
        return null;
    }


    //TODO REMOVE Student METHOD WILL BE ADDED
}