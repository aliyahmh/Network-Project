public class Room {
    private String type;
    private String ID;
    private boolean available;

    public Room(String type, String id) {
        this.type = type;
        this.ID = id;
        this.available = true;
    }

    public String getType() { return type; }
    public String getName() { return ID; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String toString() {
        return ID + " (" + type + ")";
    }
}

