import org.jibble.pircbot.*;
import java.util.*;
import java.io.*;
import java.math.*;
import java.net.*;

public class MyBot extends PircBot {
	HashMap<String, String> factoids = new HashMap<String, String>();
	boolean factExists = true;
	long time;
	long time2;
	long time3;
	int counter;
	int toKick;
	double factor = 0.0;
	private static String CHAN_SUPEROPS;
	private static String CHAN_OPS;
	private static String ADMINS;
	private static String COM_ADMINS;
	private static String OTRS;
	private static String DATA_ADMINS;
	private static String VOY_ADMINS;
	private static String FILEMOVER;
	private static String ROAD;
	private static String META_ADMINS;
	private static String CHECK;
	private static String DATA_OS;
	private static String CU;
	private static String OS;
	private static String GS;
	private static String AAROADS_ADMINS;
	private static String POINT_KEY;
	private static String POINT_VALUE;
	private static String TEMPLATE;
	private static String master;
	public static ArrayList<Channel> channelList;
	int hash = 0;
	
	public void setMaster(String value) {
		master = value;
	}
	/*
	Reloads from certain configuration files
	*/
	public void reload() {
		BufferedReader rights = null;
		    try {
		   		URL urlObject = new URL("http://www.rschen7754.com/rights.txt");
		    	HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
		     	InputStream in = connection.getInputStream();
		     	rights = new BufferedReader(new InputStreamReader(in));
				this.CHAN_SUPEROPS = rights.readLine();
				this.CHAN_OPS = rights.readLine();
				this.ADMINS = rights.readLine();
				this.COM_ADMINS = rights.readLine();
				this.OTRS = rights.readLine();
				this.DATA_ADMINS = rights.readLine();
				this.VOY_ADMINS = rights.readLine();
				this.FILEMOVER = rights.readLine();
				this.ROAD = rights.readLine();
				this.META_ADMINS = rights.readLine();
				this.DATA_OS = rights.readLine();
				this.CU = rights.readLine();
				this.OS = rights.readLine();
				this.AAROADS_ADMINS = rights.readLine();
				this.TEMPLATE = rights.readLine();
				this.GS = rights.readLine();

				rights.close();
				BufferedReader checker = new BufferedReader(new FileReader("check.txt"));
				this.CHECK = checker.readLine();
				checker.close();
		    } catch (MalformedURLException murle) {
		      System.out.println("URL string is invalid");
		      System.exit(0);
		    } catch (IOException ioe) {
		      System.out.println("Error opening URL connection");
		      System.exit(0);
		    }
	}
	
	/*
	Runs when the bot starts; put all initialization stuff here!
	*/
	public MyBot() {
		reload();
		time = time2 = time3 = System.nanoTime();
		counter = 0;
		
		channelList = new ArrayList<Channel>();
		
		try {
			BufferedReader reader;
			
			//set the nick
			reader = new BufferedReader(new FileReader("nick.txt"));
			this.setName(reader.readLine());
			reader.close();
			
			//read in the fact file
			reader = new BufferedReader(new FileReader("factsin.txt"));
			String line = null;
			while ((line=reader.readLine()) != null)
			{
				String key = line;
				line = reader.readLine();
				String fact = line;
				factoids.put(key,fact);
			}
			factoids.put("help", "This bot supports help, (super)op, quit, admin(@commons/meta/data/enwikivoyage/simple), checkuser, oversight(@data), filemover@commons, road, and bell, as well as basic link expansion. Learning and forgetting of commands is supported using is as well as answer. Ops can control the bot with !add and !remove.");
	  		reader.close();
	    
	    	//read in the points alias
	   		reader = new BufferedReader(new FileReader("point.txt"));
			POINT_KEY = reader.readLine();
			POINT_VALUE = reader.readLine();
			reader.close();
		
			toKick = 0;
		}
		catch (IOException e) {
		
		}
	}
	
	private void sendSenderMessage(String channel, String sender, String message) {
		sendMessage(channel, sender + message);
		//sendMessage(channel, channel);
	}
	
