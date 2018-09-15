package smartbi.auditing.repository;

import java.util.ArrayList;
import java.util.List;


import smartbi.repository.AbstractDAO;
import smartbi.repository.DAOModule;
import smartbi.util.DBType;
import smartbi.util.StringUtil;

/**
 * 
 * @author huangpeng
 * 
 */
public class WorkflowTaskDao extends AbstractDAO<WorkflowTask, String> {
	/** */
	private static WorkflowTaskDao instance = new WorkflowTaskDao();

	/**
	 * 
	 * @return WorkflowTaskDao
	 */
	public static WorkflowTaskDao getInstance() {
		return instance;
	}

	/** */
	WorkflowTaskDao() {
		super(DAOModule.getInstance());
	}

	/**
	 * 
	 * @param taskId
	 *            taskId
	 * @return WorkflowTask
	 */
	public WorkflowTask getByTaskId(String taskId) {
		List<WorkflowTask> tasks = findByNamedQuery("WorkflowTask.getByTaskId", taskId);
		if (tasks.size() >= 1) {
			return tasks.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param assignee
	 *            assignee
	 * @param state
	 *            state
	 * @param resId
	 *            resId
	 * @return WorkflowTask
	 */
	public WorkflowTask getByRes(String assignee, String state, String resId) {
		DBType dbType = DAOModule.getInstance().getRepository().getDatabaseType();
		//gbase8t不支持大文本字段不支持like查询 所以大文本like查询变为在查完之后过滤
		if (dbType == DBType.GBASE8T) {
			List<WorkflowTask> tasks = findByNamedQuery("WorkflowTask.getByResNoneLike", state, resId);
			if ((tasks == null || tasks.size() == 0)) {
				return null;
			}
			// 如果assignee 为空则返回第一个
			if (StringUtil.isNullOrEmpty(assignee)) {
				return tasks.get(0);
			}
			// 模拟like查询查找第一个对象
			for (WorkflowTask task : tasks) {
				if (task.getAssignee() != null && task.getAssignee().contains(assignee)) {
					return task;
				}
			}
			return null;
		} else {
			if (StringUtil.isNullOrEmpty(assignee)) {
				assignee = "";
			}
			assignee = "%" + assignee + "%";
			List<WorkflowTask> tasks = findByNamedQuery("WorkflowTask.getByRes", assignee, state, resId);
			if (tasks != null && tasks.size() >= 1) {
				return tasks.get(0);
			} else {
				return null;
			}
		}
	}

	/**
	 * 
	 * @param assignee
	 *            assignee
	 * @param state
	 *            state
	 * @return List<WorkflowTask>
	 */
	public List<WorkflowTask> getByAssignee(String assignee, String state) {
		DBType dbType = DAOModule.getInstance().getRepository().getDatabaseType();
		List<WorkflowTask> result = new ArrayList<WorkflowTask>();
		if (assignee == null) {
			assignee = "";
		}
		String likeAssignee = "%" +  assignee + "%";
		if (state.equals("unfinished")) {
			//Gbase8t大文本不支持like查询
			if (dbType == DBType.GBASE8T) {
				List<WorkflowTask> list = findByNamedQuery("WorkflowTask.getByAssigneeSortByCreateTimeNoneLike", state);
				// 做like筛选
				for (WorkflowTask task : list) {
					if (task.getAssignee() != null && task.getAssignee().contains(assignee)) {
						result.add(task);
					}
				}
			} else {
				
				result = findByNamedQuery("WorkflowTask.getByAssigneeSortByCreateTime", likeAssignee, state);
			}
		} else {
			if (dbType == DBType.GBASE8T) {
				List<WorkflowTask> list = findByNamedQuery("WorkflowTask.getByAssigneeSortByTaskOperateTimeNoneLike", state);
				// 做like筛选
				for (WorkflowTask task : list) {
					if (task.getAssignee() != null && task.getAssignee().contains(assignee)) {
						result.add(task);
					}
				}
			} else {
				result = findByNamedQuery("WorkflowTask.getByAssigneeSortByTaskOperateTime", likeAssignee, state);
			}
		}
		return result;

	}

	/**
	 * 
	 * @param instanceId
	 *            instanceId
	 * @return List<WorkflowTask>
	 */
	public List<WorkflowTask> getByInstanceId(String instanceId) {
		return findByNamedQuery("WorkflowTask.getByInstanceId", instanceId);
	}

	/**
	 * 
	 * @param instanceId
	 *            instanceId
	 * @return List<WorkflowTask>
	 */
	public List<WorkflowTask> getCompleteTasksByInstanceId(String instanceId) {
		return findByNamedQuery("WorkflowTask.getCompleteTasksByInstanceId", instanceId);
	}

}
