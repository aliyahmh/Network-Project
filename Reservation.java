public class Reservation {
    private String username;
    private String roomName;
    private String day;
    private String time;

    public Reservation(String username, String roomName, String day, String time) {
        this.username = username;
        this.roomName = roomName;
        this.day = day;
        this.time = time;
    }

    public String getUsername() { return username; }
    public String getRoomName() { return roomName; }
    public String getDay() { return day; }
    public String getTime() { return time; }

    public String toString() {
        return username + " reserved " + roomName + " on " + day + " (" + time + ")";
    }
}
