package smartbi.auditing.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import smartbi.SmartbiException;
import smartbi.auditing.DataAuditingErrorCode;
import smartbi.auditing.repository.Activity;
import smartbi.auditing.repository.Assignee;
import smartbi.auditing.repository.Define;
import smartbi.auditing.repository.Instance;
import smartbi.auditing.repository.Path;
import smartbi.auditing.repository.SummaryGroup;
import smartbi.auditing.repository.WorkflowInfo;
import smartbi.auditing.repository.WorkflowInfoDao;
import smartbi.auditing.repository.WorkflowInstance;
import smartbi.auditing.repository.WorkflowInstanceDao;
import smartbi.auditing.repository.WorkflowResource;
import smartbi.auditing.repository.WorkflowResourceDao;
import smartbi.auditing.repository.WorkflowTask;
import smartbi.auditing.repository.WorkflowTaskDao;
import smartbi.catalogtree.CatalogElement;
import smartbi.catalogtree.CatalogTreeModule;
import smartbi.framework.IModule;
import smartbi.freequery.client.simplereport.ClientReportService;
import smartbi.freequery.client.simplereport.ClientReportView;
import smartbi.freequery.metadata.BusinessViewBO;
import smartbi.freequery.metadata.RawSqlQuery;
import smartbi.freequery.querydata.GridData;
import smartbi.freequery.report.SimpleReportBO;
import smartbi.freequery.util.QueryRelationType;
import smartbi.net.sf.json.JSONArray;
import smartbi.net.sf.json.JSONObject;
import smartbi.repository.IDAOModule;
import smartbi.state.IStateModule;
import smartbi.user.IUser;
import smartbi.usermanager.Group;
import smartbi.usermanager.GroupBO;
import smartbi.usermanager.Role;
import smartbi.usermanager.User;
import smartbi.usermanager.UserManager;
import smartbi.usermanager.UserManagerModule;
import smartbi.util.StringUtil;
import smartbi.util.UUIDGenerator;

/**
 * 
 * @author huangpeng
 * 
 */
public class RuntimeService implements IModule {
	/** */
	private static Logger log = Logger.getLogger(RuntimeService.class);
	/** */
	private static RuntimeService instance;
	/** */
	private TaskService taskService;
	/** */
	private RepositoryService repositoryService;
	/** */
	private WorkflowInstanceDao instanceDao = WorkflowInstanceDao.getInstance();
	/** */
	private UserManager userManager;
	/** */
	private IStateModule stateModule;
	/** */
	private WorkflowInfoDao infoDao = WorkflowInfoDao.getInstance();
	/** */
	private WorkflowTaskDao taskDao = WorkflowTaskDao.getInstance();
	/** */
	private IDAOModule daoModule;

	/**
	 * 
	 * @return instance
	 */
	public static RuntimeService getInstance() {
		if (instance == null) {
			instance = new RuntimeService();
		}		
		return instance;
	}

	/**
	 * 
	 */
	protected RuntimeService() {
	}

	/**
	 * 
	 */
	public void activate() {
		daoModule.addPOJOClass(WorkflowInstance.class);
	}

	/**
	 * 
	 * @return daoModule
	 */
	public IDAOModule getDaoModule() {
		return daoModule;
	}

	/**
	 * 
	 * @param daoModule
	 *            daoModule
	 */
	public void setDaoModule(IDAOModule daoModule) {
		this.daoModule = daoModule;
	}

	/**
	 * 
	 * @return taskService
	 */
	public TaskService getTaskService() {
		return taskService;
	}

	/**
	 * 
	 * @param taskService
	 *            taskService
	 */
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * 
	 * @return repositoryService
	 */
	public RepositoryService getRepositoryService() {
		return repositoryService;
	}

