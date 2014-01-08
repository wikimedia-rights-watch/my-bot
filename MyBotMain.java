import org.jibble.pircbot.*;
import java.io.*;

public class MyBotMain {
    
    public static void main(String[] args) throws Exception {
        
        // Now start our bot up.
        MyBot bot = new MyBot();
        
        // Enable debugging output.
        bot.setVerbose(false);
        
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
        
        //Identify to NickServ
        bot.identify(password);
        	
        try {
			BufferedReader reader = new BufferedReader(new FileReader("channels.txt"));
			
			//this is the header
			String line = reader.readLine();
			
			String[] header = line.split(";");
			int numChannels = Integer.parseInt(header[1]);
			
			for (int i = 0; i < numChannels; i++) {
				line = reader.readLine();
				String[] pieces = line.split(";");
				Channel channel = new Channel(pieces[0].replace(";",""),Boolean.parseBoolean(pieces[1]));
				bot.joinChannel(channel);
				bot.channelList.add(channel);
				waiting (10);
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