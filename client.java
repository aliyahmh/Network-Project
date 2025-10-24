import java.io.*;
import java.net.*;
import java.util.*;

public class client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner sc = new Scanner(System.in);

            System.out.println("🏫 Connected to Room Reservation Server!");
            System.out.print("Enter your username: ");
            String username = sc.nextLine();
            out.println(username);
            System.out.print("Enter your password: ");
            String pass = sc.nextLine();
            out.println(pass);
            System.out.println(in.readLine());


            while (true) {
                System.out.println("\nMenu:");
                System.out.println("1. View Available Rooms");
                System.out.println("2. Reserve a Room");
                System.out.println("3. Cancel a Reservation");
                System.out.println("4. Exit");
                System.out.print("Choose: ");
                String choice = sc.nextLine();
                out.println(choice);

                if (choice.equals("1")) {
                    System.out.print("Enter room type (Lab/Meeting/Regular/Research/Seminar): ");
                    String type = sc.nextLine();
                    System.out.print("Enter day (e.g., Sunday): ");
                    String day = sc.nextLine();
                    out.println(type);
                    out.println(day);

                    // read lines until server says END
                    System.out.println();
                    String line;
                    while (!(line = in.readLine()).equals("END")) {
                        System.out.println(line);
                    }
                }
                else if (choice.equals("2")) {
                     System.out.print("Enter  name (e.g., Lab1): ");
                    String user = sc.nextLine();
                    System.out.print("Enter room name (e.g., Lab1): ");
                    String room = sc.nextLine();
                    System.out.print("Enter day: ");
                    String day = sc.nextLine();
                    System.out.print("Enter time (8-10 / 10-12 / 12-2): ");
                    String time = sc.nextLine();
                    String slotid=room+" - "+day+" - "+time;
                    out.println(user);
                    out.println(slotid);
                    System.out.println("\n" + in.readLine());
                }
                else if (choice.equals("3")) {
                    System.out.print("Enter room reservation id : ");
                    String id = sc.nextLine();
                    System.out.print("Enter username: ");
                    String name = sc.nextLine();
                    out.println(id);
                    out.println(name);
                    System.out.println("\n" + in.readLine());
                }
                else if (choice.equals("4")) {
                    System.out.println(in.readLine());
                    break;
                }
                else {
                    System.out.println("❌ Invalid choice.");
                }
            }

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
