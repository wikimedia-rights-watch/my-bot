import org.jibble.pircbot.*;
import java.io.*;

public class MyBotMain {
    
    public static void main(String[] args) throws Exception {
        
        // Now start our bot up.
        MyBot bot = new MyBot();
        
        // Enable debugging output.
        bot.setVerbose(true);
        
        // Connect to the IRC server.
        bot.connect("irc.freenode.net");

        String password = new String();
		try {
			BufferedReader reader = new BufferedReader(new FileReader("password.txt"));
        	password = reader.readLine();
			reader.close();
		}
		catch (Exception e) {
		}
        	bot.identify(password);
        	
        try {
			BufferedReader reader = new BufferedReader(new FileReader("channels.txt"));
			String line = reader.readLine();
        	while ((line) != null) {
					bot.joinChannel(line);
					waiting (10);
					line = reader.readLine();
				}
			reader.close();
		}
		catch (Exception e) {
		}
}

public static void waiting (int n){
        
        long t0, t1;

        t0 =  System.currentTimeMillis();

        do{
            t1 = System.currentTimeMillis();
        }
        while ((t1 - t0) < (n * 1000));
    }
    
}