import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TimeSlot {
    private int id;
    private String day;
    private String startTime;
    private String endTime;


    // Method to fetch a TimeSlot by its ID
    public static TimeSlot fetchById(int timeSlotId) {
        String sql = "SELECT * FROM time_slots WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, timeSlotId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String day = rs.getString("day");
                    String startTime = rs.getString("start_time");
                    String endTime = rs.getString("end_time");
                    return new TimeSlot(timeSlotId, day, startTime, endTime);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching TimeSlot: " + e.getMessage());
        }
        return null;
    }

        public TimeSlot(String day, String startTime, String endTime) {
            this.day = day;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public TimeSlot(int id, String day, String startTime, String endTime) {
            this.id = id;
            this.day = day;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public int getId() {
            return id;
        }

        public String getDay() {
            return day;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void saveToDatabase() {
            String sql = "INSERT INTO time_slots (day, start_time, end_time) VALUES (?, ?, ?)";

            try (Connection conn = Database.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, this.day);
                pstmt.setString(2, this.startTime);
                pstmt.setString(3, this.endTime);
                pstmt.executeUpdate();

                // Retrieve and set the generated ID
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.id = rs.getInt(1); // Set the auto-generated ID
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error saving time slot to database: " + e.getMessage());
            }
        }
    }
