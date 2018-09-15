package smartbi.auditing.repository;

import java.util.Date;

public class TaskResource {
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
	private String workflowName;
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
	/** */
	private String spreadsheetId;
	/** */
	private String spreadsheetName;
	/** */
	private String mechanismParam;
	/** */
	private String otherParams;

	/**
	 * 
	 * @return String
	 */
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
	 * @return String
	 */
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
	 * @return String
	 */
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
	 * @return String
	 */
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
	 * @return Date
	 */
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
	 * @return String
	 */
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
	 * @return String
	 */
	public String getWorkflowName() {
		return workflowName;
	}

	/**
	 * 
	 * @param workflowName workflowName
	 */
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	/**
	 * 
	 * @return String
	 */
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
	 * @return String
	 */
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
	 * @return String
	 */
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
	 * @return String
	 */
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
	 * @return String
	 */
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
	 * @return Date
	 */
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
	 * @return String
	 */
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
	 * @return String
	 */
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
	 * @return String
	 */
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
	 * @return String
	 */
	public String getTaskOpt() {
		return taskOpt;
	}

	/**
	 * 
	 * @return String
	 */
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
	 * @param taskOpt
	 *            taskOpt
	 */
	public void setTaskOpt(String taskOpt) {
		this.taskOpt = taskOpt;
	}

	/**
	 * 
	 * @return String
	 */
	public String getSpreadsheetId() {
		return spreadsheetId;
	}

	/**
	 * 
	 * @param spreadsheetId
	 *            spreadsheetId
	 */
	public void setSpreadsheetId(String spreadsheetId) {
		this.spreadsheetId = spreadsheetId;
	}

	/**
	 * 
	 * @return String
	 */
	public String getSpreadsheetName() {
		return spreadsheetName;
	}

	/**
	 * 
	 * @param spreadsheetName spreadsheetName
	 */
	public void setSpreadsheetName(String spreadsheetName) {
		this.spreadsheetName = spreadsheetName;
	}

	/**
	 * 
	 * @return String
	 */
	public String getMechanismParam() {
		return mechanismParam;
	}

	/**
	 * 
	 * @param mechanismParam
	 *            mechanismParam
	 */
	public void setMechanismParam(String mechanismParam) {
		this.mechanismParam = mechanismParam;
	}

	/***
	 * 
	 * @return String
	 */
	public String getOtherParams() {
		return otherParams;
	}

	/**
	 * 
	 * @param otherParams
	 *            otherParams
	 */
	public void setOtherParams(String otherParams) {
		this.otherParams = otherParams;
	}

}
