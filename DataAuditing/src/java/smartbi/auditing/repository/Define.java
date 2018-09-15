package smartbi.auditing.repository;

import java.util.List;

/**
 * 
 * @author huangpeng
 *
 */
public class Define {
	/** */
	private String id;
	/** */
	private String name;
	/** */
	private List<Activity> activitys;
	/** */
	private List<Path> paths;

	/**
	 * 
	 * @return id
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
	 * @param name
	 *            name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return List<Activity>
	 */
	public List<Activity> getActivitys() {
		return activitys;
	}

	/**
	 * 
	 * @param activitys
	 *            activitys
	 */
	public void setActivitys(List<Activity> activitys) {
		this.activitys = activitys;
	}

	/**
	 * 
	 * @return List<Path>
	 */
	public List<Path> getPaths() {
		return paths;
	}

	/**
	 * 
	 * @param paths
	 *            paths
	 */
	public void setPaths(List<Path> paths) {
		this.paths = paths;
	}

}
