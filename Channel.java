public class Channel {
	private String name;
	private boolean master;
	
	public static ArrayList<Trigger> triggerList;
	
	public Channel(String name, boolean master) {
		this.name=name;
		this.master=master;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name=name;
	}
	
	public boolean equals(Channel other) {
		return name.equals(other.name);
	}
	
	public boolean isMaster() {
		return master;
	}
	
}
