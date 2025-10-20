import java.util.LinkedList;

public class User {
 private String username;
 private String password;
 LinkedList<Reservation> reservations;


 public User(String name, String pass){
    username=name;
    password=pass;
    reservations=new LinkedList<Reservation>();
 }


}
