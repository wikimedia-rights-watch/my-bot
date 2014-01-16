public class Trigger {
	private String key;
	private boolean action;
	private String value;
	private boolean bad;
	
	public String getKey() {
		return key;
	}
	
	public boolean isAction() {
		return action;
	}
	
	public String getValue() {
		return value;
	}
	
	public boolean isBad() {
		return bad;
	}
	
	public Trigger(String key, boolean action, String value, boolean bad) {
		this.key = key;
		this.action = action;
		this.value = value;
		this.bad = bad;
	}
}
