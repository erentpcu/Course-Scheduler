import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        String createStudentsTable = """
            CREATE TABLE IF NOT EXISTS students (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL
            );
        """;

        String createLecturesTable = """
    CREATE TABLE IF NOT EXISTS lectures (
        id TEXT PRIMARY KEY,
        name TEXT NOT NULL,
        lecturer TEXT,           // Yeni eklenen alan
        classroom_id TEXT,
        time_slot_id INTEGER NOT NULL,
        FOREIGN KEY (classroom_id) REFERENCES classrooms (id),
        FOREIGN KEY (time_slot_id) REFERENCES time_slots (id)
    );
""";

        String createClassroomsTable = """
            CREATE TABLE IF NOT EXISTS classrooms (
                id TEXT PRIMARY KEY,
                capacity INTEGER NOT NULL
            );
        """;

        String createTimeSlotsTable = """
            CREATE TABLE IF NOT EXISTS time_slots (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                day TEXT NOT NULL,
                start_time TEXT NOT NULL,
                end_time TEXT NOT NULL
            );
        """;

        String createStudentScheduleTable = """
            CREATE TABLE IF NOT EXISTS student_schedule (
                student_id INTEGER,
                lecture_id TEXT,
                PRIMARY KEY (student_id, lecture_id),
                FOREIGN KEY (student_id) REFERENCES students (id),
                FOREIGN KEY (lecture_id) REFERENCES lectures (id)
            );
        """;


        String createClassroomScheduleTable = """
            CREATE TABLE IF NOT EXISTS classroom_schedule (
                classroom_id TEXT,
                day TEXT,
                time_slot TEXT,
                lecture_id TEXT,
                available BOOLEAN DEFAULT TRUE,
                PRIMARY KEY (classroom_id, day, time_slot),
                FOREIGN KEY (classroom_id) REFERENCES classrooms (id),
                FOREIGN KEY (lecture_id) REFERENCES lectures (id)
            );
        """;

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {
            // Execute the table creation scripts
            stmt.execute(createStudentsTable);
            stmt.execute(createLecturesTable);
            stmt.execute(createClassroomsTable);
            stmt.execute(createTimeSlotsTable);
            stmt.execute(createStudentScheduleTable);
            stmt.execute(createClassroomScheduleTable);
            System.out.println("Database tables created successfully.");
        } catch (Exception e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }
}
