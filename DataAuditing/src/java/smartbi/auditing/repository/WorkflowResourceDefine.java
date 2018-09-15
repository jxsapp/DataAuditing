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

/**
 * 流程定义关联的报表参数
 */
@Entity
@Table(name = "t_ext_workflow_resdefine")
@NamedQueries({
		@NamedQuery(name = "WorkflowResourceDefine.getBySpreadsheetId", query = "from WorkflowResourceDefine rd where rd.spreadsheetId = ?"),
		@NamedQuery(name = "WorkflowResourceDefine.getByWorkflowId", query = "from WorkflowResourceDefine rd where rd.workflowId = ?") })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "POJO")
public class WorkflowResourceDefine implements Serializable {
	/** */
	private String id;
	/** */
	private String spreadsheetId;
	/** */
	private String workflowId;
	/** */
	private String mechanismParameter;
	/** */
	private String otherParameters;
	/** */
	private boolean isBySheet;

	/**
	 * 
	 * @return id
	 */
	@Id
	@Column(name = "c_id")
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
	 * @return mechanismParameter
	 */
	@Column(name = "c_mechanismparam")
	public String getMechanismParameter() {
		return mechanismParameter;
	}

	/**
	 * 
	 * @param mechanismParameter mechanismParameter
	 */
	public void setMechanismParameter(String mechanismParameter) {
		this.mechanismParameter = mechanismParameter;
	}

	/**
	 * 
	 * @return otherParameters otherParameters
	 */
	@Column(name = "c_otherparams")
	@Type(type = "smartbi.repository.StringClobType")
	public String getOtherParameters() {
		return otherParameters;
	}

	/**
	 * 
	 * @param otherParameters otherParameters
	 */
	public void setOtherParameters(String otherParameters) {
		this.otherParameters = otherParameters;
	}

	/**
	 * 
	 * @return isBySheet
	 */
	@Column(name = "c_isbysheet")
	@Type(type = "smartbi.repository.BooleanType")
	public boolean isBySheet() {
		return isBySheet;
	}

	/**
	 * 
	 * @param isBySheet isBySheet
	 */
	public void setBySheet(boolean isBySheet) {
		this.isBySheet = isBySheet;
	}
}
