package smartbi.auditing.repository;

public class Parameter {
	/** */
	private String id;
	/** */
	private String name;
	/** */
	private String value;
	/** */
	private String displayValue;

	/**
	 * 
	 * @return String
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return String
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 
	 * @param value value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 
	 * @return String
	 */
	public String getDisplayValue() {
		return displayValue;
	}

	/**
	 * 
	 * @param displayValue displayValue
	 */
	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}
	
}
