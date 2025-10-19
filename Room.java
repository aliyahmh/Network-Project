public class Room {
    private String type;
    private String name;
    private String day;
    private String time;
    private boolean available;

    public Room(String type, String name, String day, String time) {
        this.type = type;
        this.name = name;
        this.day = day;
        this.time = time;
        this.available = true;
    }

    public String getType() { return type; }
    public String getName() { return name; }
    public String getDay() { return day; }
    public String getTime() { return time; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String toString() {
        return name + " (" + type + ") - " + day + " (" + time + ")";
    }
}

