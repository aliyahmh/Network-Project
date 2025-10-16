import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

  
public class NewClient implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<NewClient> clients;
    
      public NewClient (Socket c,ArrayList<NewClient> clients) throws IOException
      {
        this.client = c;
        this.clients=clients;
        in= new BufferedReader (new InputStreamReader(client.getInputStream())); 
        out=new PrintWriter(client.getOutputStream(),true); 
      }
      public void run ()
      {
       try{
        while (true){
            String request=in.readLine();  
                    outToAll(request);
       
        }
    }
       catch (IOException e){
           System.err.println("IO exception in new client class");
           System.err.println(e.getStackTrace());
       }
    finally{
        out.close();
           try {
               in.close();
           } catch (IOException ex) {
              ex.printStackTrace();
           }
    }
      }
        private void outToAll(String substring) {
    for (NewClient aclient:clients){
       aclient.out.println(substring); 
    }
        }
    }
    
    

  