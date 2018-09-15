package smartbi.auditing.repository;

import java.util.List;

import smartbi.repository.AbstractDAO;
import smartbi.repository.DAOModule;

/**
 * 
 * @author huangpeng
 * 
 */
public class WorkflowLogDao extends AbstractDAO<WorkflowLog, String> {

	/** */
	private static WorkflowLogDao instance = new WorkflowLogDao();

	/**
	 * 
	 * @return WorkflowLogDao
	 */
	public static WorkflowLogDao getInstance() {
		return instance;
	}

	/**  */
	WorkflowLogDao() {
		super(DAOModule.getInstance());
	}

	/**
	 * 
	 * @param taskId
	 *            taskId
	 * @return WorkflowLog
	 */
	public WorkflowLog getByTaskId(String taskId) {
		List<WorkflowLog> tasks = findByNamedQuery("WorkflowLog.getByTaskId", taskId);
		if (tasks.size() >= 1) {
			return tasks.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param instanceId
	 *            instanceId
	 * @return List<WorkflowLog>
	 */
	public List<WorkflowLog> getByInstanceId(String instanceId) {
		return findByNamedQuery("WorkflowLog.getByInstanceId", instanceId);
	}

}
