package smartbi.auditing.repository;

import java.util.List;

import smartbi.repository.AbstractDAO;
import smartbi.repository.DAOModule;

public class WorkflowResourceDefineDao extends AbstractDAO<WorkflowResourceDefine, String> {
	/** */
	private static WorkflowResourceDefineDao instance = new WorkflowResourceDefineDao();

	/**
	 * @return instance
	 */
	public static WorkflowResourceDefineDao getInstance() {
		return instance;
	}

	/** */
	WorkflowResourceDefineDao() {
		super(DAOModule.getInstance());
	}

	/**
	 * 
	 * @param id id
	 */
	public void deleteBySpreadsheetId(String id) {
		List<WorkflowResourceDefine> objs = findByNamedQuery("WorkflowResourceDefine.getBySpreadsheetId", id);
		this.deleteAll(objs);
	}
	
	/**
	 * 
	 * @param id id
	 * @return WorkflowResourceDefine
	 */
	public WorkflowResourceDefine getBySpreadsheetId(String id) {
		List<WorkflowResourceDefine> objs = findByNamedQuery("WorkflowResourceDefine.getBySpreadsheetId", id);
		if (objs.size() >= 1) {
			return objs.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param id id
	 * @return WorkflowResourceDefine
	 */
	public List<WorkflowResourceDefine> getByWorkflowId(String id) {
		List<WorkflowResourceDefine> objList = findByNamedQuery("WorkflowResourceDefine.getByWorkflowId", id);
		return objList;
	}

	/**
	 * 删除
	 * @param id id
	 */
	public void deleteByWorkflowId(String id) {
		List<WorkflowResourceDefine> objList = findByNamedQuery("WorkflowResourceDefine.getByWorkflowId", id);
		deleteAll(objList);
	}
}
