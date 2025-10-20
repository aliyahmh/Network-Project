public class TimeSlot {
    private String id;
    private boolean available;


    public TimeSlot(String id) {
        this.id = id;
        this.available = true;
    }
    
    public String getId() { return id; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    
}
