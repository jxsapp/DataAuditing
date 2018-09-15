package smartbi.auditing.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import smartbi.CommonConfiguration;
import smartbi.SmartbiException;
import smartbi.auditing.DataAuditingErrorCode;
import smartbi.auditing.repository.Assignee;
import smartbi.auditing.repository.TaskResource;
import smartbi.auditing.repository.WorkflowInfo;
import smartbi.auditing.repository.WorkflowInstance;
import smartbi.auditing.repository.WorkflowInstanceDao;
import smartbi.auditing.repository.WorkflowLog;
import smartbi.auditing.repository.WorkflowLogDao;
import smartbi.auditing.repository.WorkflowResource;
import smartbi.auditing.repository.WorkflowResourceDao;
import smartbi.auditing.repository.WorkflowTask;
import smartbi.auditing.repository.WorkflowTaskDao;
import smartbi.catalogtree.CatalogElement;
import smartbi.catalogtree.CatalogTreeModule;
import smartbi.framework.IModule;
import smartbi.net.sf.json.JSONArray;
import smartbi.net.sf.json.JSONObject;
import smartbi.repository.IDAOModule;
import smartbi.state.IStateModule;
import smartbi.user.IGroup;
import smartbi.user.IUser;
import smartbi.usermanager.Group;
import smartbi.usermanager.GroupToRole;
import smartbi.usermanager.Role;
import smartbi.usermanager.User;
import smartbi.usermanager.UserManagerModule;
import smartbi.util.StringUtil;
import smartbi.util.UUIDGenerator;

/**
 * 
 * @author huangpeng
 *
 */
public class TaskService implements IModule {
	/** */
	private RuntimeService runtimeService;
	/** */
	private UserManagerModule userManagerModule;
	/** */
	private IStateModule stateModule;
	/** */
	private static TaskService instance;
	/** */
	private WorkflowResourceDao resDao = WorkflowResourceDao.getInstance();
	/** */
	private WorkflowTaskDao taskDao = WorkflowTaskDao.getInstance();
	/** */
	private IDAOModule daoModule;

	/**
	 * 
	 * @return TaskService
	 */
	public static TaskService getInstance() {
		if (instance == null) {
			instance = new TaskService();
		}
		return instance;
	}

	/**
	 * 
	 */
	protected TaskService() {
	}

