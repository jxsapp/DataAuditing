package smartbi.auditing.repository;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import smartbi.repository.IEditableResource;

/**
 * 
 * @author huangpeng 流程定义表
 */
@Entity
@Table(name = "t_ext_workflow_info")
@NamedQueries({ @NamedQuery(name = "WorkflowInfo.getAllInfo", query = "from WorkflowInfo") })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "POJO")
public class WorkflowInfo implements IEditableResource, Serializable {
	
	/** */
	private String id;
	/** */
	private String name;
	/** */
	private String desc;
	/** */
	private String alias;
	/** */
	private String workflowType;
	/** */
	private String workflowLifeCycle;
	/** */
	private Integer workflowInstanceNub;
	/** */
	private String workflowInitiateRole;
	/** */
	private String workflowDefine;

	/**
	 * @return String
	 */
	@Id
	@Column(name = "c_workflowid")
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return String
	 */
	@Column(name = "c_workflowname")
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return String
	 */
	@Column(name = "c_workflowalias")
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias
	 *            alias
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * @return String
	 */
	@Column(name = "c_workflowdesc")
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc
	 *            desc
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @return String
	 */
	@Column(name = "c_workflowtype")
	public String getWorkflowType() {
		return workflowType;
	}

	/**
	 * @param workflowType
	 *            workflowType
	 */
	public void setWorkflowType(String workflowType) {
		this.workflowType = workflowType;
	}

	/**
	 * @return String
	 */
	@Column(name = "c_workflowlifecycle")
	public String getWorkflowLifeCycle() {
		return workflowLifeCycle;
	}

	/**
	 * @param workflowLifeCycle
	 *            workflowLifeCycle
	 */
	public void setWorkflowLifeCycle(String workflowLifeCycle) {
		this.workflowLifeCycle = workflowLifeCycle;
	}

	/**
	 * @return String
	 */
	@Column(name = "c_workflowinstances")
	public Integer getWorkflowInstanceNub() {
		return workflowInstanceNub;
	}

	/**
	 * @param workflowInstanceNub
	 *            workflowInstanceNub
	 */
	public void setWorkflowInstanceNub(Integer workflowInstanceNub) {
		this.workflowInstanceNub = workflowInstanceNub;
	}

	/**
	 * @return String
	 */
	@Column(name = "c_workflowinitiaterole")
	public String getWorkflowInitiateRole() {
		return workflowInitiateRole;
	}

	/**
	 * @param workflowInitiateRole
	 *            workflowInitiateRole
	 */
	public void setWorkflowInitiateRole(String workflowInitiateRole) {
		this.workflowInitiateRole = workflowInitiateRole;
	}

	/**
	 * @return String
	 */
	@Column(name = "c_workflowdefine")
	@Type(type = "smartbi.repository.StringClobType")
	public String getWorkflowDefine() {
		return workflowDefine;
	}

	/**
	 * @param workflowDefine
	 *            workflowDefine
	 */
	public void setWorkflowDefine(String workflowDefine) {
		this.workflowDefine = workflowDefine;
	}

}
