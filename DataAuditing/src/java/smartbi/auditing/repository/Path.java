package smartbi.auditing.repository;

/**
 * 
 * @author huangpeng
 *
 */
public class Path {
	/** */
	private String id;
	/** */
	private String from;
	/** */
	private String to;
	/** */
	private String desc;

	/**
	 * 
	 * @return String
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 *            id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return String
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * 
	 * @param from
	 *            from
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * 
	 * @return String
	 */
	public String getTo() {
		return to;
	}

	/**
	 * 
	 * @param to
	 *            to
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * 
	 * @return String
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * 
	 * @param desc
	 *            desc
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

}
