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

/**
 * 
 * @author huangpeng
 * 
 */
@Entity
@Table(name = "t_ext_workflow_log")
@NamedQueries({ @NamedQuery(name = "WorkflowLog.getByTaskId", query = "from WorkflowLog log where log.taskId = ?"),
		@NamedQuery(name = "WorkflowLog.getByInstanceId", query = "from WorkflowLog log where log.instanceId = ? Order By time ASC ") })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "POJO")
public class WorkflowLog implements Serializable {
	/** */
	private String id;
	/** */
	private String userName;
	/** */
	private String useralias;
	/** */
	private Date time;
	/** */
	private String taskId;
	/** */
	private String taskName;
	/** */
	private String instanceId;
	/** */
	private String detail;

	/**
	 * 
	 * @return String
	 */
	@Id
	@Column(name = "c_id")
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
	@Column(name = "c_username")
	public String getUserName() {
		return userName;
	}

	/**
	 * 
	 * @param userName
	 *            userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * 
	 * @return String
	 */
	@Column(name = "c_useralias")
	public String getUseralias() {
		return useralias;
	}

	/**
	 * 
	 * @param useralias
	 *            useralias
	 */
	public void setUseralias(String useralias) {
		this.useralias = useralias;
	}

	/**
	 * 
	 * @return Date
	 */
	@Column(name = "c_time")
	public Date getTime() {
		return time;
	}

	/**
	 * 
	 * @param time
	 *            time
	 */
	public void setTime(Date time) {
		this.time = time;
	}

	/**
	 * 
	 * @return String
	 */
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
	 * @return String
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
	 * @return String
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
	 * @return String
	 */
	@Column(name = "c_detail")
	public String getDetail() {
		return detail;
	}

	/**
	 * 
	 * @param detail
	 *            detail
	 */
	public void setDetail(String detail) {
		this.detail = detail;
	}

}
