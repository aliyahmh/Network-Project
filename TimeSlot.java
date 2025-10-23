public class TimeSlot {
    private String id;
    private boolean available;
    private String day;
    private String time;



    public TimeSlot(String id) {
        this.id = id;
        this.available = true;
    }
    
    public String getId() { return id; }
    public String getDay() { return day; }
    public String getTime() { return time; }


    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    
}
