package smartbi.auditing.repository;

import java.io.Serializable;
import java.util.Date;


public class Instance implements Serializable {
	/** */
	private String instanceName;
	/** */
	private String instanceId;
	/** */
	private String spreadsheetName;
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
	/** */
	private String mechanismParam;
	/** */
	private String otherParams;

	
	/**
	 * 
	 * @return String
	 */
	public String getInstanceName() {
		return instanceName;
	}

	/**
	 * 
	 * @param instanceName instanceName
	 */
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
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
	 * @param instanceId instanceId
	 */
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
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
	 * @param resourceId resourceId
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
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
	 * @return String
	 */
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
	 * @return String
	 */ 
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
	 * @return String
	 */
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
	 * @return Date
	 */
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
	 * @return String
	 */
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
	 * @return String
	 */
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
	 * @return Date
	 */
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
	 * @return String
	 */
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

	/**
	 * 
	 * @return String
	 */
	public String getMechanismParam() {
		return mechanismParam;
	}

	/**
	 * 
	 * @param mechanismParam mechanismParam
	 */
	public void setMechanismParam(String mechanismParam) {
		this.mechanismParam = mechanismParam;
	}

	/**
	 * 
	 * @return String
	 */ 
	public String getOtherParams() {
		return otherParams;
	}

	/**
	 * 
	 * @param otherParams otherParams
	 */
	public void setOtherParams(String otherParams) {
		this.otherParams = otherParams;
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
	
	
	
}
