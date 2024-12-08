public class TimeSlot {
    private String day;
    private String startTime;
    private String endTime;

    public TimeSlot(String day, String startTime, String endTime) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean overlaps(TimeSlot other) {
        if (!this.day.equals(other.day)) return false;
        return !(this.endTime.compareTo(other.startTime) <= 0 || this.startTime.compareTo(other.endTime) >= 0);
    }

    @Override
    public String toString() {
        return day + " " + startTime + " - " + endTime;
    }
}
