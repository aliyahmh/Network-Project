import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    static LinkedList<User> users = new LinkedList<User>();
    static ArrayList<Room> rooms = new ArrayList<Room>();
    static ArrayList<Reservation> reservations = new ArrayList<Reservation>();

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(1234);
            System.out.println("🏫 Room Reservation Server Started...");
            initializeRooms();

            while (true) {
                Socket client = server.accept();
                System.out.println("New client connected!");
                new Thread(new ClientHandler(client)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Create rooms manually
   private static void initializeRooms() {
        String[] roomTypes = {"Lab", "Meeting Room", "Regular Room", "Research Room", "Conference Room"};
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String[] timeSlots = {"8am-10am","10am-12pm","1pm-2pm"};
        
        for (String type : roomTypes) {
            for (int j = 1; j <= 5; j++) {
                String roomId = type + "-" + j;
                Room room = new Room(roomId, type);
                
                for (String day : days) {
                    for (String time : timeSlots) {
                        String slotId = roomId + "-" + day + "-" + time;
                        room.addTimeSlot(new TimeSlot(slotId));
                    }
                }
                rooms.add(room);
            }
        }
    }

    // Register
    public static synchronized String Register(String username, String pass) {
        if (username == null || username.trim().equals("")) {
            return "❌ Username cannot be empty.";
        }
        User u= new User(username, pass);
         users.add(u);
        return "✅ Hello " + username + "! You are now logged in.";
        /*for (int i = 0; i < users.size(); i++) {
            if (users.get(i).equalsIgnoreCase(username))
                return "✅ Welcome back, " + username + "!";
        }*/   
    }

    private static User findUser(String username) {
        for (User user : users) {
            if (user.getUserName().equals(username)) {
                return user;
            }
        }
        return null;
    }

    private static Reservation findReservation(String reservationId) {
        for (Reservation reservation : reservations) {
            if (reservation.getID().equals(reservationId)) {
                return reservation;
            }
        }
        return null;
    }




    public static String getAvailableSlots(String roomType, String day) {
        String result = "";
        
        for (Room room : rooms) {
            if (room.getType().equals(roomType)) {
                for (TimeSlot slot : room.getTimeSlots()) {
                    if (slot.getDay().equals(day) && slot.isAvailable()) {
                        if (!result.isEmpty()) {
                            result += "|";
                        }
                        result += room.getId() + " - " + slot.getTime();
                    }
                }
            }
        }
        return result.isEmpty() ? "ERROR: No available slots" : result;
    }

    
        
        


 

    public static String makeReservation(String username, String slotId) {
        String[] parts = slotId.split(" - ");
        if (parts.length < 3) {  // Now we expect 3 parts!
            return "ERROR: Invalid slot format";
        }
    
        String roomId = parts[0];   // e.g., "Lab-1"
        String day = parts[1];      // e.g., "Monday" 
        String time = parts[2];     // e.g., "8:00 AM"
        
        // Now we have all the information we need!
        // Rest of the method remains the same but simpler...
        
        // Find the room
        Room targetRoom = null;
        for (Room room : rooms) {
            if (room.getId().equals(roomId)) {
                targetRoom = room;
                break;
            }
        }
        
        if (targetRoom == null) {
            return "ERROR: Room not found";
        }
        
        // Find the specific time slot using ALL three identifiers
        TimeSlot targetSlot = null;
        String fullSlotId = roomId + "-" + day + "-" + time;
        
        for (TimeSlot slot : targetRoom.getTimeSlots()) {
            if (slot.getId().equals(fullSlotId) && slot.isAvailable()) {
                targetSlot = slot;
                break;
            }
        }
        
        if (targetSlot == null) {
            return "ERROR: Slot not available";
        }
        
        // Create reservation (same as before)
        targetSlot.setAvailable(false);
        String reservationId = "RES" + (reservations.size() + 1);
        Reservation reservation = new Reservation(reservationId, username, roomId, day, time);
        reservations.add(reservation);
        
        User user = findUser(username);
        if (user != null) {
            user.addReservation(reservation);
        }
        
        return "SUCCESS: Reservation confirmed. ID: " + reservationId;
    }


    public static String cancelReservation(String reservationId, String username) {
        // Find reservation
        Reservation reservationToCancel = null;
        
        for (Reservation reservation : reservations) {
            if (reservation.getID().equals(reservationId)) {
                    reservationToCancel = reservation;
                    break;
                }
            }
        
        
        if (reservationToCancel == null) {
            return "ERROR: Reservation not found";
        }
        
        // Mark the time slot as available again
        String roomId = reservationToCancel.getRoomID();
        String day = reservationToCancel.getDay();
        String time = reservationToCancel.getTime();
        
        for (Room room : rooms) {
            if (room.getId().equals(roomId)) {
                for (TimeSlot slot : room.getTimeSlots()) {
                    if (slot.getDay().equals(day) && slot.getTime().equals(time)) {
                        slot.setAvailable(true);
                        break;
                    }
                }
                break;
            }
        }
        
        // Remove reservation from list
        reservations.remove(reservationToCancel);
        
        // Remove from user's reservations
        User user = findUser(username);
        if (user != null) {
            user.removeReservation(reservationToCancel);
        }
        
        return "SUCCESS: Reservation cancelled";
    }





    // client handler
    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                String username = in.readLine();
                out.println(login(username));

                while (true) {
                    String option = in.readLine();

                    if (option.equals("1")) {
                        String type = in.readLine();
                        String day = in.readLine();
                        sendAvailableRooms(out, type, day);
                    }
                    else if (option.equals("2")) {
                        String room = in.readLine();
                        String day = in.readLine();
                        String time = in.readLine();
                        out.println(reserveRoom(username, room, day, time));
                    }
                    else if (option.equals("3")) {
                        String room = in.readLine();
                        String day = in.readLine();
                        String time = in.readLine();
                        out.println(cancelReservation(username, room, day, time));
                    }
                    else if (option.equals("4")) {
                        out.println("👋 Disconnected.");
                        break;
                    }
                    else {
                        out.println("❌ Invalid option.");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
