package smartbi.auditing.repository;

import java.util.Date;

public class TaskLog {
	/** */
	private String id;
	/** */
	private String userName;
	/** */
	private String userAlias;
	/** */
	private String detail;
	/** */
	private Date time;
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
	public String getUserName() {
		return userName;
	}
	
	/**
	 * 
	 * @param userName userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * 
	 * @return String
	 */
	public String getUserAlias() {
		return userAlias;
	}
	
	/**
	 * 
	 * @param userAlias userAlias
	 */
	public void setUserAlias(String userAlias) {
		this.userAlias = userAlias;
	}
	
	/**
	 * 
	 * @return String
	 */
	public String getDetail() {
		return detail;
	}
	
	/**
	 * 
	 * @param detail detail
	 */
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	/**
	 * 
	 * @return Date
	 */
	public Date getTime() {
		return time;
	}
	
	/**
	 * 
	 * @param time time
	 */
	public void setTime(Date time) {
		this.time = time;
	}
	
	
}
