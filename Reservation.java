public class Reservation {
        private String user;
        private String type;
        private String day;
        private String time;
    
        public Reservation(String user, String type, String day, String time) {
            this.user = user;
            this.type = type;
            this.day = day;
            this.time = time;
        }
    
        // getters
        public String getUser() { return user; }
        public String getType() { return type; }
        public String getDay() { return day; }
        public String getTime() { return time; }
    
        public String toString() {
            return type + " " + day + " " + time + " (by " + user + ")";
        }
}
    
