public class Trigger {
	private String key;
	private boolean action;
	private String value;
	
	public String getKey() {
		return key;
	}
	
	public boolean isAction() {
		return action;
	}
	
	public String getValue() {
		return value;
	}
	
	public Trigger(String key, boolean action, String value) {
		this.key = key;
		this.action = action;
		this.value = value;
	}
}
