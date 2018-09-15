package smartbi.auditing.repository;

import java.util.List;
/**
 * 
 * @author huangpeng
 *
 */
public class Activity {
	/** */
	private String id;
	/** */
	private String type;
	/** */
	private String name;
	/** */
	private String rule;
	/** */
	private String opt;
	/** */
	private Life life;
	/** */
	private List<Assignee> assignee;
	/** */
	private String desc;
	/**
	 * 兼容修改，yes仅发起人可见；sameOrg同机构可见； no所有人可见
	 */
	private String visiableType;
	
	/**
	 * 
	 * @return String
	 */
	public String getVisiableType() {
		return visiableType;
	}

	/**
	 * 
	 * @param visiableType
	 *            orgVisiable
	 */
	public void setVisiableType(String visiableType) {
		this.visiableType = visiableType;
	}
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
	public String getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 *            type
	 */
	public void setType(String type) {
		this.type = type;
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
	 * @return String
	 */
	public String getRule() {
		return rule;
	}

	/**
	 * 
	 * @param rule
	 *            rule
	 */
	public void setRule(String rule) {
		this.rule = rule;
	}

	/**
	 * 
	 * @return List<String>
	 */
	public String getOpt() {
		return opt;
	}

	/**
	 * 
	 * @param opt
	 *            opt
	 */
	public void setOpt(String opt) {
		this.opt = opt;
	}

	/**
	 * 
	 * @return Life
	 */
	public Life getLife() {
		return life;
	}

	/**
	 * 
	 * @param life
	 *            life
	 */
	public void setLife(Life life) {
		this.life = life;
	}

	/**
	 * 
	 * @return List<Assignee>
	 */
	public List<Assignee> getAssignee() {
		return assignee;
	}

	/**
	 * 
	 * @param assignee
	 *            assignee
	 */
	public void setAssignee(List<Assignee> assignee) {
		this.assignee = assignee;
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
