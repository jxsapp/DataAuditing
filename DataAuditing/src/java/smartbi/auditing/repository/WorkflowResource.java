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


/**
 * 流程实例对应的报表状态
 */
@Entity
@Table(name = "t_ext_workflow_resource")
@NamedQueries({
		@NamedQuery(name = "WorkflowRes.getByWorkflowId", query = "from WorkflowResource res where res.workflowId = ?"),
		@NamedQuery(name = "WorkflowRes.getByWorkflowAndParam", query = "from WorkflowResource res where res.workflowId = ? and res.mechanismParam = ? and res.otherParams = ?"),
		@NamedQuery(name = "WorkflowRes.getByWorkflowAndParamNotMySQL", query = "from WorkflowResource res where res.workflowId = ? and res.mechanismParam = ?"),
		@NamedQuery(name = "WorkflowRes.getByMechanismParam", query = "from WorkflowResource res where  res.mechanismParam like ?"),
		@NamedQuery(name = "WorkflowRes.getByOtherParams", query = "from WorkflowResource res where  res.otherParams like ?"),
		@NamedQuery(name = "WorkflowRes.getByOtherParamsNotMySQL", query = "from WorkflowResource res"),
		@NamedQuery(name = "WorkflowRes.getBySpreadSheetAndParam", query = "from WorkflowResource res where res.spreadsheetId = ? and res.mechanismParam = ? and res.otherParams = ?"),
		@NamedQuery(name = "WorkflowRes.getBySpreadSheetAndParamNotMySQL", query = "from WorkflowResource res where res.spreadsheetId = ? and res.mechanismParam = ?") })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "POJO")
public class WorkflowResource implements Serializable {
	/** */
	private String resourceId;
	/** */
	private String spreadsheetId;
	/** */
	private String workflowId;
	/** */
	private String instanceId;
	/** */
	private String mechanismParam;
	/** */
	private String otherParams;

	/**
	 * 
	 * @return resourceId
	 */
	@Id
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
	 * @return spreadsheetId
	 */
	@Column(name = "c_spreadsheetid")
	public String getSpreadsheetId() {
		return spreadsheetId;
	}

	/**
	 * 
	 * @param spreadsheetId spreadsheetId
	 */
	public void setSpreadsheetId(String spreadsheetId) {
		this.spreadsheetId = spreadsheetId;
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
	 * @return String
	 */
	@Column(name = "c_mechanismparam")
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
	@Column(name = "c_otherparams")	
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

	



}
