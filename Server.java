import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    static ArrayList<String> users = new ArrayList<String>();
    static ArrayList<Room> rooms = new ArrayList<Room>();
    static ArrayList<Reservation> reservations = new ArrayList<Reservation>();

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(1234);
            System.out.println("🏫 Room Reservation Server Started...");
            createRooms();

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
    public static void createRooms() {
        rooms.add(new Room("Lab", "Lab1", "Sunday", "8-10"));
        rooms.add(new Room("Lab", "Lab1", "Sunday", "10-12"));
        rooms.add(new Room("Lab", "Lab1", "Sunday", "12-2"));
        rooms.add(new Room("Meeting", "Meeting1", "Sunday", "8-10"));
        rooms.add(new Room("Meeting", "Meeting1", "Sunday", "10-12"));
        rooms.add(new Room("Meeting", "Meeting1", "Sunday", "12-2"));
        rooms.add(new Room("Research", "Research1", "Sunday", "8-10"));
        rooms.add(new Room("Research", "Research1", "Sunday", "10-12"));
        rooms.add(new Room("Research", "Research1", "Sunday", "12-2"));
        rooms.add(new Room("Regular", "Regular1", "Sunday", "8-10"));
        rooms.add(new Room("Regular", "Regular1", "Sunday", "10-12"));
        rooms.add(new Room("Regular", "Regular1", "Sunday", "12-2"));
        rooms.add(new Room("Seminar", "Seminar1", "Sunday", "8-10"));
        rooms.add(new Room("Seminar", "Seminar1", "Sunday", "10-12"));
        rooms.add(new Room("Seminar", "Seminar1", "Sunday", "12-2"));
    }

    // Login
    public static synchronized String login(String username) {
        if (username == null || username.trim().equals("")) {
            return "❌ Username cannot be empty.";
        }

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).equalsIgnoreCase(username))
                return "✅ Welcome back, " + username + "!";
        }

        users.add(username);
        return "✅ Hello " + username + "! You are now logged in.";
    }

    // View available rooms — sends each line separately
    public static synchronized void sendAvailableRooms(PrintWriter out, String type, String day) {
        int count = 0;
        for (int i = 0; i < rooms.size(); i++) {
            Room r = rooms.get(i);
            if (r.getType().equalsIgnoreCase(type)
                    && r.getDay().equalsIgnoreCase(day)
                    && r.isAvailable()) {
                count++;
                out.println(count + ". " + r.toString());
            }
        }

        if (count == 0)
            out.println("❌ No available " + type + " rooms on " + day + ".");
        else
            out.println("\nTotal: " + count + " available room(s).");

        // tell the client the list is over
        out.println("END");
    }

    // Reserve room with full validation
    public static synchronized String reserveRoom(String username, String roomName, String day, String time) {
        if (roomName.trim().equals("") || day.trim().equals("") || time.trim().equals("")) {
            return "❌ Please fill in all fields.";
        }

        // check if room exists
        Room target = null;
        for (int i = 0; i < rooms.size(); i++) {
            Room r = rooms.get(i);
            if (r.getName().equalsIgnoreCase(roomName)
                    && r.getDay().equalsIgnoreCase(day)
                    && r.getTime().equalsIgnoreCase(time)) {
                target = r;
                break;
            }
        }

        if (target == null)
            return "❌ Invalid room name, day, or time.";

        // check if already reserved by anyone
        if (!target.isAvailable())
            return "❌ That slot is already reserved by someone else.";

        // check if user already reserved this slot
        for (int i = 0; i < reservations.size(); i++) {
            Reservation res = reservations.get(i);
            if (res.getUsername().equalsIgnoreCase(username)
                    && res.getRoomName().equalsIgnoreCase(roomName)
                    && res.getDay().equalsIgnoreCase(day)
                    && res.getTime().equalsIgnoreCase(time)) {
                return "❌ You already reserved this slot.";
            }
        }

        // reserve
        target.setAvailable(false);
        reservations.add(new Reservation(username, roomName, day, time));
        return "✅ Reservation confirmed for " + roomName + " on " + day + " (" + time + ")";
    }

    // Cancel reservation
    public static synchronized String cancelReservation(String username, String roomName, String day, String time) {
        if (roomName.trim().equals("") || day.trim().equals("") || time.trim().equals("")) {
            return "❌ Please fill in all fields.";
        }

        boolean found = false;

        for (int i = 0; i < reservations.size(); i++) {
            Reservation res = reservations.get(i);
            if (res.getUsername().equalsIgnoreCase(username)
                    && res.getRoomName().equalsIgnoreCase(roomName)
                    && res.getDay().equalsIgnoreCase(day)
                    && res.getTime().equalsIgnoreCase(time)) {
                reservations.remove(i);
                found = true;
                break;
            }
        }

        if (!found)
            return "❌ You don’t have this reservation.";

        // make room available again
        for (int i = 0; i < rooms.size(); i++) {
            Room r = rooms.get(i);
            if (r.getName().equalsIgnoreCase(roomName)
                    && r.getDay().equalsIgnoreCase(day)
                    && r.getTime().equalsIgnoreCase(time)) {
                r.setAvailable(true);
            }
        }

        return "✅ Reservation cancelled successfully.";
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
