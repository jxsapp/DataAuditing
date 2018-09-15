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

@Entity
@Table(name = "t_ext_workflow_task")
@NamedQueries({
		@NamedQuery(name = "WorkflowTask.getByTaskId", query = "from WorkflowTask task where task.taskId = ?"),
		@NamedQuery(name = "WorkflowTask.getByAssigneeSortByCreateTime", query = "from WorkflowTask task where task.assignee  like ? and task.taskState = ? Order By task.createTime Desc"),
		@NamedQuery(name = "WorkflowTask.getByAssigneeSortByCreateTimeNoneLike", query = "from WorkflowTask task where  task.taskState = ? Order By task.createTime Desc"),
		@NamedQuery(name = "WorkflowTask.getByAssigneeSortByTaskOperateTime", query = "from WorkflowTask task where task.assignee  like ? and task.taskState = ? Order By task.taskOperateTime Desc"),
		@NamedQuery(name = "WorkflowTask.getByAssigneeSortByTaskOperateTimeNoneLike", query = "from WorkflowTask task where  task.taskState = ? Order By task.taskOperateTime Desc"),

		@NamedQuery(name = "WorkflowTask.getByRes", query = "from WorkflowTask task where task.assignee  like ?  and task.taskState = ? and task.resourceId = ?"),
		@NamedQuery(name = "WorkflowTask.getByResNoneLike", query = "from WorkflowTask task where task.taskState = ? and task.resourceId = ?"),
		@NamedQuery(name = "WorkflowTask.getByInstanceId", query = "from WorkflowTask task where task.instanceId = ?"),
		@NamedQuery(name = "WorkflowTask.getCompleteTasksByInstanceId", query = "from WorkflowTask task where task.instanceId = ? and task.taskState = 'complete'") })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "POJO")
public class WorkflowTask implements Serializable {
	/** */
	private String taskId;
	/** */
	private String taskType;
	/** */
	private String assigneeType;
	/** */
	private String assignee;
	/** */
	private Date createTime;
	/** */
	private String instanceId;
	/** */
	private String workflowId;
	/** */
	private String resourceId;
	/** */
	private String taskName;
	/** */
	private String taskState;
	/** */
	private String taskDesc;
	/** */
	private String taskOperator;
	/** */
	private Date taskOperateTime;
	/** */
	private String taskRule;
	/** */
	private String taskSugget;
	/** */
	private String taskActionContent;
	/** */
	private String taskOpt;

	/**
	 * 
	 * @return taskId
	 */
	@Id
	@Column(name = "c_taskid")
	public String getTaskId() {
		return taskId;
	}

	/**
	 * 
	 * @param taskId
	 *            taskId
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * 
	 * @return taskType
	 */
	@Column(name = "c_tasktype")
	public String getTaskType() {
		return taskType;
	}

	/**
	 * 
	 * @param taskType
	 *            taskType
	 */
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	/**
	 * 
	 * @return assigneeType
	 */
	@Column(name = "c_assigneetype")
	public String getAssigneeType() {
		return assigneeType;
	}

	/**
	 * 
	 * @param assigneeType
	 *            assigneeType
	 */
	public void setAssigneeType(String assigneeType) {
		this.assigneeType = assigneeType;
	}

	/**
	 * 
	 * @return assignee
	 */
	@Column(name = "c_assignee")
	public String getAssignee() {
		return assignee;
	}

	/**
	 * 
	 * @param assignee
	 *            assignee
	 */
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	/**
	 * 
	 * @return instanceId
	 */
	@Column(name = "c_instanceid")
	public String getInstanceId() {
		return instanceId;
	}

	/**
	 * 
	 * @param instanceId
	 *            instanceId
	 */
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
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
	 * @param workflowId
	 *            workflowId
	 */
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
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
	 * @param resourceId
	 *            resourceId
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	/**
	 * 
	 * @return taskName
	 */
	@Column(name = "c_taskname")
	public String getTaskName() {
		return taskName;
	}

	/**
	 * 
	 * @param taskName
	 *            taskName
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 * 
	 * @return taskState
	 */
	@Column(name = "c_taskstate")
	public String getTaskState() {
		return taskState;
	}

	/**
	 * 
	 * @param taskState
	 *            taskState
	 */
	public void setTaskState(String taskState) {
		this.taskState = taskState;
	}

	/**
	 * 
	 * @return taskDesc
	 */
	@Column(name = "c_taskdesc")
	public String getTaskDesc() {
		return taskDesc;
	}

	/**
	 * 
	 * @param taskDesc
	 *            taskDesc
	 */
	public void setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
	}

	/**
	 * 
	 * @return taskRule
	 */
	@Column(name = "c_taskrule")
	public String getTaskRule() {
		return taskRule;
	}

	/**
	 * 
	 * @param taskRule
	 *            taskRule
	 */
	public void setTaskRule(String taskRule) {
		this.taskRule = taskRule;
	}

	/**
	 * 
	 * @return taskSugget
	 */
	@Column(name = "c_tasksugget")
	public String getTaskSugget() {
		return taskSugget;
	}

	/**
	 * 
	 * @param taskSugget
	 *            taskSugget
	 */
	public void setTaskSugget(String taskSugget) {
		this.taskSugget = taskSugget;
	}

	/**
	 * 
	 * @return taskActionContent
	 */
	@Column(name = "c_taskactioncontent")
	public String getTaskActionContent() {
		return taskActionContent;
	}

	/**
	 * 
	 * @param taskActionContent
	 *            taskActionContent
	 */
	public void setTaskActionContent(String taskActionContent) {
		this.taskActionContent = taskActionContent;
	}

	/**
	 * 
	 * @return createTime
	 */
	@Column(name = "c_createtime")
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * 
	 * @param createTime
	 *            createTime
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * 
	 * @return taskOperator
	 */
	@Column(name = "c_taskoperator")
	public String getTaskOperator() {
		return taskOperator;
	}

	/**
	 * 
	 * @param taskOperator
	 *            taskOperator
	 */
	public void setTaskOperator(String taskOperator) {
		this.taskOperator = taskOperator;
	}

	/**
	 * 
	 * @return taskOperateTime
	 */
	@Column(name = "c_operatetime")
	public Date getTaskOperateTime() {
		return taskOperateTime;
	}

	/**
	 * 
	 * @param taskOperateTime
	 *            taskOperateTime
	 */
	public void setTaskOperateTime(Date taskOperateTime) {
		this.taskOperateTime = taskOperateTime;
	}

	/**
	 * 
	 * @return taskOpt
	 */
	@Column(name = "c_taskopt")
	public String getTaskOpt() {
		return taskOpt;
	}

	/**
	 * 
	 * @param taskOpt
	 *            taskOpt
	 */
	public void setTaskOpt(String taskOpt) {
		this.taskOpt = taskOpt;
	}

}