	/**
	 * 
	 */
	public void activate() {
		daoModule.addPOJOClass(WorkflowTask.class);
		daoModule.addPOJOClass(WorkflowLog.class);
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
	 * @return runtimeService
	 */
	public RuntimeService getRuntimeService() {
		return runtimeService;
	}

	/**
	 * 
	 * @param runtimeService
	 *            runtimeService
	 */
	public void setRuntimeService(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
	}

	/**
	 * 获得所分配的任务
	 * 
	 * @param state
	 *            state
	 * @return List<TaskResource>
	 */
	public List<TaskResource> taskByAssignee(String state) {
		List<TaskResource> taskRess = new ArrayList<TaskResource>();
		List<WorkflowTask> tasks = this.getTaskByAssignee(state);
		for (WorkflowTask task : tasks) {
			WorkflowInfo info = RepositoryService.getInstance().getInfo(task.getWorkflowId());
			WorkflowResource res = RepositoryService.getInstance().getResByResId(task.getResourceId());
			String[] spreadsheetId = res.getSpreadsheetId().split("_");
			CatalogElement spreadsheet = CatalogTreeModule.getInstance().getCatalogElementById(spreadsheetId[0]);
			if (info == null || info.getName() == null || spreadsheet == null || spreadsheet.getName() == null) {
				continue;
			}
			TaskResource taskRes = new TaskResource();
			taskRes.setCreateTime(task.getCreateTime());
			taskRes.setMechanismParam(res.getMechanismParam());
			taskRes.setOtherParams(res.getOtherParams());
			taskRes.setAssignee(task.getAssignee());
			taskRes.setInstanceId(task.getInstanceId());
			taskRes.setSpreadsheetId(res.getSpreadsheetId());
			taskRes.setResourceId(task.getResourceId());
			taskRes.setTaskDesc(task.getTaskDesc());
			taskRes.setTaskState("undone");

			taskRes.setTaskOperateTime(task.getTaskOperateTime());
			taskRes.setSpreadsheetName(spreadsheet.getName());
			taskRes.setWorkflowName(info.getName());
			taskRes.setWorkflowId(task.getWorkflowId());
			taskRes.setTaskName(task.getTaskName());
			taskRess.add(taskRes);
		}
		/*
		 * if (state.equals("unfinished")) { taskSortByCreateTime(taskRess); } else {
		 * taskSortByOperateTime(taskRess); }
		 */
		return taskRess;
	}

	/**
	 * 
	 * @param tasks
	 *            tasks
	 */
	public void taskSortByCreateTime(List<TaskResource> tasks) {
		TaskResource tTask;
		int size = tasks.size();
		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				if (tasks.get(i).getCreateTime().getTime() > tasks.get(j).getCreateTime().getTime()) {
					tTask = tasks.get(i);
					tasks.set(i, tasks.get(j));
					tasks.set(j, tTask);
				}
			}
		}
	}

	/**
	 * 
	 * @param tasks
	 *            tasks
	 */
	public void taskSortByOperateTime(List<TaskResource> tasks) {
		TaskResource tTask;
		int size = tasks.size();
		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				if (tasks.get(i).getTaskOperateTime().getTime() < tasks.get(j).getTaskOperateTime().getTime()) {
					tTask = tasks.get(i);
					tasks.set(i, tasks.get(j));
					tasks.set(j, tTask);
				}
			}
		}
	}

	/**
	 * 获得相关状态的该用户所分配的任务
	 * 
	 * @param state
	 *            state
	 * @return List<WorkflowTask>
	 */
	public List<WorkflowTask> getTaskByAssignee(String state) {
		userManagerModule = UserManagerModule.getInstance();
		stateModule = userManagerModule.getStateModule();
		// 当前用户
		User user = userManagerModule.getUserById(stateModule.getCurrentUser().getId());
		// 当前用户所在组
		List<Group> currentUserGroups = new ArrayList<Group>();
		List<String> currentGroupStrs = userManagerModule.getDefaultGroups(user.getId());
		for (String groupStre : currentGroupStrs) {
			currentUserGroups.add(userManagerModule.getGroupById(groupStre));
		}

		List<WorkflowTask> currentUserTasks = new ArrayList<WorkflowTask>();
		// 判断任务分配的角色和用户所处角色是否相同
		for (Role role : user.getAssignedRoles()) {
			Assignee assi = new Assignee();
			assi.setType("ROLE");
			assi.setValue(role.getId());
			JSONObject assignee = JSONObject.fromObject(assi);
			List<WorkflowTask> ret = taskDao.getByAssignee(assignee.toString(), state);
			if (ret != null) {
				// 判断流程实例创建用户是否是组树中当前用户组的子节点
				for (WorkflowTask task : ret) {
					WorkflowInstance ins = runtimeService.getInstanceById(task.getInstanceId());
					if ("system".equals(ins.getInstanceCreatorId())) { // 流程计划的创建者为system
						currentUserTasks.add(task);
					} else {
						/*List<String> groupStrs = userManagerModule.getDefaultGroups(ins.getInstanceCreatorId());
						// 创建用户所在默认组
						Group creatorGroup = userManagerModule.getGroupById(groupStrs.get(0));
						for (IGroup group : currentUserGroups) {
							Group currentGroup = (Group) group;
							// 判断是当前用户所处组是否是创建用户所处组的父节点
							if (findGroupToParent(creatorGroup, currentGroup)) {
								currentUserTasks.add(task);
							} else if (findGroupToSub(creatorGroup, currentGroup)) {
								currentUserTasks.add(task);
							}
						}*/
						//上面的逻辑，当流程创建的用户所在组与当前用户所在组不是父子节点关系时，任务加入不了，所以不对用户所在分组进行父子节点判断
						currentUserTasks.add(task);
					}
				}
			}
		}

		// 判断任务分配的组和用户所处组是否相同
		for (IGroup group : currentUserGroups) {
			Assignee assi = new Assignee();
			assi.setType("GROUP");
			assi.setValue(group.getId());
			JSONObject assignee = JSONObject.fromObject(assi);
			List<WorkflowTask> ret = taskDao.getByAssignee(assignee.toString(), state);
			if (ret != null) {
				// 判断流程实例创建用户是否在当前用户组的树中
				for (WorkflowTask task : ret) {
					if (!currentUserTasks.contains(task)) {
						currentUserTasks.add(task);
					}

				}
			}
		}
		//通过用户组继承的角色
		for (Group group : currentUserGroups) {
			List<GroupToRole> groupToRole = group.getGroupToRole();
			for (GroupToRole groupToRole2 : groupToRole) {
				Assignee assi = new Assignee();
				assi.setType("ROLE");
				assi.setValue(groupToRole2.getRole().getId());
				JSONObject assignee = JSONObject.fromObject(assi);
				List<WorkflowTask> ret = taskDao.getByAssignee(assignee.toString(), state);
				if (ret != null) {
					// 判断流程实例创建用户是否在当前用户组的树中
					for (WorkflowTask task : ret) {
						if (!currentUserTasks.contains(task)) {
							currentUserTasks.add(task);
						}

					}
				}
			}
		}
		// 判断任务分配的用户和当前用户是否相同
		Assignee assi = new Assignee();
		assi.setType("USER");
		assi.setValue(user.getId());
		JSONObject assignee = JSONObject.fromObject(assi);
		List<WorkflowTask> userRet = taskDao.getByAssignee(assignee.toString(), state);
		if (userRet != null) {
			// 判断流程实例创建用户是否在当前用户组的树中
			for (WorkflowTask task : userRet) {
				if (!currentUserTasks.contains(task)) {
					currentUserTasks.add(task);
				}
			}
		}

		return currentUserTasks;
	}

	/**
	 * 判断参数curentGroup是否是group的子组
	 * 
	 * @param group
	 *            group
	 * @param curentGroup
	 *            curentGroup
	 * @return boolean
	 */
	public boolean findGroupToSub(Group group, Group curentGroup) {
		List<Group> subGroups = group.getSubGroups();
		for (Group subGroup : subGroups) {
			if (subGroup.getId().equals(curentGroup.getId())) {
				return true;
			} else {
				return findGroupToSub(subGroup, curentGroup);
			}
		}
		return false;
	}

	/**
	 * 判断参数curentGroup是否是group的父组
	 * 
	 * @param group
	 *            group
	 * @param curentGroup
	 *            curentGroup
	 * @return boolean
	 */
	public boolean findGroupToParent(Group group, Group curentGroup) {
		if (group.getId().equals(curentGroup.getId())) {
			return true;
		}
		Group parentGroup = group.getParentGroup();
		if (parentGroup != null) {
			if (parentGroup.getId().equals(curentGroup.getId())) {
				return true;
			} else {
				return findGroupToParent(parentGroup, curentGroup);
			}
		}
		return false;
	}

	/**
	 * 查找两个组相距的级别
	 * 
	 * @param group
	 *            group
	 * @param curentGroup
	 *            curentGroup
	 * @param level
	 *            level
	 * @return int
	 */
	public int findLevelGroupToSub(Group group, Group curentGroup, int level) {
		List<Group> subGroups = group.getSubGroups();
		for (int i = 0; i < subGroups.size(); i++) {
			if (subGroups.get(i).getId().equals(curentGroup.getId())) {
				return level + 1;
			} else if (i == (subGroups.size() - 1)) {
				return findLevelGroupToSub(subGroups.get(i), curentGroup, level) + 1;
			}
		}
		return -99999;
	}

	/**
	 * 获得group所处组的第level个级别的子组，放入allSubGroups中
	 * 
	 * @param allSubGroups
	 *            allSubGroups
	 * @param group
	 *            group
	 * @param level
	 *            level
	 */
	public void getSubGroupsByLevel(List<Group> allSubGroups, Group group, int level) {
		List<Group> subGroups = group.getSubGroups();
		if (level > 1) {
			for (Group subGroup : subGroups) {
				getSubGroupsByLevel(allSubGroups, subGroup, level - 1);
			}
		} else {
			allSubGroups.addAll(subGroups);
		}
	}

	/**
	 * 通过流程状态，电子表格ID，参数获得分配的任务
	 * 
	 * @param state
	 *            state
	 * @param spreadSheetId
	 *            spreadSheetId
	 * @param mechanismParam
	 *            mechanismParam
	 * @param otherParams
	 *            otherParams
	 * @return WorkflowTask
	 */
	public WorkflowTask getTaskByRes(String state, String spreadSheetId, String mechanismParam, String otherParams) {
		WorkflowResource res = resDao.getBySpreadSheetAndParam(spreadSheetId, mechanismParam, otherParams);
		if (res == null) {
			return null;
		}
		String resId = res.getResourceId();
		userManagerModule = UserManagerModule.getInstance();
		stateModule = userManagerModule.getStateModule();
		User user = userManagerModule.getUserById(stateModule.getCurrentUser().getId());
		List<Group> currentUserGroups = new ArrayList<Group>();
		List<String> currentGroupStrs = userManagerModule.getDefaultGroups(user.getId());
		for (String groupStre : currentGroupStrs) {
			currentUserGroups.add(userManagerModule.getGroupById(groupStre));
		}
		for (Role role : user.getAssignedRoles()) {
			Assignee assi = new Assignee();
			assi.setType("ROLE");
			assi.setValue(role.getId());
			JSONObject assignee = JSONObject.fromObject(assi);
			WorkflowTask task = taskDao.getByRes(assignee.toString(), state, resId);
			if (task != null) {
				// 判断流程实例创建用户是否在当前用户组的树中
				WorkflowInstance ins = runtimeService.getInstanceById(task.getInstanceId());
				if ("system".equals(ins.getInstanceCreatorId())) { // 流程计划的创建者为system
					return task;
				} else {
					/*List<String> groupStrs = userManagerModule.getDefaultGroups(ins.getInstanceCreatorId());
					// 创建用户所在默认组
					Group creatorGroup = userManagerModule.getGroupById(groupStrs.get(0));
					for (IGroup group : currentUserGroups) {
						Group currentGroup = (Group) group;
						if (findGroupToParent(creatorGroup, currentGroup)) {
							return task;
						} else if (findGroupToSub(creatorGroup, currentGroup)) {
							return task;
						}
					}*/
					return task;
				}
			}
		}
		for (IGroup group : currentUserGroups) {
			Assignee assi = new Assignee();
			assi.setType("GROUP");
			assi.setValue(group.getId());
			JSONObject assignee = JSONObject.fromObject(assi);
			WorkflowTask task = taskDao.getByRes(assignee.toString(), state, resId);
			if (task != null) {
				return task;
			}
		}

		//通过用户组继承的角色
		for (Group group : currentUserGroups) {
			List<GroupToRole> groupToRole = group.getGroupToRole();
			for (GroupToRole groupToRole2 : groupToRole) {
				Assignee assi = new Assignee();
				assi.setType("ROLE");
				assi.setValue(groupToRole2.getRole().getId());
				JSONObject assignee = JSONObject.fromObject(assi);
				WorkflowTask task = taskDao.getByRes("%" + assignee.toString() + "%", state, resId);
				if (task != null) {
					return task;
				}
			}
		}
				
		Assignee assi = new Assignee();
		assi.setType("USER");
		assi.setValue(user.getId());
		JSONObject assignee = JSONObject.fromObject(assi);
		WorkflowTask task = taskDao.getByRes(assignee.toString(), state, resId);
		if (task != null) {
			return task;
		}
		return null;
	}

	/**
	 * 通过流程状态，电子表格ID，参数获得任务接收者
	 * 
	 * @param state
	 *            state
	 * @param spreadSheetId
	 *            spreadSheetId
	 * @param mechanismParam
	 *            mechanismParam
	 * @param otherParams
	 *            otherParams
	 * @return WorkflowTask
	 */
	public List<String> getTaskUserIdsByRes(String state, String spreadSheetId, String mechanismParam, String otherParams) {
		List<String> userIds = new ArrayList<String>();
		WorkflowResource res = resDao.getBySpreadSheetAndParam(spreadSheetId, mechanismParam, otherParams);
		if (res == null) {
			return userIds;
		}
		String resId = res.getResourceId();
		WorkflowTask task = taskDao.getByRes(null, state, resId);
		if (task != null) {
			JSONArray array = JSONArray.fromString(task.getAssignee());
			if (array != null) {
				for (int i = 0; i < array.length(); i++) {
					String type = ((JSONObject) array.get(i)).optString("type");
					String value = ((JSONObject) array.get(i)).optString("value");
					if (type.equals("ROLE")) {
						Set<IUser> users = (Set<IUser>) userManagerModule.getAllUsersOfRole(value);
						for (IUser user : users) {
							userIds.add(user.getId());
						}
					} else if (type.equals("GROUP")) {
						List<? extends IUser> usersOfGroup = userManagerModule.getUsersOfGroup(value);
						if (usersOfGroup != null && usersOfGroup.size() > 0) {
							for (IUser user : usersOfGroup) {
								userIds.add(user.getId());
							}
						}
					} else if (type.equals("USER")) {
						userIds.add(value);
					}
				}
			}
		}
		return userIds;
	}
	
	/**
	 * 添加操作信息
	 * 
	 * @param taskId
	 *            taskId
	 * @param opt
	 *            opt
	 */
	public void addOperationLog(String taskId, String opt) {
		WorkflowTask task = taskDao.getByTaskId(taskId);
		userManagerModule = UserManagerModule.getInstance();
		stateModule = userManagerModule.getStateModule();
		User user = (User) stateModule.getCurrentUser();
		WorkflowLog log = new WorkflowLog();
		log.setId(UUIDGenerator.generate());
		log.setTaskId(task.getTaskId());
		log.setTaskName(task.getTaskName());
		log.setInstanceId(task.getInstanceId());
		log.setUserName(user.getName());
		log.setUseralias(user.getAlias());
		log.setTime(new Date());
		log.setDetail(opt);
		WorkflowLogDao.getInstance().save(log);
	}

	/**
	 * 添加操作信息，指定任务名称
	 * 
	 * @param taskId
	 *            taskId
	 * @param opt
	 *            opt
	 * @param taskName
	 *            taskName
	 */
	public void addOperationLogByTaskName(String taskId, String opt, String taskName) {
		WorkflowTask task = taskDao.getByTaskId(taskId);
		userManagerModule = UserManagerModule.getInstance();
		stateModule = userManagerModule.getStateModule();
		WorkflowLog log = new WorkflowLog();
		log.setId(UUIDGenerator.generate());
		log.setTaskId(task.getTaskId());
		log.setTaskName(taskName);
		log.setInstanceId(task.getInstanceId());
		log.setUserName(taskName);
		log.setUseralias(taskName);
		log.setTime(new Date());
		log.setDetail(opt);
		WorkflowLogDao.getInstance().save(log);
	}

	/***
	 * 返回完成任务日志
	 * 
	 * @param instanceId
	 *            instanceId
	 * @return List<WorkflowLog>
	 */
	public List<WorkflowLog> getInstanceCompleteTasks(String instanceId) {
		WorkflowInstance ins = runtimeService.getInstanceById(instanceId);
		String parentInsId = ins.getParentInstanceId();
		List<WorkflowLog> logs = new ArrayList<WorkflowLog>();
		List<WorkflowLog> clogs = WorkflowLogDao.getInstance().getByInstanceId(instanceId);
		logs.addAll(clogs);
		if (parentInsId != null && !parentInsId.equals("")) {
			List<WorkflowLog> plogs = getInstanceCompleteTasks(parentInsId);
			logs.addAll(plogs);
		}
		this.logSortByTime(logs);
		return logs;
	}

	/**
	 * 
	 * @param logs
	 *            logs
	 */
	public void logSortByTime(List<WorkflowLog> logs) {
		WorkflowLog tLog;
		int size = logs.size();
		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				if (logs.get(i).getTime().getTime() > logs.get(j).getTime().getTime()) {
					tLog = logs.get(i);
					logs.set(i, logs.get(j));
					logs.set(j, tLog);
				}
			}
		}
	}

	/**
	 * 回退到前一步
	 * 
	 * @param taskId
	 *            taskId
	 * @param usrId
	 *            usrId
	 */
	public void backToPre(String taskId, String usrId) {
		completeTask(taskId, usrId, "backToPre");
	}

	/**
	 * 审核通过
	 * 
	 * @param taskId
	 *            taskId
	 * @param usrId
	 *            usrId
	 * @return boolean
	 */
	public boolean auditPass(String taskId, String usrId) {
		if (usrId != null && usrId.equals("system")) {
			WorkflowTask task = taskDao.getByTaskId(taskId);
			WorkflowLog log = new WorkflowLog();
			log.setId(UUIDGenerator.generate());
			log.setTaskId(task.getTaskId());
			log.setTaskName(task.getTaskName());
			log.setInstanceId(task.getInstanceId());
			String detail = StringUtil.getLanguageValue("SystemAuditPass", CommonConfiguration.getInstance().getLocale());
			log.setUserName(detail);
			log.setUseralias(detail);
			log.setTime(new Date());
			log.setDetail(detail);
			WorkflowLogDao.getInstance().save(log);
		}
		return completeTask(taskId, usrId, "complete");
	}

	/**
	 * 回退到开始
	 * 
	 * @param taskId
	 *            taskId
	 * @param usrId
	 *            usrId
	 * @return boolean
	 */
	public boolean backToStart(String taskId, String usrId) {
		return completeTask(taskId, usrId, "backToStart");
	}

	/**
	 * 完成任务
	 * 
	 * @param taskId
	 *            taskId
	 * @param usrId
	 *            usrId
	 * @param state
	 *            state
	 * @return boolean
	 */
	public boolean completeTask(String taskId, String usrId, String state) {
		WorkflowTask task = taskDao.getByTaskId(taskId);
		// 如果流程不存在，则不进行流转
		WorkflowInfo info = RepositoryService.getInstance().getInfo(task.getWorkflowId());
		if (info == null) {
			throw new SmartbiException(DataAuditingErrorCode.CANNOT_TO_COMPLETE_TASK);
		}
		task.setTaskState(state);
		task.setTaskOperator(usrId);
		task.setTaskOperateTime(new Date());
		taskDao.update(task);
		runtimeService.dispatch(task.getInstanceId(), state);
		//如果流程实例的创建者为system，改为当前操作用户
		WorkflowInstance ins = runtimeService.getInstanceById(task.getInstanceId());
		if ("system".equals(ins.getInstanceCreatorId())) { // 流程计划的创建者为system
			ins.setInstanceCreatorId(usrId);
			WorkflowInstanceDao.getInstance().update(ins);
		}
		return true;
	}

	/**
	 * 创建任务
	 * 
	 * @param instanceId
	 *            instanceId
	 * @param workflowId
	 *            workflowId
	 * @param activityName
	 *            activityName
	 * @param resourceId
	 *            resourceId
	 * @param rule
	 *            rule
	 * @param assigneeType
	 *            assigneeType
	 * @param assignee
	 *            assignee
	 * @param opt
	 *            opt
	 * @return WorkflowTask
	 */
	public WorkflowTask createTask(String instanceId, String workflowId, String activityName, String resourceId,
			String rule, String assigneeType, String assignee, String opt) {
		WorkflowTask task = new WorkflowTask();
		task.setTaskId(UUIDGenerator.generate());
		task.setAssigneeType(assigneeType);
		task.setAssignee(assignee);
		task.setInstanceId(instanceId);
		task.setWorkflowId(workflowId);
		task.setTaskName(activityName);
		task.setResourceId(resourceId);
		task.setTaskState("unfinished");
		task.setTaskOpt(opt);
		task.setTaskRule(rule);
		task.setCreateTime(new Date());
		taskDao.save(task);
		return task;
	}
}