	public void checkReassess() {
		BufferedReader result = null;
		    try {
		     	URL urlObject = new URL("http://en.wikipedia.org/w/api.php?action=query&list=categorymembers&cmtitle=Category:U.S._Roads_project_articles_needing_reassessment&format=xml");
		      	HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
		      	InputStream in = connection.getInputStream();
		      	result = new BufferedReader(new InputStreamReader(in));
				String line = result.readLine();
			System.out.println(line);
			if (!line.equals("<?xml version=\"1.0\"?><api><query><categorymembers /></query></api>")) {
			line = line.replace("<?xml version=\"1.0\"?><api><query><categorymembers>","");
			line = line.replace("</categorymembers></query></api>","");
			line = line.replace("<","");
			String[] items = line.split("/>");
			
			int newHash = 0;
			for (int i = 0; i<items.length; i++) {
				items[i] = items[i].replace("\"","");
				items[i] = items[i].substring((items[i].indexOf(":")+1),items[i].length()-1);
				items[i] = items[i].replace(" ","_");
				
				newHash+=items[i].hashCode();
			}
			if (hash != newHash) {
				if (items.length > 0) {
					sendMessage("#wikipedia-en-roads","!bell");
					sendMessage("#wikipedia-en-roads","An editor has requested the reassessment of an article:");
				}
				for (int i = 0; i<items.length; i++) {
					sendMessage("#wikipedia-en-roads", "https://en.wikipedia.org/wiki/"+items[i]);
				}
			}
			hash = newHash;
		}
		    } catch (MalformedURLException murle) {
		      System.out.println("URL string is invalid");
		    } catch (IOException ioe) {
		      System.out.println("Error opening URL connection");
		    }
	}

	public void onServerPing(String response) {
		super.onServerPing(response);
		checkReassess();
	}
	public boolean checkTime(String channel) {
		long now = System.nanoTime();
		if (counter > 2) {
			if (now-time <= 1.2e11) {
				time = time2;
				time2 = time3;
				time3 = System.nanoTime();
				sendAction(channel, "barfs");
				sendMessage("Chanserv","OP "+ channel);
				toKick++;
				return true;
			}
		}
	
		time = time2;
		time2 = time3;
		time3 = System.nanoTime();
		return false;
	}

	public boolean isOp(String name, String channel) {
		org.jibble.pircbot.User[] users = getUsers(channel);
		for (org.jibble.pircbot.User user:users) {
			if (user.equals(name)) {
				if (user.isOp()) {
					return true;
				}
			}
		}
		return false;
	}

public boolean isOn(String name, String channel) {
	System.err.println(name + " "+ channel);
	org.jibble.pircbot.User[] users = getUsers(channel);
	for (org.jibble.pircbot.User user:users) {
		if (user.equals(name)) {
				return true;
		}
	}
	return false;
}

public void onPrivateMessage(String sender, String login, String hostname, String message ) {
	if (message.startsWith("!op")) {
		sendMessage(sender, sender+CHAN_OPS);
	}
	else if (message.equals("!check")) {
		checkReassess();
	}
	else if (message.equals("!gag")) {
		counter++;
		factor++;
		if (!checkTime("#wikipedia-en-roads"))
		sendAction("#wikipedia-en-roads", "gags");
	}
	else if (message.startsWith("!reset")) {
		sendMessage("Chanserv","OP #wikipedia-en-roads -USRD_bot");
		sendMessage("Chanserv","OP ##rschen7754 -USRD_bot");
		counter = 0;
		reload();
	}
	else if (message.startsWith("!factor")) {
		sendMessage(sender, "Factor is: "+factor);
	}
	else if (message.startsWith("!")) {
		String key = message.replace("!","");
		if (message.contains(" answer "))
		{
			sendMessage(sender, "Must do this in the channel.");
		}
		else if (message.contains(" is "))
		{
			sendMessage(sender, "Must do this in the channel.");
		}
		else if (factoids.containsKey(key.trim())==true)
			sendMessage(sender, factoids.get(key));
		else
			sendMessage(sender, key + "? I don't know what you're going on about...");
	}
	
}

