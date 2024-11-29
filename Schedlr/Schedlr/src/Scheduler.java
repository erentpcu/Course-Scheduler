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
                System.out.println("Assigned Lecture " + lecture.getId() +
                        " (" + lecture.getName() + ") to Classroom " + selectedClassroom.getId());
            } else {
                System.out.println("No available classroom for Lecture " + lecture.getId() +
                        " (" + lecture.getName() + ")");
            }
        }
    }

    private boolean isClassroomAvailable(Classroom classroom, TimeSlot timeSlot) {
        for (Lecture lecture : classroom.getLectures()) {
            if (lecture.getTimeSlot().overlaps(timeSlot)) {
                return false;
            }
        }
        return true;
    }
}
