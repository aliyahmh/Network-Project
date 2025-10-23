public class Reservation {
    private String username;
    private String roomID;
    private String day;
    private String time;
    private String ID;

    public Reservation(String username, String roomID, String day, String time, String ID) {
        this.username = username;
        this.roomID = roomID;
        this.day = day;
        this.time = time;
        this.ID = ID;
    }

    public String getUsername() { return username; }
    public String getRoomID() { return roomID; }
    public String getDay() { return day; }
    public String getTime() { return time; }
    public String getID() { return ID; }


    

    public String toString() {
        return username + " reserved " + roomID + " on " + day + " (" + time + ")";
    }
}
