import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        // Önce bağımlılığı olmayan tabloları oluştur
        String createStudentsTable = """
       CREATE TABLE IF NOT EXISTS students (
           id INTEGER PRIMARY KEY,
           name TEXT NOT NULL
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
        String createLecturesTable = """
       CREATE TABLE IF NOT EXISTS lectures (
           id TEXT PRIMARY KEY,
           name TEXT NOT NULL,
           lecturer TEXT NOT NULL,
           classroom_id TEXT,
           time_slot_id INTEGER,
           FOREIGN KEY (classroom_id) REFERENCES classrooms(id),
           FOREIGN KEY (time_slot_id) REFERENCES time_slots(id)
       );
   """;
        String createStudentScheduleTable = """
       CREATE TABLE IF NOT EXISTS student_schedule (
           student_id INTEGER,
           lecture_id TEXT,
           PRIMARY KEY (student_id, lecture_id),
           FOREIGN KEY (student_id) REFERENCES students(id),
           FOREIGN KEY (lecture_id) REFERENCES lectures(id)
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
           FOREIGN KEY (classroom_id) REFERENCES classrooms(id),
           FOREIGN KEY (lecture_id) REFERENCES lectures(id)
       );
   """;

        String insertClassroomsData = """
            INSERT OR IGNORE INTO classrooms (id, capacity) VALUES 
            ('C201', 35),
            ('C202', 35),
            ('C203', 40),
            ('C204', 40),
            ('C205', 40),
            ('C206', 45),
            ('C207', 45),
            ('C208', 45),
            ('C301', 60),
            ('M01', 90),
            ('M101', 75),
            ('M102', 85),
            ('M103', 65),
            ('M201', 45),
            ('M202', 80),
            ('M203', 70),
            ('M204', 50),
            ('M205', 55),
            ('M206', 40),
            ('ML102', 20),
            ('ML103', 30),
            ('ML104', 25),
            ('ML105', 65),
            ('ML201', 60);
        """;

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {

            // Tabloları doğru sırayla oluştur (sadece yoksa)
            stmt.execute(createStudentsTable);
            stmt.execute(createClassroomsTable);
            stmt.execute(createTimeSlotsTable);
            stmt.execute(createLecturesTable);
            stmt.execute(createStudentScheduleTable);
            stmt.execute(createClassroomScheduleTable);
            // Sınıf verilerini ekle (INSERT OR IGNORE sayesinde var olanları tekrar eklemez)
            stmt.execute(insertClassroomsData);
            System.out.println("Database tables created and classroom data inserted successfully.");
        } catch (Exception e) {
            System.out.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}