	public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
		sendMessage("MemoServ", "SENDOPS " + channel +" "+kickerNick+" kicked "+recipientNick+" from the channel "+channel+" because "+reason);
	}

	public void onDisconnect() {
		System.exit (-1);
	}

	public void syncChannels() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("channels.txt"));
			String[] channels = getChannels();
			for (int i=0; i < channels.length; i++) {
				writer.write (channels[i] +"\n");
			}
			writer.close();
		}
		catch (Exception e) {
		}
	}

	/*
	Stub function that treats it as a message
	*/
	public void onAction(String sender, String login, String hostname, String target, String action) {
		onMessage(target, sender, login, hostname, action);
	}
	
	public void onMessage(String channel, String sender,
	String login, String hostname, String message) {
		if (sender.contains("bot") || sender.contains("collector") ) {
			return;
		}
		if (message.toLowerCase().contains("stalk") || message.toLowerCase().contains("ignore") ||
		message.toLowerCase().contains("unstalk") || message.toLowerCase().contains("unignore") ||
		message.toLowerCase().contains("!list") || message.toLowerCase().contains("!3list") || message.toLowerCase().contains("join") ||
		message.toLowerCase().contains("part")) {
			return;
		}

		if (message.startsWith("!")) {
			if (message.equals("!")) {
				return;
			}
			else if (message.equalsIgnoreCase("!time")) {
				String time = new java.util.Date().toString();
				sendSenderMessage(channel, sender, ": The time is now " + time);

			}
			else if (message.startsWith("!quit")) {

				if (isOp(sender, channel))
				{
					quitServer("User request to terminate");
					System.exit(0);
				}
				else
				{
					sendSenderMessage(channel, sender, ": You must become an op to quit the bot");
				}
			}
			else if (message.startsWith("!add")) {

				if (isOp(sender, channel))
				{
					if (message.startsWith("!add channel")) {
						joinChannel(message.replace("!add channel ",""));
						syncChannels();
					}
					else if (message.startsWith("!add trigger")) {
						sendSenderMessage(channel, sender, ": That does not work yet!");
					}
					else if (message.startsWith("!add category")) {
						sendSenderMessage(channel, sender, ": That does not work yet!");
					}
					else if (message.startsWith("!add point")) {
						sendSenderMessage(channel, sender, ": That does not work yet!");
					}
					else {
						sendSenderMessage(channel, sender, ": Valid terms: channel, trigger, category, point");
					}
				}
				else
				{
					sendSenderMessage(channel, sender, ": You must become an op to add a channel, trigger, category, or point!");
				}
			}
			else if (message.startsWith("!remove")) {

				if (isOp(sender, channel))
				{
					if (message.startsWith("!remove channel")) {
						if (channel.equals(master) && message.equals("!remove channel")) {
							sendSenderMessage(channel, sender, ": You cannot remove the master channel!");
						}
						else if (message.equals("!remove channel")) {
							partChannel(channel, "Requested by "+sender);
							syncChannels();
						}
						else if (!channel.equals(master)) {
							sendSenderMessage(channel, sender, ": You must be in the master channel to do that!");
							return;
						}
						else {
							partChannel(message.replace("!remove channel ",""), "Requested by "+sender);
							syncChannels();
						}
					}
					else if (message.startsWith("!remove trigger")) {
						sendSenderMessage(channel, sender, ": That does not work yet!");
					}
					else if (message.startsWith("!remove category")) {
						sendSenderMessage(channel, sender, ": That does not work yet!");
					}
					else if (message.startsWith("!remove point")) {
						sendSenderMessage(channel, sender, ": That does not work yet!");
					}
					else {
						sendSenderMessage(channel, sender, ": Valid terms: channel, trigger, category, point");
					}
				}
				else
				{
					sendSenderMessage(channel, sender, ": You must become an op to remove a channel, trigger, category, or point!");
				}
			}
			else if (message.startsWith("!lmgtfy")) {
				String key = message.replace("!lmgtfy ","http://www.lmgtfy.com/?q=");
				key = key.replace(" ","+");
				sendMessage(channel, key);
				factor+=10;
				return;
			}
			else if (message.startsWith("!forget")) {
				String key = message.replace("!forget ","");
				if (factoids.remove(key) == null) {
					sendMessage(channel, "There is no "+key+"!");
					return;
				}
				sendMessage(channel, "Ok, "+key+" is forgotten");
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter("factsin.txt"));
					Set<String> keys = factoids.keySet();
					for (String test: keys)
					{
						writer.write(test);
						writer.newLine();
						writer.write(factoids.get(test));
						writer.newLine();
					}
					writer.close();
			}
			catch (IOException e) {
				
			}
			}
			else if (message.startsWith("!admin")) {
				if (message.startsWith("!admin@commons")) {
					sendSenderMessage(channel, sender, COM_ADMINS);
				}
				else if (message.startsWith("!admin@wikidata") || message.startsWith("!admin@data")) {
					sendSenderMessage(channel, sender, DATA_ADMINS);
				}
				else if (message.startsWith("!admin@meta")) {
					sendSenderMessage(channel, sender, META_ADMINS);
				}
				else if (message.startsWith("!admin@voyage") || message.startsWith("!admin@wikivoyage") ||message.startsWith("!admin@enwikivoyage")) {
					sendSenderMessage(channel, sender, VOY_ADMINS);
				}
				else if (message.startsWith("!admin@aaroads") ) {
					sendSenderMessage(channel, sender, AAROADS_ADMINS);
				}
				else {
					sendSenderMessage(channel, sender, ADMINS);
				}
			}

			else if (message.equals("!tmi")) {
				factor+=4;
				sendMessage(channel, "ewwww");
				return;
			}
			else if (message.toLowerCase().startsWith("!oversight@wikidata") || message.toLowerCase().startsWith("!oversight@data")) {
				sendSenderMessage(channel, sender, DATA_OS);
			}
			else if (message.startsWith("!checkuser")) {
				sendSenderMessage(channel, sender, CU);
			}
			else if (message.startsWith("!oversight")) {
				sendSenderMessage(channel, sender, OS);
			}
			else if (message.startsWith("!template")) {
				sendSenderMessage(channel, sender, TEMPLATE);
			}
			else if (message.toLowerCase().startsWith("!otrs")) {
				sendSenderMessage(channel, sender, OTRS);
			}
			else if (message.startsWith("!bell")) {
				sendSenderMessage("#wikipedia-en-roads", sender, " requests the reassessment of an article.");
			}
			else if (message.startsWith("!fotn")) {
				sendMessage(channel, "Tonight's nomination for fail: *drumroll*");
			}
			else if (message.startsWith("!road")) {
				sendSenderMessage(channel, sender, ROAD);
			}
			
			else if (message.startsWith("!gs")) {
				sendSenderMessage(channel, sender, GS);
			}
		else if (message.startsWith("!"+POINT_KEY)) {
			sendMessage(channel, POINT_VALUE.replace("%d", ((int)(Math.max( (Math.random() * 1000), 101)+100*factor)+"")));
			factor = 0;
		}
		
		else if (message.startsWith("!superop")) {
			sendSenderMessage(channel, sender, CHAN_SUPEROPS);
		}
		else if (message.startsWith("!op")) {
			sendSenderMessage(channel, sender, CHAN_OPS);
		}
		else if (message.startsWith("!filemover@commons")) {
			sendSenderMessage(channel, sender, FILEMOVER);
		}
		else {
			
			//must be a fact then
			String key = message.substring(1);
			if (message.contains(" answer "))
			{
				String[] pieces = key.split(" answer ");
				//sendMessage(channel, pieces[0]);
				//sendMessage(channel, pieces[1]);
				factoids.put(pieces[0],pieces[1]);
				sendMessage(channel, "I'll remember that.");
				try {
					//sendMessage(channel, pieces[0]);
					//sendMessage(channel, pieces[1]);
					BufferedWriter writer = new BufferedWriter(new FileWriter("factsin.txt"));
					Set<String> keys = factoids.keySet();
					for (String test: keys)
					{
						writer.write(test);
						writer.newLine();
						writer.write(factoids.get(test));
						writer.newLine();
					}
					writer.close();
			}
			catch (IOException e) {
				
			}
			}
			else if (message.contains(" is "))
			{
				String[] pieces = key.split(" is ");
				//sendMessage(channel, pieces[0]);
				//sendMessage(channel, pieces[1]);
				factoids.put(pieces[0],key);
				sendMessage(channel, "I'll remember that.");
				try {
					//sendMessage(channel, pieces[0]);
					//sendMessage(channel, pieces[1]);
					BufferedWriter writer = new BufferedWriter(new FileWriter("factsin.txt"));
					Set<String> keys = factoids.keySet();
					for (String test: keys)
					{
						writer.write(test);
						writer.newLine();
						writer.write(factoids.get(test));
						writer.newLine();
					}
					writer.close();
			}
			catch (IOException e) {
				
			}
			}
			else if (factoids.containsKey(key.trim())==true)
				sendMessage(channel, factoids.get(key));
			else
				sendMessage(channel, key + "? I don't know what you're going on about...");
			}
		}
		else if (message.toLowerCase().contains("pink underwear")) {
			counter++;
			factor+=15;
			if (!checkTime(channel))
			sendAction(channel, "gags");
			
		}
		else if (message.toLowerCase().contains("underwear")) {
			counter++;
			factor+=10;
			if (!checkTime(channel))
			sendAction(channel, "gags");
			
		}
		else if (message.toLowerCase().contains("pink ") || message.toLowerCase().contains(" pink ") || message.toLowerCase().equals("pink")) {
			counter++;
			factor+=5;
			if (!checkTime(channel))
			sendAction(channel, "gags");
			
		}
		else if (message.toLowerCase().contains("holister")) {
			counter++;
			factor++;
			if (!checkTime(channel))
			sendAction(channel, "gags");
			
		}
		else if (message.toLowerCase().contains("tilly")) {
			counter++;
			factor++;
			if (!checkTime(channel))
			sendAction(channel, "gags");
			
		}
		else if (message.toLowerCase().contains("dord ") || message.toLowerCase().equals("dord ")) {
			counter--;
			factor--;
			sendMessage(channel, sender + ": DoRD is an enwiki checkuser.");
			
		}
		else if (message.toLowerCase().equals(":>") || message.toLowerCase().equals(":^") ||message.toLowerCase().equals(":v")) {
			counter++;
			factor++;
			sendMessage(channel, "Why so Emufarmers?");
			
		}
		else if (message.toLowerCase().equals("derp")) {
			counter--;
			factor--;
			sendMessage(channel, "derp is a LTA banned from #wikipedia, #wikipedia-en, #wikimedia-ops, and #wikimedia-stewards. He is allowed in #wikimedia-commons, #mediawiki, and #wikimedia-tech though.");
			
		}
		else if (message.toLowerCase().contains("crombie")) {
			counter++;
			factor++;
			if (!checkTime(channel))
			sendAction(channel, "gags");
			
		}
		else if (message.toLowerCase().contains("hollister") || message.toLowerCase().contains("h0llister") || message.toLowerCase().contains("ho1lister") || message.toLowerCase().contains("hol1ister") ||message.toLowerCase().contains("ho11ister") || message.toLowerCase().contains("h01lister") || message.toLowerCase().contains("h0l1ister") || message.toLowerCase().contains("h0llister")) {
			counter++;
			factor++;
			if (!checkTime(channel))
			sendAction(channel, "gags");
			
		}
		else if (message.toLowerCase().contains("gift card")) {
			counter++;
			factor++;
			sendAction(channel, "rolls eyes");	
		}
		else if (message.toLowerCase().contains("hco ") || message.toLowerCase().contains(" hco") ||message.toLowerCase().contains("hco.") || message.equalsIgnoreCase("hco")) {
			counter++;
			factor++;
			if (!checkTime(channel))
				sendAction(channel, "gags");
		}
		else if (message.contains("abuse") && message.contains("bot")) {
			sendAction(channel, "frowns");
			factor+=2;
		}
		else if (message.contains("route_bot")) {
			sendAction(channel, "frowns");
			factor+=2;
		}

		else if (message.contains("kick")) {
			factor++;
		}
		else if ((message.startsWith("cant ") || message.contains(" cant")) && isOn(CHECK, channel)) {
			factor+=0.1;
			sendMessage(channel, "*can't");
		}
		else if (message.contains("isnt") && isOn(CHECK, channel)) {
			factor+=0.1;
			sendMessage(channel, "*isn't");
		}
			else if (message.startsWith("ive")&& isOn(CHECK, channel)) {
				factor+=0.1;
				sendMessage(channel, "*I've");
			}
		else if (message.contains("wont")&& isOn(CHECK, channel)) {
			factor+=0.1;
			sendMessage(channel, "*won't");
		}
		else if (message.contains("dont")&& isOn(CHECK, channel)) {
			factor+=0.1;
			sendMessage(channel, "*don't");
		}
		else if (message.contains("didnt")&& isOn(CHECK, channel)) {
			factor+=0.1;
			sendMessage(channel, "*didn't");
		}
		else if (message.contains("havent")&& isOn(CHECK, channel)) {
			factor+=0.1;
			sendMessage(channel, "*haven't");
		}
		else if (message.contains("thats")&& isOn(CHECK, channel)) {
			factor+=0.1;
			sendMessage(channel, "*that's");
		}
        else if (message.startsWith("im ")&& isOn(CHECK, channel)) {
			factor+=0.1;
			sendMessage(channel, "*I'm");
		}
		else if (message.contains("quiet")) {
			factor+=0.5;
		}
		else if (message.contains("bored")) {
			factor+=2;
		}
		else if (message.contains("?")) {
			factor+=0.25;
		}
		else if (message.contains("mall")) {
			factor+=2;
		}
		else if (message.contains("dead")) {
			factor+=1;
		}

		if (message.contains("[[commons:") && message.contains("]]")) {
			int i=0;
			int k=0;
			String output="https://commons.wikimedia.org/wiki/";
			for (boolean j=true;j==true;i++)
			{
				if (message.charAt(i)=='[')
					{j=false;}
			}
			k=i+1;
			for (boolean j=true;j==true;k++)
			{
				if (message.charAt(k)==']')
					{j=false;}
			}
			output = output+message.substring(i+1,k-1);

			output = output.replace(" ","_");
			output = output.replace("commons:","");
			sendMessage(channel, output);
		}
		else if ((message.contains("[[m:") || message.contains("[[meta:")) && message.contains("]]")) {
			int i=0;
			int k=0;
			String output="https://meta.wikimedia.org/wiki/";
			for (boolean j=true;j==true;i++)
			{
				if (message.charAt(i)=='[')
					{j=false;}
			}
			k=i+1;
			for (boolean j=true;j==true;k++)
			{
				if (message.charAt(k)==']')
					{j=false;}
			}
			output = output+message.substring(i+1,k-1);

			output = output.replace(" ","_");
			output = output.replace("m:","");
			output = output.replace("meta:","");
			sendMessage(channel, output);
		}
		else if ((message.contains("[[d:") || message.contains("[[data:")) && message.contains("]]")) {
			int i=0;
			int k=0;
			String output="https://www.wikidata.org/wiki/";
			for (boolean j=true;j==true;i++)
			{
				if (message.charAt(i)=='[')
					{j=false;}
			}
			k=i+1;
			for (boolean j=true;j==true;k++)
			{
				if (message.charAt(k)==']')
					{j=false;}
			}
			output = output+message.substring(i+1,k-1);

			output = output.replace(" ","_");
			output = output.replace("d:","");
			output = output.replace("data:","");
			sendMessage(channel, output);
		}
		else if (message.contains("[[") && message.contains("]]")) {
			if (message.contains("|")) {
				return;
			}
			int i=0;
			int k=0;
			String output="https://en.wikipedia.org/wiki/";
			for (boolean j=true;j==true;i++)
			{
				if (message.charAt(i)=='[')
					{j=false;}
			}
			k=i+1;
			for (boolean j=true;j==true;k++)
			{
				if (message.charAt(k)==']')
					{j=false;}
			}
			output = output+message.substring(i+1,k-1);

			output = output.replace(" ","_");
			sendMessage(channel, output);
		}
		if (message.contains("{{{") && message.contains("}}}")) {
		}
		else if (message.contains("|")) {
			
		}
		else if (message.contains("{{") && message.contains("}}")) {
				int i=0;
				int k=0;
				String output="https://en.wikipedia.org/wiki/Template:";
				for (boolean j=true;j==true;i++)
				{
					if (message.charAt(i)=='{')
						{j=false;}
				}
				k=i+1;
				for (boolean j=true;j==true;k++)
				{
					if (message.charAt(k)=='}')
						{j=false;}
				}
				output = output+message.substring(i+1,k-1);

				output = output.replace(" ","_");
				sendMessage(channel, output);
		}
	
	}
	
	//wrapper function
	public void joinChannel(Channel channel) {
		joinChannel(channel.getName());
	}

}