	/**
	 * 
	 * @param repositoryService
	 *            repositoryService
	 */
	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}


	/**
	 * 启动流程实例
	 * @param infoId infoId
	 * @param spreadsheetId spreadsheetId
	 * @param mechanismParmasStr mechanismParmasStr
	 * @param otherParmasStr otherParmasStr
	 * @param userId userId
	 * @return WorkflowInstance
	 */
	public WorkflowInstance startInstanceById(String infoId, String spreadsheetId, String mechanismParmasStr,
			String otherParmasStr, String userId) {
		WorkflowInstance startIns = this.getInsBySpreadSheetAndParam(spreadsheetId, mechanismParmasStr, otherParmasStr);
		//如果流程实例已经启动过，则不再启动
		if (startIns != null) {
			return null;
		}
		WorkflowInfo info = repositoryService.getInfo(infoId);
		WorkflowResourceDao workflowResourceDao = WorkflowResourceDao.getInstance();
		userManager = UserManager.getInstance();
		WorkflowResource res = new WorkflowResource();
		res.setResourceId(UUIDGenerator.generate());
		res.setWorkflowId(infoId);
		res.setSpreadsheetId(spreadsheetId);
		res.setMechanismParam(mechanismParmasStr);
		res.setOtherParams(otherParmasStr);
		// res.setParameters(params);
		workflowResourceDao.save(res);
		Define define = repositoryService.parseDefine(infoId, info.getWorkflowDefine());
		Activity act = getStartActivity(define);
		WorkflowInstance ins = new WorkflowInstance();
		ins.setInstanceId(UUIDGenerator.generate());
		ins.setWorkflowId(infoId);
		ins.setResourceId(res.getResourceId());
		ins.setInstanceState("running");
		ins.setInstanceCreateDate(new Date());
		ins.setInstanceCreatorId(userId);
		ins.setActivity(act.getId());
		instanceDao.save(ins);
		nextActivity(ins.getInstanceId());
		return ins;
	}

	/**
	 * 通过计划任务启动流程
	 * @param workflowId
	 *            workflowId
	 */
	public void startInstanceBySchedule(String workflowId) {
		ManagerService.getInstance().startInstanceBySchedule(workflowId);
	}

	/**
	 * 启动流程父实例
	 * @param infoId infoId
	 * @param resId resId
	 * @param childInstanceId childInstanceId
	 * @param insCreatorId insCreatorId
	 * @param act act
	 * @return WorkflowInstance
	 */
	public WorkflowInstance startParentIns(String infoId, String resId, String childInstanceId, String insCreatorId,
			Activity act) {
		WorkflowInfo info = repositoryService.getInfo(infoId);
		WorkflowResourceDao workflowResourceDao = WorkflowResourceDao.getInstance();
		UserManagerModule userManagerModule = UserManagerModule.getInstance();
		List<Assignee> assis = act.getAssignee();
		Assignee assi = assis.get(0);
		List<Group> groups = new ArrayList<Group>();
		Set<IUser> users = new HashSet<IUser>();
		if (assi.getType().equals("USER")) {
			User user = userManagerModule.getUserById(assi.getValue());
			if (user != null) {
				users.add(user);
			}
		} else if (assi.getType().equals("ROLE")) {
			// 获得角色下所有用户
			users = (Set<IUser>) userManagerModule.getAllUsersOfRole(assi.getValue());
		} else if (assi.getType().equals("GROUP")) {
			// 获得组下所有用户
			users = new HashSet<IUser>(userManagerModule.getUsersOfGroup(assi.getValue()));
		}
		if (users.size() == 0) {
			throw new SmartbiException(DataAuditingErrorCode.OPERATER_IS_EMPTY);
		}
		for (IUser user : users) {
			List<String> groupsStr = userManagerModule.getDefaultGroups(user.getId());
			for (String groupStr : groupsStr) {
				Group tgroup = userManagerModule.getGroupById(groupStr);
				if (!groups.contains(tgroup)) {
					groups.add(tgroup);
				}
			}
		}
		// 查找角色所在用户组是否在机构树中
		List<String> groupStrs = userManagerModule.getDefaultGroups(insCreatorId);
		// 创建用户所在默认组
		Group creatorGroup = userManagerModule.getGroupById(groupStrs.get(0));
		Group parentGroup = null;
		for (Group group : groups) {
			boolean flg = TaskService.getInstance().findGroupToParent(creatorGroup, group);
			if (flg) {
				parentGroup = group;
			}
		}
		WorkflowResource res = workflowResourceDao.load(resId);
		JSONArray parentMechanismParmasJson = JSONArray.fromString(res.getMechanismParam());
		if (parentMechanismParmasJson.length() < 1) {
			throw new SmartbiException(DataAuditingErrorCode.SUMMARY_PARAM_IS_EMPTY);
		}
		JSONObject parentMechanismParmaObject = (JSONObject) parentMechanismParmasJson.get(0);
		parentMechanismParmaObject.put("value", parentGroup.getDepartmentCode());
		parentMechanismParmaObject.put("displayValue", parentGroup.getName());
		String parentMechanismParmasStr = parentMechanismParmasJson.toString();
		// 判断当前流程是否已启动过父流程
		WorkflowInstance pIns = this.getInsBySpreadSheetAndParam(res.getSpreadsheetId(), parentMechanismParmasStr,
				res.getOtherParams());
		Define define = repositoryService.parseDefine(infoId, info.getWorkflowDefine());
		if (pIns != null) {
			// 如果父节点已经激活，则不再激活
			if (pIns.getActivity().equals("")) {
				pIns.setActivity(act.getId());
				if (!act.getType().equals("end")) {
					List<WorkflowTask> tasks = assignTask(pIns.getInstanceId(), info.getId(),
							pIns.getResourceId(), act, define);
					TaskService.getInstance().addOperationLogByTaskName(tasks.get(0).getTaskId(), "已有流程进入汇总",
							"已有流程进入汇总");
				}
			}
			return pIns;
		}
		WorkflowResource parentRes = new WorkflowResource();
		parentRes.setResourceId(UUIDGenerator.generate());
		parentRes.setWorkflowId(infoId);
		parentRes.setSpreadsheetId(res.getSpreadsheetId());
		parentRes.setMechanismParam(parentMechanismParmasStr);
		parentRes.setOtherParams(res.getOtherParams());
		workflowResourceDao.save(parentRes);
		WorkflowInstance ins = new WorkflowInstance();
		ins.setInstanceId(UUIDGenerator.generate());
		ins.setWorkflowId(infoId);
		ins.setResourceId(parentRes.getResourceId());
		ins.setInstanceState("running");
		ins.setInstanceCreateDate(new Date());
		ins.setInstanceCreatorId(insCreatorId);
		ins.setActivity(act.getId());
		instanceDao.save(ins);
		if (!act.getType().equals("end")) {
			List<WorkflowTask> tasks = assignTask(ins.getInstanceId(), info.getId(), ins.getResourceId(), act, define);
			TaskService.getInstance().addOperationLogByTaskName(tasks.get(0).getTaskId(), "已有流程进入汇总", "已有流程进入汇总");
		}
		return ins;

	}


	/**
	 * 汇总
	 * @param instanceId
	 *            instanceId
	 * @param currentGroupId
	 *            currentGroupId
	 * @return List<SummaryGroup>
	 */
	public List<SummaryGroup> gatherInstance(String instanceId, String currentGroupId) {
		userManager = UserManager.getInstance();
		UserManagerModule userManagerModule  = UserManagerModule.getInstance();
		GroupBO currentGroup = userManager.getGroupByCode(currentGroupId);

		List<WorkflowInstance> childInss = this.instanceDao.getChildInstanceByParentId(instanceId);
		WorkflowInstance childIns = childInss.get(0);
		// 获得子流程实例的用户
		WorkflowTask ctask = this.taskDao.getByInstanceId(childIns.getInstanceId()).get(0);
		String operatorId = ctask.getTaskOperator();
		// 查找当前环节的用户和子流程用户的层级关系
		List<String> groupsOfopt = (List<String>) userManagerModule.getDefaultGroups(operatorId);
		// 子层级所处组
		List<Group> allSubGroups = new ArrayList<Group>();
		for (String groupStr : groupsOfopt) {
			Group group = userManagerModule.getGroupById(groupStr);
			int level = TaskService.getInstance().findLevelGroupToSub(currentGroup.toGroup(), group, 0);
			if (level > 0) {
				TaskService.getInstance().getSubGroupsByLevel(allSubGroups, currentGroup.toGroup(), level);
			}
		}
		// 处理子层级所处组没有分配节点的组
		// 获得前一层级的环节角色
		WorkflowInstance tins = this.instanceDao.load(instanceId);
		WorkflowInfo tinfo = repositoryService.getInfo(childIns.getWorkflowId());
		Define tdefine = repositoryService.parseDefine(childIns.getWorkflowId(), tinfo.getWorkflowDefine());
		Activity act = getPreActivity(tins.getActivity(), tdefine);
		List<Assignee> assis = act.getAssignee();
		Assignee assi = assis.get(0);
		String role = assi.getValue();
		List<Group> rgroups = new ArrayList<Group>();
		for (Group tgroup : allSubGroups) {
			boolean tflg = false;
			List<User> usrs = (List<User>) userManagerModule.getUsersOfGroup(tgroup.getId());
			for (User usr : usrs) {
				List<Role> assignedRoles = usr.getAssignedRoles();
				for (Role trole : assignedRoles) {
					if (trole.getId().equals(role)) {
						tflg = true;
					}
				}
			}
			if (!tflg) {
				rgroups.add(tgroup);
			}
		}
		for (Group rg : rgroups) {
			allSubGroups.remove(rg);
		}
		int sum = 0;
		List<SummaryGroup> summaryGroups = new ArrayList<SummaryGroup>();
		for (Group group : allSubGroups) {
			
			SummaryGroup sgroup = new SummaryGroup();
			sgroup.setId(group.getId());
			sgroup.setName(group.getName());
			sgroup.setDesc(group.getDesc());
			sgroup.setFlg("unfinished");
			sgroup.setOrgId(group.getOrgId());
			summaryGroups.add(sgroup);
		}

		for (WorkflowInstance tchildIns : childInss) {
			WorkflowInfo info = this.repositoryService.getInfo(tchildIns.getWorkflowId());
			Define define = repositoryService.parseDefine(tchildIns.getWorkflowId(), info.getWorkflowDefine());
			if (tchildIns.getActivity().equals("")) {
				WorkflowResource res = WorkflowResourceDao.getInstance().load(tchildIns.getResourceId());
				JSONArray mjson = JSONArray.fromString(res.getMechanismParam());
				JSONObject mobj = (JSONObject) mjson.get(0);
				String mechanismStr = mobj.optString("value");
				for (SummaryGroup sgroup : summaryGroups) {
					if (sgroup.getOrgId().equals(mechanismStr)) {
						sgroup.setFlg("finish");
					}
				}
			}
		}
		return summaryGroups;
	}


	/**
	 *  派遣任务
	 * @param instanceId instanceId
	 * @param opt opt
	 */
	public void dispatch(String instanceId, String opt) {
		WorkflowInstance ins = instanceDao.getByInstanceId(instanceId);
		List<WorkflowTask> tasks = taskDao.getByInstanceId(instanceId);
		if (opt.equals("complete")) {
			boolean complteteFlg = true;
			// 考虑以后扩展会签功能
			for (WorkflowTask task : tasks) {
				if (task.getTaskState().equals("unfinished")) {
					complteteFlg = false;
					break;
				}
			}
			if (complteteFlg) {
				nextActivity(instanceId);
			}
		}
		if (opt.equals("backToPre")) {
			preActivity(instanceId);
		}
		if (opt.equals("backToStart")) {
			toStart(instanceId);
		}

	}

	/**
	 * 获得流程实例
	 * @param instanceId instanceId
	 * @return WorkflowInstance
	 */
	public WorkflowInstance getInstanceById(String instanceId) {
		WorkflowInstance ins = instanceDao.getByInstanceId(instanceId);
		return ins;
	}

	/**
	 * 通过电子表格ID以及参数获得流程实例
	 * @param spreadSheetId
	 *            spreadSheetId
	 * @param mechanismParam
	 *            mechanismParam
	 * @param otherParams
	 *            otherParams
	 * @return WorkflowInstance
	 */
	public WorkflowInstance getInsBySpreadSheetAndParam(String spreadSheetId, String mechanismParam, String otherParams) {
		WorkflowResource res = RepositoryService.getInstance().getResBySpreadSheetAndParam(spreadSheetId,
				mechanismParam, otherParams);
		if (res == null) {
			return null;
		}
		WorkflowInstance ins = instanceDao.getByResourceId(res.getResourceId());
		return ins;
	}

	/**
	 * 通过流程ID、机构参数、其它参数查询出相关流程
	 * @param workflowId workflowId
	 * @param mechanismParam mechanismParam
	 * @param otherParams otherParams
	 * @return List<Instance>
	 */
	public List<Instance> findInsByWorkflowInfoAndParam(String workflowId, String mechanismParam, String otherParams) {
		List<WorkflowResource> resources = WorkflowResourceDao.getInstance().findWorkflowResource(workflowId, mechanismParam, otherParams);
		List<Instance> inss = new ArrayList<Instance>();
		for (WorkflowResource res : resources) {
			WorkflowInstance wIns = instanceDao.getByResourceId(res.getResourceId());
			WorkflowInfo info = this.repositoryService.getInfo(wIns.getWorkflowId());
			String[] spreadsheetId = res.getSpreadsheetId().split("_");
			CatalogElement spreadsheet = CatalogTreeModule.getInstance().getCatalogElementById(spreadsheetId[0]);

			Define define = repositoryService.parseDefine(wIns.getWorkflowId(), info.getWorkflowDefine());
			Instance ins = new Instance();
			if (wIns.getActivity().equals("")) {
				ins.setActivity("childcomplete");
			} else {
				Activity act = this.getCurrentActivity(wIns.getActivity(), define);
				if (act != null) {
					ins.setActivity(act.getName());	
				}				
			}
			ins.setSpreadsheetName(spreadsheet.getName());
			ins.setMechanismParam(res.getMechanismParam());
			ins.setOtherParams(res.getOtherParams());
			ins.setInstanceName(info.getName());
			ins.setInstanceCreateDate(wIns.getInstanceCreateDate());
			ins.setInstanceDesc(wIns.getInstanceDesc());
			ins.setInstanceState(wIns.getInstanceState());
			inss.add(ins);
		}	
		
		return inss;
	}
	/**
	 * 获得运行中的流程
	 * @return List<Instance>
	 */
	public List<Instance> getRuningInstances() {
		List<Instance> inss = new ArrayList<Instance>();
		List<WorkflowInstance> wInss = instanceDao.getAllInstance("running");
		for (WorkflowInstance wIns : wInss) {
			WorkflowInfo info = this.repositoryService.getInfo(wIns.getWorkflowId());
			Define define = repositoryService.parseDefine(wIns.getWorkflowId(), info.getWorkflowDefine());
			Instance ins = new Instance();
			if (wIns.getActivity().equals("")) {
				ins.setActivity("childcomplete");
			} else {
				Activity act = this.getCurrentActivity(wIns.getActivity(), define);
				ins.setActivity(act.getName());
			}
			ins.setInstanceCreateDate(wIns.getInstanceCreateDate());
			ins.setInstanceDesc(wIns.getInstanceDesc());
			ins.setInstanceState(wIns.getInstanceState());
			inss.add(ins);
		}
		return inss;
	}

	/**
	 * 获得流程的所有节点
	 * @param instanceId
	 *            instanceId
	 * @return List<Activity>
	 */
	public List<Activity> getInstanceActivitsById(String instanceId) {
		WorkflowInstance wIns = instanceDao.getByInstanceId(instanceId);
		WorkflowInfo info = this.repositoryService.getInfo(wIns.getWorkflowId());
		Define define = repositoryService.parseDefine(wIns.getWorkflowId(), info.getWorkflowDefine());
		List<Activity> acts = getSortActivits(define);
		// 在父节流程中查找
		String actStr = getInsParentActivityById(wIns.getInstanceId());
		Map<String, Integer> actMap = this.sortActivits(define);
		// 如果父流程中没有找到所处节点，则在子流程中中找
		if (actStr == null) {
			List<String> actsStr = new ArrayList<String>();
			getInsChildActivitysById(actsStr, instanceId);
			if (actsStr.size() > 0) {
				// 查找离汇总节点最近的流程状态
				String closeAct = actsStr.get(0);
				Integer closeActNub = 0;
				for (String cAct : actsStr) {
					Integer nub = actMap.get(cAct);
					if (nub > closeActNub) {
						closeActNub = nub;
						closeAct = cAct;
					}
				}
				actStr = closeAct;
			}
		}
		for (Activity act : acts) {
			if (act.getId().equals(actStr)) {
				act.setDesc("current");
			}
		}
		return acts;
	}

	/**
	 * 对流程节点进行排序
	 * @param define
	 *            define
	 * @return List<Activity>
	 */
	public List<Activity> getSortActivits(Define define) {
		List<Activity> acts = new ArrayList<Activity>();
		Activity startAct = this.getStartActivity(define);
		acts.add(startAct);
		String nextActStr = startAct.getId();
		while (this.getNextActivity(nextActStr, define) != null) {
			Activity nextAct = this.getNextActivity(nextActStr, define);
			if (nextAct.getId().equals(startAct.getId())
					|| (define.getActivitys() != null && acts.size() >= define.getActivitys().size())) {
				log.error(define.getName() + " FlowWork There's an infinite loop  ");
				break;
			}
			nextActStr = nextAct.getId();
			acts.add(nextAct);
		}
		return acts;
	}

	/**
	 * 
	 * @param define
	 *            define
	 * @return Map<String,Integer>
	 */
	public Map<String, Integer> sortActivits(Define define) {
		Map<String, Integer> actMap = new HashMap<String, Integer>();
		Activity startAct = this.getStartActivity(define);
		String nextActStr = startAct.getId();
		int i = 0;
		actMap.put(nextActStr, i);
		while (this.getNextActivity(nextActStr, define) != null) {
			Activity nextAct = this.getNextActivity(nextActStr, define);
			if (nextAct.getId().equals(startAct.getId())
					|| (define.getActivitys() != null && i >= define.getActivitys().size())) {
				log.error(define.getName() + " FlowWork There's an infinite loop  ");
				break;
			}
			i++;
			nextActStr = nextAct.getId();
			actMap.put(nextActStr, i);
		}
		return actMap;
	}

	/**
	 * 获得流程所处节点，如果当前流程实例没有所处节点，则在其父流程实例上查找
	 * @param instanceId
	 *            instanceId
	 * @return String
	 */
	public String getInsParentActivityById(String instanceId) {
		WorkflowInstance wIns = instanceDao.getByInstanceId(instanceId);
		if (!wIns.getActivity().equals("")) {
			return wIns.getActivity();
		} else if (wIns.getParentInstanceId() != null && !instanceId.equals(wIns.getParentInstanceId())) {
			return getInsParentActivityById(wIns.getParentInstanceId());
		}
		return null;
	}

	/**
	 * 获得流程所处节点，如果当前流程实例没有所处节点，则在其子流程实例上查找
	 * @param acts
	 *            acts
	 * @param instanceId
	 *            instanceId
	 */
	public void getInsChildActivitysById(List<String> acts, String instanceId) {
		WorkflowInstance wIns = instanceDao.getByInstanceId(instanceId);
		if (!wIns.getActivity().equals("")) {
			acts.add(wIns.getActivity());
		} else {
			List<WorkflowInstance> childsIns = this.instanceDao.getChildInstanceByParentId(instanceId);
			for (WorkflowInstance cIns : childsIns) {
				if (!instanceId.equals(cIns.getInstanceId())) {
					getInsChildActivitysById(acts, cIns.getInstanceId());
				}
			}
		}
	}

	/**
	 * 获得完成的流程实例
	 * @return List<WorkflowInstance>
	 */
	public List<WorkflowInstance> getCompleteInstances() {
		return instanceDao.getAllInstance("complete");
	}


	/**
	 * 分配任务
	 * 
	 * @param instanceId
	 *            instanceId
	 * @param workflowId
	 *            workflowId
	 * @param resourceId
	 *            resourceId
	 * @param act
	 *            act
	 * @param define
	 *            define
	 * @return List<WorkflowTask>
	 */
	public List<WorkflowTask> assignTask(String instanceId, String workflowId,
			String resourceId, Activity act, Define define) {
		WorkflowInstance ins = instanceDao.getByInstanceId(instanceId);
		String creatorId = ins.getInstanceCreatorId();
		List<Assignee> assis = act.getAssignee();
		String vsType = act.getVisiableType();
		//兼容修改，yes仅发起人可见；sameOrg同机构可见； no所有人可见
		if (!StringUtil.isNullOrEmpty(vsType) && !vsType.equals("no") 
				&& getActivitIndex(define, act.getId()) == 1) {
			if (vsType.equals("yes")) {
				assis.clear();
				Assignee ag = new Assignee();
				ag.setType("USER");
				ag.setValue(creatorId);
				assis.add(ag);
			} else if (vsType.equals("sameOrganization")) {
				UserManagerModule userManagerModule = UserManagerModule.getInstance();
				List<String> currentGroupStrs = userManagerModule.getDefaultGroups(creatorId);
				if (currentGroupStrs.size() > 0) {
					List<Assignee> newAssignee = new ArrayList<Assignee>();
					String currentGroup = currentGroupStrs.get(0);
					for (Assignee assi : assis) {
						if (assi.getType().equals("USER")) {
							if (assi.getValue().equals(creatorId)) {
								newAssignee.add(assi);
							} else {
								List<String> defaultGroup = userManagerModule.getDefaultGroups(assi.getValue());
								if (defaultGroup.size() > 0 && defaultGroup.get(0).equals(currentGroup)) {
									newAssignee.add(assi);
								}
							}
						} else if (assi.getType().equals("GROUP")) {
							if (assi.getValue().equals(currentGroup)) {
								newAssignee.add(assi);
							}
						}
					}
					assis = newAssignee;
				}
			}
		}
		List<Assignee> customAssis = new ArrayList<Assignee>();
		for (Assignee assi : assis) {
			if (assi.getType().equals("CUSTOM")) {
				UserManagerModule.getInstance().getStateModule().setSessionAttribute("flowInstanceId", instanceId);
				String sql = assi.getValue();
				BusinessViewBO view = new BusinessViewBO();
				view.setEnforceFilter(false);
				String viewId = view.getBusinessViewId();
				view.setDataSourceID("DS.SYSTEM知识库");
				RawSqlQuery rawSql = view.createRawSqlQuery(sql);
				rawSql.setQueryRelation(QueryRelationType.MAIN);
				ClientReportService reportService = ClientReportService.getInstance();
				ClientReportView clientReportView = reportService.createSimpleReport();
				String reportId = clientReportView.getClientId();
				SimpleReportBO tmpReport = (SimpleReportBO) UserManagerModule.getInstance().getStateModule().getSessionAttribute(reportId);
				reportService.initFromBizViewInternal(reportId, viewId);
				tmpReport.setRowsPerPage(10);
				GridData data = tmpReport.getGridData(0);
				for (int i = 0; i < data.getRowsCount(); i++) {
					for (int j = 0; j < data.getColumnsCount(); j++) {
						Assignee as = new Assignee();
						as.setValue(data.get(i, j).getStringValue());
						as.setType("USER");
						customAssis.add(as);
					}
				}
			}
		}
		if (customAssis.size() > 0) {
			assis = customAssis;
		}
		JSONArray assignees = JSONArray.fromArray(assis.toArray());
		WorkflowTask task = taskService.createTask(instanceId, workflowId, act.getName(), resourceId, act.getRule(), "",
				assignees.toString(), act.getOpt());
		List<WorkflowTask> tasks =  new ArrayList<WorkflowTask>();
		tasks.add(task);
		return tasks;
	}

	/**
	 * 
	 * @param define
	 *            define
	 *  @param currentId
	 *            currentId
	 * @return index
	 */
	public int getActivitIndex(Define define, String currentId) {
		Activity startAct = this.getStartActivity(define);
		String nextActStr = startAct.getId();
		int i = 0;
		while (this.getNextActivity(nextActStr, define) != null) {
			i++;
			Activity nextAct = this.getNextActivity(nextActStr, define);
			nextActStr = nextAct.getId();
			if (currentId.equals(nextActStr)) {
				return i;
			}
		}
		return i;
	}
	
	/**
	 * 
	 * @param define
	 *            define
	 *  @param id
	 *            index
	 * @return Activity
	 */
	public Activity getActivitByIndex(Define define, String id) {
		if (StringUtil.isNullOrEmpty(id)) {
			return null;
		}
		Activity startAct = this.getStartActivity(define);
		String nextActStr = startAct.getId();
		while (this.getNextActivity(nextActStr, define) != null) {
			Activity nextAct = this.getNextActivity(nextActStr, define);
			nextActStr = nextAct.getId();
			if (id.equals(nextActStr)) {
				return nextAct;
			}
		}
		return null;
	}
	
	/**
	 * 获得流程的启动节点
	 * @param define
	 *            define
	 * @return Activity
	 */
	public Activity getStartActivity(Define define) {
		List<Activity> acts = define.getActivitys();
		for (Activity act : acts) {
			if (act.getType().equals("start")) {
				return act;
			}
		}
		return null;
	}

	/**
	 * 获得流程的结束节点
	 * @param define
	 *            define
	 * @return Activity
	 */
	public Activity getEndActvity(Define define) {
		List<Activity> acts = define.getActivitys();
		for (Activity act : acts) {
			if (act.getType().equals("end")) {
				return act;
			}
		}
		return null;
	}

	/**
	 * 获得流程下一个节点
	 * @param currentAct currentAct
	 * @param define define
	 * @return Activity
	 */
	public Activity getNextActivity(String currentAct, Define define) {
		List<Path> paths = define.getPaths();
		List<Activity> acts = define.getActivitys();
		for (Path path : paths) {
			if (path.getFrom().equals(currentAct)) {
				String nextAct = path.getTo();
				for (Activity act : acts) {
					if (act.getId().equals(nextAct)) {
						return act;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 获得流程上一个节点
	 * @param currentAct
	 *            currentAct
	 * @param define
	 *            define
	 * @return Activity
	 */
	public Activity getPreActivity(String currentAct, Define define) {
		List<Path> paths = define.getPaths();
		List<Activity> acts = define.getActivitys();
		for (Path path : paths) {
			if (path.getTo().equals(currentAct)) {
				String nextAct = path.getFrom();
				for (Activity act : acts) {
					if (act.getId().equals(nextAct)) {
						return act;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 获得流程当前节点
	 * @param currentAct
	 *            currentAct
	 * @param define
	 *            define
	 * @return Activity
	 */
	public Activity getCurrentActivity(String currentAct, Define define) {
		List<Activity> acts = define.getActivitys();
		for (Activity act : acts) {
			if (act.getId().equals(currentAct)) {
				return act;
			}
		}
		return null;
	}

	/**
	 * 流转到下一个节点
	 * @param instanceId
	 *            instanceId
	 */
	public void nextActivity(String instanceId) {
		WorkflowInstance ins = instanceDao.getByInstanceId(instanceId);
		WorkflowInfo info = repositoryService.getInfo(ins.getWorkflowId());
		Define define = repositoryService.parseDefine(ins.getWorkflowId(), info.getWorkflowDefine());
		Activity act = getNextActivity(ins.getActivity(), define);
		// 判断下一个节点操作是否有汇总，如果有汇总，启动主流程
		if (act.getOpt() != null) {
			JSONArray opts = JSONArray.fromString(act.getOpt());
			Iterator optIt = opts.iterator();
			while (optIt.hasNext()) {
				String optStr = (String) optIt.next();
				if (optStr.equals("summary")) {
					String pinsId = ins.getParentInstanceId();
					if (pinsId == null) {
						// 启动父流程实例
						WorkflowInstance pins = this.startParentIns(ins.getWorkflowId(), ins.getResourceId(),
								ins.getInstanceId(), ins.getInstanceCreatorId(), act);
						// 将当前流程的ACTIVITY设为空
						ins.setActivity("");
						ins.setParentInstanceId(pins.getInstanceId());
						instanceDao.update(ins);
					} else {
						// 将当前流程的ACTIVITY设为空
						ins.setActivity("");
						instanceDao.update(ins);
						WorkflowInstance pIns = this.instanceDao.getByInstanceId(pinsId);
						
						// 如果父节点已经激活，则不再激活
						if (pIns.getActivity().equals("")) {
							pIns.setActivity(act.getId());
							if (!act.getType().equals("end")) {
								List<WorkflowTask> tasks = assignTask(pIns.getInstanceId(), info.getId(),
										pIns.getResourceId(), act, define);
								TaskService.getInstance().addOperationLogByTaskName(tasks.get(0).getTaskId(), "已有流程进入汇总",
										"已有流程进入汇总");
							}
						}
					}
					return;
				}
			}
		}
		ins.setActivity(act.getId());
		if (act.getType().equals("end")) {
			ins.setInstanceState("complete");
			ins.setCompleteDate(new Date());
			completeAllChildrenInstance(instanceId); //把所有子流程的运行状态改为完成
		}
		instanceDao.update(ins);
		if (!act.getType().equals("end")) {
			assignTask(ins.getInstanceId(), info.getId(), ins.getResourceId(), act, define);
		}
	}
	
	/**
	 * 把所有子流程的运行状态改为完成
	 */
	private void  completeAllChildrenInstance(String parentId) {
		List<WorkflowInstance> children = instanceDao.getChildInstanceByParentId(parentId);
		if (children == null || children.size() == 0) {
			return;
		}
		for (WorkflowInstance child : children) {
			child.setInstanceState("complete");
			child.setCompleteDate(new Date());
			instanceDao.update(child);
			completeAllChildrenInstance(child.getInstanceId());
		}
	}
	
	/**
	 * 流转到上一个节点
	 * @param instanceId
	 *            instanceId
	 */
	public void preActivity(String instanceId) {
		WorkflowInstance ins = instanceDao.getByInstanceId(instanceId);
		WorkflowInfo info = repositoryService.getInfo(ins.getWorkflowId());
		Define define = repositoryService.parseDefine(ins.getWorkflowId(), info.getWorkflowDefine());
		Activity act = getPreActivity(ins.getActivity(), define);

		// 判断当前节点操作是否有汇总，如果有汇总，将子流程回退
		Activity currentAct = getCurrentActivity(ins.getActivity(), define);
		JSONArray opts = JSONArray.fromString(currentAct.getOpt());
		Iterator optIt = opts.iterator();
		while (optIt.hasNext()) {
			String optStr = (String) optIt.next();
			if (optStr.equals("summary")) {
				// 将当前流程的ACTIVITY设为空
				ins.setActivity("");
				instanceDao.update(ins);
				List<WorkflowInstance> childInss = instanceDao.getChildInstanceByParentId(ins.getInstanceId());
				for (WorkflowInstance childIns : childInss) {
					childIns.setActivity(act.getId());
					assignTask(childIns.getInstanceId(), info.getId(), childIns.getResourceId(), act, define);
				}
				return;
			}
		}
		ins.setActivity(act.getId());
		if (act.getType().equals("end")) {
			ins.setInstanceState("complete");
		}
		instanceDao.update(ins);
		assignTask(ins.getInstanceId(), info.getId(), ins.getResourceId(), act, define);
	}

	/**
	 * 流转到开始节点
	 * @param instanceId
	 *            instanceId
	 */
	public void toStart(String instanceId) {
		WorkflowInstance ins = instanceDao.getByInstanceId(instanceId);
		WorkflowInfo info = repositoryService.getInfo(ins.getWorkflowId());
		Define define = repositoryService.parseDefine(ins.getWorkflowId(), info.getWorkflowDefine());
		Activity act = getStartActivity(define);
		// 查找树的末尾节点,支持汇总回退到首节点
		List<WorkflowInstance> baseChildIns = new ArrayList<WorkflowInstance>();
		findBaseChildsIns(ins.getInstanceId(), baseChildIns);
		if (baseChildIns.size() > 0) {
			ins.setActivity("");
			instanceDao.update(ins);
			for (WorkflowInstance childIns : baseChildIns) {
				childIns.setActivity(act.getId());
				instanceDao.update(childIns);
				nextActivity(childIns.getInstanceId());
			}
			return;
		}

		ins.setActivity(act.getId());
		instanceDao.update(ins);
		nextActivity(ins.getInstanceId());
	}

	/**
	 * 查找树的末尾节点
	 * 
	 * @param instanceId
	 *            instanceId
	 * @param baseChildIns
	 *            baseChildIns
	 */
	public void findBaseChildsIns(String instanceId, List<WorkflowInstance> baseChildIns) {
		List<WorkflowInstance> childsIns = this.instanceDao.getChildInstanceByParentId(instanceId);
		if (childsIns.size() > 0) {
			for (WorkflowInstance childIns : childsIns) {
				findBaseChildsIns(childIns.getInstanceId(), baseChildIns);
			}
		} else {
			baseChildIns.add(this.instanceDao.load(instanceId));
		}

	}
}
