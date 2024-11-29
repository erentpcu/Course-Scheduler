import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Sample Data
        List<Student> students = new ArrayList<>();
        List<Classroom> classrooms = new ArrayList<>();
        List<Lecture> lectures = new ArrayList<>();

        // Create Students
        Student student1 = new Student(1, "Alice");
        Student student2 = new Student(2, "Bob");
        students.add(student1);
        students.add(student2);

        // Create Classrooms
        classrooms.add(new Classroom(1, 30));
        classrooms.add(new Classroom(2, 50));
        classrooms.add(new Classroom(3, 100));

        // Create TimeSlots
        TimeSlot slot1 = new TimeSlot("Monday", "09:00", "10:00");
        TimeSlot slot2 = new TimeSlot("Monday", "10:00", "11:00");
        TimeSlot slot3 = new TimeSlot("Monday", "11:00", "12:00");

        // Create Lectures
        Lecture lecture1 = new Lecture(1, "Math 101", 25, slot1);
        Lecture lecture2 = new Lecture(2, "Physics 101", 35, slot2);
        Lecture lecture3 = new Lecture(3, "Chemistry 101", 20, slot1);
        Lecture lecture4 = new Lecture(4, "Biology 101", 45, slot3);
        lectures.add(lecture1);
        lectures.add(lecture2);
        lectures.add(lecture3);
        lectures.add(lecture4);

        Scheduler scheduler = new Scheduler();
        scheduler.organizeLectures(lectures, classrooms);

        // Scanner for User Input
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Search for a Student by Name");
            System.out.println("2. Search for a Classroom by ID");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("\nEnter Student Name: ");
                    String studentName = scanner.nextLine();
                    Student foundStudent = searchStudentByName(students, studentName);
                    if (foundStudent != null) {
                        System.out.println("Student: " + foundStudent.getName());
                        System.out.println("Schedule:");
                        for (Lecture lecture : foundStudent.getSchedule()) {
                            System.out.println(" - " + lecture.getName() + " at " + lecture.getTimeSlot());
                        }
                    } else {
                        System.out.println("Student not found.");
                    }
                    break;

                case 2:
                    System.out.print("\nEnter Classroom ID: ");
                    int classroomId = scanner.nextInt();
                    Classroom foundClassroom = searchClassroomById(classrooms, classroomId);
                    if (foundClassroom != null) {
                        System.out.println("Classroom " + foundClassroom.getId() + " Lectures:");
                        for (Lecture lecture : foundClassroom.getLectures()) {
                            System.out.println(" - " + lecture.getName() + " at " + lecture.getTimeSlot());
                        }
                    } else {
                        System.out.println("Classroom not found.");
                    }
                    break;

                case 3:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static Student searchStudentByName(List<Student> students, String name) {
        for (Student student : students) {
            if (student.getName().equalsIgnoreCase(name)) {
                return student;
            }
        }
        return null;
    }

    private static Classroom searchClassroomById(List<Classroom> classrooms, int id) {
        for (Classroom classroom : classrooms) {
            if (classroom.getId() == id) {
                return classroom;
            }
        }
        return null;
    }
}
