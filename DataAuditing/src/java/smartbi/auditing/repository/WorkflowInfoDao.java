package smartbi.auditing.repository;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import smartbi.auditing.DataAuditingLogType;
import smartbi.auditing.service.ManagerService;
import smartbi.net.sf.json.JSONObject;
import smartbi.oltp.FreeQueryModule;
import smartbi.repository.AbstractDAO;
import smartbi.repository.DAOModule;
import smartbi.repository.OperationLogModule;
import smartbi.state.IStateModule;
import smartbi.usermanager.UserManagerModule;

public class WorkflowInfoDao extends AbstractDAO<WorkflowInfo, String> {
	/** */
	private static WorkflowInfoDao instance = new WorkflowInfoDao();

	/**
	 * 
	 * @return instance
	 */
	public static WorkflowInfoDao getInstance() {
		return instance;
	}

	/** */
	WorkflowInfoDao() {
		super(DAOModule.getInstance());
	}

	/**
	 * 
	 * @return List<WorkflowInfo>
	 */
	public List<WorkflowInfo> getAllInfo() {
		return findByNamedQuery("WorkflowInfo.getAllInfo");
	}

	@Override
	public void deleteById(String id) {
		WorkflowInfo info = load(id);
		delete(info);
	}


	@Override
	public void delete(WorkflowInfo info) {
		//级联删除WorkflowResourceDefine对象
		WorkflowResourceDefineDao.getInstance().deleteByWorkflowId(info.getId());
		WorkflowLogDao workflowLogDao = WorkflowLogDao.getInstance();
		WorkflowTaskDao workflowTaskDao = WorkflowTaskDao.getInstance();
		WorkflowInstanceDao workflowInstanceDao = WorkflowInstanceDao.getInstance();
		WorkflowResourceDao workflowResourceDao	= WorkflowResourceDao.getInstance();
		List<WorkflowResource> resources = workflowResourceDao.getByWorkflowId(info.getId());	
		for (WorkflowResource res : resources) {
			WorkflowInstance ins = workflowInstanceDao.getByResourceId(res.getResourceId());
			List<WorkflowLog> logs = workflowLogDao.getByInstanceId(ins.getInstanceId());
			for (WorkflowLog log : logs) {
				workflowLogDao.deleteById(log.getId());
			}
			List<WorkflowTask> tasks = workflowTaskDao.getByInstanceId(ins.getInstanceId());
			for (WorkflowTask task : tasks) {
				workflowTaskDao.deleteById(task.getTaskId());
			}					
			workflowInstanceDao.deleteById(ins.getInstanceId());
			workflowResourceDao.deleteById(res.getResourceId());
		}		
		//ManagerService.getInstance().deleteSchedule(info);
		try {	
			WorkflowInfo newInfo = new WorkflowInfo();
			PropertyUtils.copyProperties(newInfo, info);
			JSONObject cycleJson = JSONObject.fromString(info.getWorkflowLifeCycle());
			String type = cycleJson.optString("type");
			if (!type.equals("nothing")) {
				ManagerService.getInstance().deleteSchedule(info);
			}
			super.delete(info);
			IStateModule stateModule = UserManagerModule.getInstance().getStateModule();
			String userName = stateModule.getCurrentUser().getName();
			String userAlias = stateModule.getCurrentUser().getAlias();
			String sessionId = stateModule.getSession().getId();
			String detail = "{id:'" + newInfo.getId() + "',name:'" + newInfo.getName() + "',alias:'"
					+ newInfo.getAlias() + "',path:'"
					+ FreeQueryModule.getInstance().getCatalogTreeModule().getCatalogElementFullPath(info.getId())
					+ "'}";
			OperationLogModule.getInstance().addOperationLog(userName, userAlias,
					DataAuditingLogType.ModifyDataAuditingLogType.getMsg(), detail, sessionId, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	



}
