package smartbi.auditing.repository;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import smartbi.util.JSONFields;

/**
 * 
 * 流程实例表
 */
@Entity
@Table(name = "t_ext_workflow_instance")
@NamedQueries({ 
	@NamedQuery(name = "WorkflowInstance.getByInstanceId", query = "from WorkflowInstance ins where ins.instanceId = ?"),
	@NamedQuery(name = "WorkflowInstance.getChildInstanceByParentId", query = "from WorkflowInstance ins where ins.parentInstanceId = ?"),
	@NamedQuery(name = "WorkflowInstance.getAllInstance", query = "from WorkflowInstance ins where ins.instanceState = ?"),
	@NamedQuery(name = "WorkflowInstance.getByResourceId", query = "from WorkflowInstance ins where ins.resourceId = ?") 
	})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "POJO")
@JSONFields(fields = { "instanceId", "resourceId", "workflowId", "instanceDesc", "activity", "instanceCreateDate",
		"instanceState", "instanceCreatorId", "completeFlag", "completeDate" })
public class WorkflowInstance implements Serializable {
	/** */
	private String instanceId;
	/** */
	private String resourceId;
	/** */
	private String workflowId;
	/** */
	private String instanceDesc;
	/** */
	private String activity;
	/** */
	private String instanceState;
	/** */
	private Date instanceCreateDate;
	/** */
	private String instanceCreatorId;
	/** */
	private String completeFlag;
	/** */
	private Date completeDate;
	/** */
	private String  parentInstanceId;

	/**
	 * 
	 * @return instanceId
	 */
	@Id
	@Column(name = "c_instanceid")
	public String getInstanceId() {
		return instanceId;
	}

	/**
	 * 
	 * @param instanceId instanceId
	 */
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	/**
	 * 
	 * @return resourceId
	 */
	@Column(name = "c_resourceid")
	public String getResourceId() {
		return resourceId;
	}

	/**
	 * 
	 * @param resourceId resourceId
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	/**
	 * 
	 * @return workflowId
	 */
	@Column(name = "c_workflowid")
	public String getWorkflowId() {
		return workflowId;
	}

	/**
	 * 
	 * @param workflowId workflowId
	 */
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	/**
	 * 
	 * @return instanceDesc
	 */
	@Column(name = "c_instancedesc")
	public String getInstanceDesc() {
		return instanceDesc;
	}

	/**
	 * 
	 * @param instanceDesc instanceDesc
	 */
	public void setInstanceDesc(String instanceDesc) {
		this.instanceDesc = instanceDesc;
	}

	/**
	 * 
	 * @return activity
	 */
	@Column(name = "c_activity")
	public String getActivity() {
		return activity;
	}

	/**
	 * 
	 * @param activity activity
	 */
	public void setActivity(String activity) {
		this.activity = activity;
	}

	/**
	 * 
	 * @return instanceCreateDate
	 */
	@Column(name = "c_instancecreatedate")
	public Date getInstanceCreateDate() {
		return instanceCreateDate;
	}

	/**
	 * 
	 * @param instanceCreateDate instanceCreateDate
	 */
	public void setInstanceCreateDate(Date instanceCreateDate) {
		this.instanceCreateDate = instanceCreateDate;
	}

	/**
	 * 
	 * @return instanceState
	 */
	@Column(name = "c_instancestate")
	public String getInstanceState() {
		return instanceState;
	}

	/**
	 * 
	 * @param instanceState instanceState
	 */
	public void setInstanceState(String instanceState) {
		this.instanceState = instanceState;
	}

	/**
	 * 
	 * @return instanceCreatorId
	 */
	@Column(name = "c_instancecreatorid")
	public String getInstanceCreatorId() {
		return instanceCreatorId;
	}

	/**
	 * 
	 * @param instanceCreatorId instanceCreatorId
	 */
	public void setInstanceCreatorId(String instanceCreatorId) {
		this.instanceCreatorId = instanceCreatorId;
	}

	/**
	 * 
	 * @return completeFlag
	 */
	@Column(name = "c_completeflag")
	public String getCompleteFlag() {
		return completeFlag;
	}

	/**
	 * 
	 * @param completeFlag completeFlag
	 */
	public void setCompleteFlag(String completeFlag) {
		this.completeFlag = completeFlag;
	}

	/**
	 * 
	 * @return completeDate
	 */
	@Column(name = "c_completedate")
	public Date getCompleteDate() {
		return completeDate;
	}

	/**
	 * 
	 * @param completeDate completeDate
	 */
	public void setCompleteDate(Date completeDate) {
		this.completeDate = completeDate;
	}

	/**
	 * 
	 * @return parentInstanceId
	 */
	@Column(name = "c_parentinstanceid")
	public String getParentInstanceId() {
		return parentInstanceId;
	}

	/**
	 * 
	 * @param parentInstanceId parentInstanceId
	 */
	public void setParentInstanceId(String parentInstanceId) {
		this.parentInstanceId = parentInstanceId;
	}
	
	
}
