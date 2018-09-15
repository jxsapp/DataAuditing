package smartbi.auditing.repository;

import java.util.List;

import smartbi.repository.AbstractDAO;
import smartbi.repository.DAOModule;

public class WorkflowInstanceDao extends AbstractDAO<WorkflowInstance, String> {
	/**  */
	private static WorkflowInstanceDao instance = new WorkflowInstanceDao();

	/**
	 * 
	 * @return WorkflowInstanceDao
	 */
	public static WorkflowInstanceDao getInstance() {
		return instance;
	}

	/** */
	WorkflowInstanceDao() {
		super(DAOModule.getInstance());
	}

	/**
	 * 
	 * @param id
	 *            id
	 * @return WorkflowInstance
	 */
	public WorkflowInstance getByInstanceId(String id) {
		List<WorkflowInstance> objs = findByNamedQuery("WorkflowInstance.getByInstanceId", id);
		if (objs.size() >= 1) {
			return objs.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param parentId
	 *            parentId
	 * @return List<WorkflowInstance>
	 */
	public List<WorkflowInstance> getChildInstanceByParentId(String parentId) {
		return findByNamedQuery("WorkflowInstance.getChildInstanceByParentId", parentId);
	}

	/**
	 * 
	 * @param state
	 *            state
	 * @return List<WorkflowInstance>
	 */
	public List<WorkflowInstance> getAllInstance(String state) {
		return findByNamedQuery("WorkflowInstance.getAllInstance", state);
	}

	/**
	 * 
	 * @param id
	 *            id
	 * @return WorkflowInstance
	 */
	public WorkflowInstance getByResourceId(String id) {
		List<WorkflowInstance> objs = findByNamedQuery("WorkflowInstance.getByResourceId", id);
		if (objs.size() >= 1) {
			return objs.get(0);
		} else {
			return null;
		}
	}

	
}
