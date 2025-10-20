public class Room {
    private String type;
    private String ID;
    private TimeSlot[] timeSlots;
    private int timeSlotCount;


    public Room(String type, String id) {
        this.type = type;
        this.ID = id;
        this.timeSlots = new TimeSlot[21]; // 7 days * 3 time slots
        this.timeSlotCount = 0;
    }
    
    public String getId() { return ID; }
    public String getType() { return type; }
    public TimeSlot[] getTimeSlots() { return timeSlots; }
    public int getTimeSlotCount() { return timeSlotCount; }
    
    public void addTimeSlot(TimeSlot slot) {
        if (timeSlotCount < timeSlots.length) {
            timeSlots[timeSlotCount] = slot;
            timeSlotCount++;
        }
    }
}

