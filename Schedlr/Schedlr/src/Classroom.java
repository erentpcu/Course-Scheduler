import java.util.ArrayList;
import java.util.List;

public class Classroom {
    private int id;
    private int capacity;
    private List<Lecture> lectures;

    public Classroom(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.lectures = new ArrayList<>();
    }

    public void addLecture(Lecture lecture) {
        lectures.add(lecture);
    }

    public List<Lecture> getLectures() {
        return lectures;
    }

    public int getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public static Classroom findClassroomById(List<Classroom> classrooms, int id) {
        for (Classroom classroom : classrooms) {
            if (classroom.getId() == id) {
                return classroom;
            }
        }
        return null;
    }
}
