import java.util.ArrayList;
import java.util.List;

public class Classroom {
    private String id;
    private int capacity;
    private List<Lecture> lectures;

    public Classroom(String id, int capacity) {
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


    //todo availability attribute
    //todo isAvailable()
    //todo reserve()
}
