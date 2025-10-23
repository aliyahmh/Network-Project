import java.util.LinkedList;

public class User {
 private String username;
 private String password;
 private int reservationCount;
 LinkedList<Reservation> reservations;


 public User(String name, String pass){
    username=name;
    password=pass;
    reservations=new LinkedList<Reservation>();
 }

 public String getUserName(){
    return username;
 }



 public void addReservation(Reservation r) {
    reservations.add(r);
}

public void removeReservation(Reservation r) {
    reservations.remove(r);
}
}










}
