package smartbi.auditing.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import smartbi.CommonConfiguration;
import smartbi.SmartbiException;
import smartbi.auditing.DataAuditingDragHandler;
import smartbi.auditing.DataAuditingDuplicator;
import smartbi.auditing.DataAuditingElementType;
import smartbi.auditing.DataAuditingErrorCode;
import smartbi.auditing.DataAuditingExporter;
import smartbi.auditing.DataAuditingImporter;
import smartbi.auditing.DataAuditingLogType;
import smartbi.auditing.DataAuditingReference;
import smartbi.auditing.macro.WorkflowAdapter;
import smartbi.auditing.macro.WorkflowOutline;
import smartbi.auditing.repository.Activity;
import smartbi.auditing.repository.Assignee;
import smartbi.auditing.repository.Define;
import smartbi.auditing.repository.Life;
import smartbi.auditing.repository.Path;
import smartbi.auditing.repository.WorkflowInfo;
import smartbi.auditing.repository.WorkflowInfoDao;
import smartbi.auditing.repository.WorkflowResInfo;
import smartbi.auditing.repository.WorkflowResource;
import smartbi.auditing.repository.WorkflowResourceDao;
import smartbi.auditing.repository.WorkflowResourceDefine;
import smartbi.auditing.repository.WorkflowResourceDefineDao;
import smartbi.catalogtree.ICatalogElement;
import smartbi.catalogtree.ICatalogTreeModule;
import smartbi.catalogtree.PurviewType;
import smartbi.framework.IModule;
import smartbi.macro.IMacroService;
import smartbi.macro.adapters.ResourceAdapterFactory;
import smartbi.metadata.IMetadataModule;
import smartbi.net.sf.json.JSONArray;
import smartbi.net.sf.json.JSONObject;
import smartbi.oltp.FreeQueryModule;
import smartbi.param.IParameter;
import smartbi.repository.IDAOModule;
import smartbi.repository.OperationLogModule;
import smartbi.spreadsheetreport.SpreadsheetParamsHandler;
import smartbi.state.IStateModule;
import smartbi.usermanager.Group;
import smartbi.usermanager.GroupToRole;
import smartbi.usermanager.Role;
import smartbi.usermanager.User;
import smartbi.usermanager.UserManagerErrorCode;
import smartbi.usermanager.UserManagerModule;
import smartbi.util.StringUtil;
import smartbi.util.UUIDGenerator;

/**
 * 
 * @author huangpeng
 *
 */
public class RepositoryService implements IModule {

	/** */
	private static RepositoryService instance;
	/** */
	private WorkflowResourceDao resourceDao = WorkflowResourceDao.getInstance();
	/** */
	private WorkflowInfoDao workflowInfoDao = WorkflowInfoDao.getInstance();

	/** */
	private UserManagerModule userManagerModule = UserManagerModule.getInstance();
	/** */
	private IStateModule stateModule;

	/** */
	private IDAOModule daoModule;

	/**
	 * @return RepositoryService
	 */
	public static RepositoryService getInstance() {
		if (instance == null) {
			instance = new RepositoryService();
		}
		return instance;
	}

	/** */
	private ICatalogTreeModule catalogTreeModule;

	/** */
	private IMetadataModule metadataModule;

	/** */
	private IMacroService macroService;

	/** */
	private static final Logger LOG = Logger.getLogger(RepositoryService.class);

	/**
	 * @return catalogTreeModule
	 */
	public ICatalogTreeModule getCatalogTreeModule() {
		return catalogTreeModule;
	}

	/**
	 * @param catalogTreeModule
	 *            catalogTreeModule
	 */
	public void setCatalogTreeModule(ICatalogTreeModule catalogTreeModule) {
		this.catalogTreeModule = catalogTreeModule;
	}

	/**
	 * @return metadataModule
	 */
	public IMetadataModule getMetadataModule() {
		return metadataModule;
	}

	/**
	 * @param metadataModule
	 *            metadataModule
	 */
	public void setMetadataModule(IMetadataModule metadataModule) {
		this.metadataModule = metadataModule;
	}

	/**
	 * @return macroService
	 */
	public IMacroService getMacroService() {
		return macroService;
	}

	/**
	 * @param macroService
	 *            macroService
	 */
	public void setMacroService(IMacroService macroService) {
		this.macroService = macroService;
	}

	/** */
	protected RepositoryService() {
	}

	/** */
	public void activate() {
		catalogTreeModule.registerDAO(DataAuditingElementType.WORKFLOW.name(), WorkflowInfoDao.getInstance());
		catalogTreeModule.registerNonChildElementType(DataAuditingElementType.WORKFLOW.name());

		catalogTreeModule.addDragListener(new DataAuditingDragHandler());
		catalogTreeModule.registerDuplicator(new DataAuditingDuplicator());
		catalogTreeModule.registerExporter(new DataAuditingImporter(catalogTreeModule),
				new DataAuditingExporter(userManagerModule, catalogTreeModule));
		metadataModule.registerReference(new DataAuditingReference());

		macroService.registerOutline(DataAuditingElementType.WORKFLOW.name(), WorkflowOutline.class);
		ResourceAdapterFactory.getInstance().registerAdapter("smartbi.auditing.repository.WorkflowInfo",
				WorkflowAdapter.class);

		daoModule.addPOJOClass(WorkflowInfo.class);
		daoModule.addPOJOClass(WorkflowResource.class);
		daoModule.addPOJOClass(WorkflowResourceDefine.class);
		// daoModule.addPOJOClass(WorkflowJob.class);
	}

	/**
	 * @return IDAOModule
	 */
	public IDAOModule getDaoModule() {
		return daoModule;
	}

	/**
	 * @param daoModule
	 *            daoModule
	 */
	public void setDaoModule(IDAOModule daoModule) {
		this.daoModule = daoModule;
	}

	/** */
	private SpreadsheetParamsHandler paramHandler = new SpreadsheetParamsHandler();

	/**
	 * 
	 * @param reportId
	 *            reportId
	 * @return List<IParameter>
	 */
	public List<IParameter> getParametersBySpreadsheetId(String reportId) {
		List<IParameter> rtn = paramHandler.getParameters(reportId);
		return rtn;
	}

	/**
	 * 打开流程定义，并返回前端用于展示的属性
	 * 
	 * @param workFlowId
	 *            workFlowId
	 * @return Object[]
	 */
	public Object[] openWorkFlowInfo(String workFlowId) {
		boolean accessible2 = catalogTreeModule.isAccessible(workFlowId, PurviewType.READ);
		if (!accessible2) {
			return null;
		}
		Object[] rtn = new Object[4];
		WorkflowInfo info = WorkflowInfoDao.getInstance().load(workFlowId);
		rtn[0] = info;

		if (info != null) {
			List<WorkflowResourceDefine> rdList = WorkflowResourceDefineDao.getInstance().getByWorkflowId(info.getId());
			rtn[1] = rdList;
			Map<String, ICatalogElement> spreadsheetReportList = new HashMap<String, ICatalogElement>();
			rtn[2] = spreadsheetReportList;

			for (WorkflowResourceDefine rd : rdList) {
				boolean accessible = catalogTreeModule.isAccessible(rd.getSpreadsheetId(), PurviewType.REF);
				if (!accessible) {
					continue;
				}
				spreadsheetReportList.put(rd.getSpreadsheetId(),
						catalogTreeModule.getCatalogElementById(rd.getSpreadsheetId()));
			}

			List<IParameter> reportParameterList = new ArrayList<IParameter>();
			if (rdList.size() > 0) {
				reportParameterList = getParametersBySpreadsheetId(rdList.get(0).getSpreadsheetId());
			}
			if (reportParameterList == null) {
				reportParameterList = new ArrayList<IParameter>();
			}

			Map<String, IParameter> parameterList = new HashMap<String, IParameter>();
			rtn[3] = parameterList;

			for (WorkflowResourceDefine rd : rdList) {
				if (!StringUtil.isNullOrEmpty(rd.getMechanismParameter())) {
					for (IParameter p : reportParameterList) {
						if (StringUtil.equals(p.getName(), rd.getMechanismParameter())) {
							parameterList.put(rd.getMechanismParameter(), p);
							break;
						}
					}
				}
				String otherParameter = rd.getOtherParameters();
				if (!StringUtil.isNullOrEmpty(otherParameter)) {
					List<String> nameList = StringUtil.split(otherParameter, ",");
					for (String name : nameList) {
						for (IParameter p : reportParameterList) {
							if (StringUtil.equals(p.getName(), name)) {
								parameterList.put(name, p);
								break;
							}
						}
					}
				}
			}
			userManagerModule = UserManagerModule.getInstance();
			stateModule = userManagerModule.getStateModule();
			String userName = stateModule.getCurrentUser().getName();
			String userAlias = stateModule.getCurrentUser().getAlias();
			String sessionId = stateModule.getSession().getId();
			String detail = "{id:'" + info.getId() + "',name:'" + info.getName() + "',alias:'" + info.getAlias()
					+ "',path:'"
					+ FreeQueryModule.getInstance().getCatalogTreeModule().getCatalogElementFullPath(info.getId())
					+ "'}";
			OperationLogModule.getInstance().addOperationLog(userName, userAlias,
					DataAuditingLogType.BrowsesDataAuditingLogType.getMsg(), detail, sessionId, null);
		}

		return rtn;
	}

	/**
	 * 保存流程定义
	 * 
	 * @param workFlowInfo
	 *            workFlowInfo
	 * @param spreadsheetId
	 *            spreadsheetId
	 * @param mechanismParameter
	 *            mechanismParameter
	 * @param otherParameters
	 *            otherParameters
	 * @param isBySheet
	 *            isBySheet
	 * @param name
	 *            name
	 * @param alias
	 *            alias
	 * @param desc
	 *            desc
	 * @param folderId
	 *            folderId
	 * @return String
	 */
	public String saveAsWorkFlowInfo(WorkflowInfo workFlowInfo, String spreadsheetId, String mechanismParameter,
			String otherParameters, boolean isBySheet, String name, String alias, String desc, String folderId) {
		catalogTreeModule.assertAccessible(folderId, PurviewType.WRITE);
		ICatalogElement folder = catalogTreeModule.getCatalogElementById(folderId);
		if (folder.getType().equals("SELF_TREENODE")) {
			throw new SmartbiException(DataAuditingErrorCode.CANNOT_TO_COMPLETE_TASK);
		}
		WorkflowInfo newInfo = new WorkflowInfo();
		try {
			PropertyUtils.copyProperties(newInfo, workFlowInfo);
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}
		newInfo.setId(UUIDGenerator.generate());
		newInfo.setName(name);
		newInfo.setAlias(alias);
		newInfo.setDesc(desc);
		workflowInfoDao.save(newInfo);

		resetWorkflowResourceDefine(spreadsheetId, mechanismParameter, otherParameters, newInfo.getId(), isBySheet);

		catalogTreeModule.createResourceNode(folderId, newInfo, DataAuditingElementType.WORKFLOW.name());
		// 创建计划任务
		JSONObject cycleJson = JSONObject.fromString(newInfo.getWorkflowLifeCycle());
		String type = cycleJson.optString("type");
		if (type.equals("nothing")) {
			newInfo.setWorkflowType("manual");
		} else { // 自启动流程计划任务权限判断
			assertSchuduleTaskAccessible();
			newInfo.setWorkflowType("auto");
			ManagerService.getInstance().createSchedule(newInfo);
		}
		workflowInfoDao.update(newInfo);
		userManagerModule = UserManagerModule.getInstance();
		stateModule = userManagerModule.getStateModule();
		String userName = stateModule.getCurrentUser().getName();
		String userAlias = stateModule.getCurrentUser().getAlias();
		String sessionId = stateModule.getSession().getId();
		String detail = "{id:'" + newInfo.getId() + "',name:'" + newInfo.getName() + "',alias:'" + newInfo.getAlias()
				+ "',path:'"
				+ FreeQueryModule.getInstance().getCatalogTreeModule().getCatalogElementFullPath(newInfo.getId())
				+ "'}";
		OperationLogModule.getInstance().addOperationLog(userName, userAlias,
				DataAuditingLogType.NewDataAuditingLogType.getMsg(), detail, sessionId, null);
		return newInfo.getId();
	}

	/**
	 * 更新流程定义
	 * 
	 * @param workFlowInfo
	 *            workFlowInfo
	 * @param spreadsheetId
	 *            spreadsheetId
	 * @param mechanismParameter
	 *            mechanismParameter
	 * @param otherParameters
	 *            otherParameters
	 * @param isBySheet
	 *            isBySheet
	 * @return true
	 */
	public boolean updateWorkFlowInfo(WorkflowInfo workFlowInfo, String spreadsheetId, String mechanismParameter,
			String otherParameters, boolean isBySheet) {
		catalogTreeModule.assertAccessible(workFlowInfo.getId(), PurviewType.WRITE);
		WorkflowInfo old = WorkflowInfoDao.getInstance().load(workFlowInfo.getId());
		try {
			PropertyUtils.copyProperties(old, workFlowInfo);
		} catch (Exception e) {
			LOG.error(e);
			return false;
		}
		WorkflowInfoDao.getInstance().update(old);

		JSONObject cycleJson = JSONObject.fromString(workFlowInfo.getWorkflowLifeCycle());
		String type = cycleJson.optString("type");
		if (type.equals("nothing")) {
			old.setWorkflowType("manual");
			ManagerService.getInstance().deleteSchedule(old);
		} else { // 自启动流程计划任务权限判断
			assertSchuduleTaskAccessible();
			old.setWorkflowType("auto");
			String oldAlias = ManagerService.getInstance().getWorkflowCreateAlias(old);
			ManagerService.getInstance().deleteSchedule(old);
			ManagerService.getInstance().createScheduleWithAlias(old, oldAlias);
		}

		resetWorkflowResourceDefine(spreadsheetId, mechanismParameter, otherParameters, old.getId(), isBySheet);
		catalogTreeModule.updateCatalogElement(old.getId(), old.getAlias(), old.getDesc());
		userManagerModule = UserManagerModule.getInstance();
		stateModule = userManagerModule.getStateModule();
		String userName = stateModule.getCurrentUser().getName();
		String userAlias = stateModule.getCurrentUser().getAlias();
		String sessionId = stateModule.getSession().getId();
		String detail = "{id:'" + old.getId() + "',name:'" + old.getName() + "',alias:'" + old.getAlias() + "',path:'"
				+ FreeQueryModule.getInstance().getCatalogTreeModule().getCatalogElementFullPath(old.getId()) + "'}";
		OperationLogModule.getInstance().addOperationLog(userName, userAlias,
				DataAuditingLogType.ModifyDataAuditingLogType.getMsg(), detail, sessionId, null);
		return true;
	}

	private void assertSchuduleTaskAccessible() {
		userManagerModule = UserManagerModule.getInstance();
		if (!userManagerModule.isCurUserFuncTypeAccessible("CUSTOM_SCHEDULETASK_TASK")) {
			throw new SmartbiException(UserManagerErrorCode.NO_PERMISSION).setDetail(
					StringUtil.getLanguageValue("Schedule_Task", CommonConfiguration.getInstance().getLocale()));
		} // 任务操作权限
		if (!userManagerModule.isCurUserFuncTypeAccessible("CUSTOM_SCHEDULETASK_SCHEDULE")) {
			throw new SmartbiException(UserManagerErrorCode.NO_PERMISSION).setDetail(
					StringUtil.getLanguageValue("Schedule_Schedule", CommonConfiguration.getInstance().getLocale()));
		} // 计划操作权限

		ICatalogElement taskWorkflowCatalog = catalogTreeModule.getChildElementByName("TASKS", "workflow");
		ICatalogElement schedulesWorkflowCatalog = catalogTreeModule.getChildElementByName("SCHEDULES", "workflow");
		if (taskWorkflowCatalog != null && taskWorkflowCatalog != null) {
			boolean taskWorkflowAccessible = catalogTreeModule.isAccessible(taskWorkflowCatalog.getId(),
					PurviewType.WRITE);
			boolean schedulesWorkflowAccessible = catalogTreeModule.isAccessible(schedulesWorkflowCatalog.getId(),
					PurviewType.WRITE);
			if (!taskWorkflowAccessible || !schedulesWorkflowAccessible) {
				throw new SmartbiException(DataAuditingErrorCode.NO_RIGHT_OF_SCHEDULETASK);
			}
		}
	}

	/**
	 * 覆盖已有的流程定义
	 * 
	 * @param workFlowInfo
	 *            workFlowInfo
	 * @param spreadsheetId
	 *            spreadsheetId
	 * @param mechanismParameter
	 *            mechanismParameter
	 * @param otherParameters
	 *            otherParameters
	 * @param replacedReportId
	 *            replacedReportId
	 * @param isBySheet
	 *            isBySheet
	 * @param desc
	 *            desc
	 * @return true
	 */
	public boolean overwriteFlowInfo(WorkflowInfo workFlowInfo, String spreadsheetId, String mechanismParameter,
			String otherParameters, boolean isBySheet, String replacedReportId ,  String desc) {
		catalogTreeModule.assertAccessible(replacedReportId, PurviewType.WRITE);
		WorkflowInfo old = WorkflowInfoDao.getInstance().load(replacedReportId);
			String name = old.getName();
			String alias = old.getAlias();
			try {
				PropertyUtils.copyProperties(old, workFlowInfo);
			} catch (Exception e) {
				LOG.error(e);
				return false;
			}
			old.setId(replacedReportId);
			old.setName(name);
			old.setAlias(alias);
			WorkflowInfoDao.getInstance().update(old);
			
			JSONObject cycleJson = JSONObject.fromString(workFlowInfo.getWorkflowLifeCycle());
			String type = cycleJson.optString("type");
			if (type.equals("nothing")) {
				old.setWorkflowType("manual");
				ManagerService.getInstance().deleteSchedule(old);
			} else { // 自启动流程计划任务权限判断
				assertSchuduleTaskAccessible();
				old.setWorkflowType("auto");
				ManagerService.getInstance().createSchedule(old);
			}

			resetWorkflowResourceDefine(spreadsheetId, mechanismParameter, otherParameters, old.getId(), isBySheet);
			catalogTreeModule.updateCatalogElement(replacedReportId, old.getAlias(), desc);
			userManagerModule = UserManagerModule.getInstance();
			stateModule = userManagerModule.getStateModule();
			String userName = stateModule.getCurrentUser().getName();
			String userAlias = stateModule.getCurrentUser().getAlias();
			String sessionId = stateModule.getSession().getId();
			String detail = "{id:'" + old.getId() + "',name:'" + old.getName() + "',alias:'" + old.getAlias()
					+ "',path:'"
					+ FreeQueryModule.getInstance().getCatalogTreeModule().getCatalogElementFullPath(old.getId())
					+ "'}";
			OperationLogModule.getInstance().addOperationLog(userName, userAlias,
					DataAuditingLogType.ModifyDataAuditingLogType.getMsg(), detail, sessionId, null);
			return true;
		
	}

	/**
	 * 
	 * @param spreadsheetId
	 *            spreadsheetId
	 * @param mechanismParameter
	 *            mechanismParameter
	 * @param otherParameters
	 *            otherParameters
	 * @param workflowId
	 *            workflowId
	 * @param isBySheet
	 *            isBySheet
	 */
	private void resetWorkflowResourceDefine(String spreadsheetId, String mechanismParameter, String otherParameters,
			String workflowId, boolean isBySheet) {
		if (!StringUtil.isNullOrEmpty(spreadsheetId)) {
			WorkflowResourceDefineDao.getInstance().deleteByWorkflowId(workflowId);
			List<String> idList = StringUtil.split(spreadsheetId, ",");
			for (String id : idList) {
				if (!StringUtil.isNullOrEmpty(id)) {
					// 一个电子表格只能绑定一个流程，因此把原来的绑定删除
					removeBinding(id);
					WorkflowResourceDefine rd = new WorkflowResourceDefine();
					rd.setId(UUIDGenerator.generate());
					rd.setSpreadsheetId(id);
					rd.setWorkflowId(workflowId);
					rd.setMechanismParameter(mechanismParameter);
					rd.setOtherParameters(otherParameters);
					rd.setBySheet(isBySheet);
					WorkflowResourceDefineDao.getInstance().save(rd);
				}
			}
		}
	}

	/**
	 * 获取所有绑定
	 * 
	 * @param workflowId
	 *            workflowId
	 * @return List<WorkflowResourceDefine>
	 */
	public List<WorkflowResourceDefine> getWorkflowResourceDefineByWorkflowId(String workflowId) {
		return WorkflowResourceDefineDao.getInstance().getByWorkflowId(workflowId);
	}

	/**
	 * 删除绑定
	 * 
	 * @param spreadsheetId
	 *            spreadsheetId
	 * @return true
	 */
	public boolean removeBinding(String spreadsheetId) {
		WorkflowResourceDefine define = WorkflowResourceDefineDao.getInstance().getBySpreadsheetId(spreadsheetId);
		WorkflowResourceDefineDao.getInstance().deleteBySpreadsheetId(spreadsheetId);
		if (define != null) {
			ICatalogElement elem = catalogTreeModule.getCatalogElementById(define.getWorkflowId());
			if (elem != null) {
				catalogTreeModule.updateCatalogElement(elem.getId(), elem.getAlias(), elem.getDesc());
			}
		}
		return true;
	}

	/**
	 * 绑定新的流程
	 * 
	 * @param workflowId
	 *            workflowId
	 * @param spreadsheetId
	 *            spreadsheetId
	 * @param mechanismParameter
	 *            mechanismParameter
	 * @param otherParameters
	 *            otherParameters
	 * @param isBySheet
	 *            isBySheet
	 * @return true
	 */
	public boolean addBinding(String workflowId, String spreadsheetId, String mechanismParameter,
			String otherParameters, boolean isBySheet) {
		// 一个电子表格只能绑定一个流程，因此把原来的绑定删除
		removeBinding(spreadsheetId);

		WorkflowResourceDefine rd = new WorkflowResourceDefine();
		rd.setId(UUIDGenerator.generate());
		rd.setSpreadsheetId(spreadsheetId);
		rd.setWorkflowId(workflowId);
		rd.setMechanismParameter(mechanismParameter);
		rd.setOtherParameters(otherParameters);
		rd.setBySheet(isBySheet);
		WorkflowResourceDefineDao.getInstance().save(rd);

		ICatalogElement elem = catalogTreeModule.getCatalogElementById(workflowId);
		if (elem != null) {
			catalogTreeModule.updateCatalogElement(elem.getId(), elem.getAlias(), elem.getDesc());
		}

		return true;
	}

	/**
	 * 解析流程定义
	 * 
	 * @param id
	 *            id
	 * @param wfDefine
	 *            wfDefine
	 * @return Define
	 */

	public Define parseDefine(String id, String wfDefine) {
		Define define = new Define();
		define.setId(id);
		JSONObject defineJson = JSONObject.fromString(wfDefine);
		JSONObject activitysJson = defineJson.optJSONObject("activitys");
		JSONObject pathsJson = defineJson.optJSONObject("paths");
		List<Activity> activitys = parseActivity(activitysJson);
		List<Path> paths = parsePath(pathsJson);
		define.setActivitys(activitys);
		define.setPaths(paths);
		return define;
	}

	/**
	 * 解析流程节点
	 * 
	 * @param activitysJson
	 *            activitysJson
	 * @return List<Activity>
	 */
	public List<Activity> parseActivity(JSONObject activitysJson) {
		Iterator actIt = activitysJson.keys();
		List<Activity> activitys = new ArrayList<Activity>();
		while (actIt.hasNext()) {
			Activity activity = new Activity();
			String key = (String) actIt.next();
			JSONObject activityJson = activitysJson.optJSONObject(key);
			activity.setId(activityJson.optString("id"));
			activity.setName(activityJson.optString("name"));
			activity.setType(activityJson.optString("type"));
			if (!activityJson.optString("type").equals("start") && !activityJson.optString("type").equals("end")) {
				activity.setOpt(activityJson.optString("opt"));
				activity.setRule(activityJson.optString("rule"));
				activity.setDesc(activityJson.optString("desc"));
				activity.setVisiableType(activityJson.optString("orgVisiable"));
				JSONArray assisJson = activityJson.optJSONArray("assignee");
				List<Assignee> assis = new ArrayList<Assignee>();
				Iterator assiIt = assisJson.iterator();
				while (assiIt.hasNext()) {
					Assignee assi = new Assignee();
					JSONObject asJson = (JSONObject) assiIt.next();
					assi.setType(asJson.optString("type"));
					assi.setValue(asJson.optString("value"));
					assis.add(assi);
				}
				activity.setAssignee(assis);
				JSONObject lifeJson = activityJson.optJSONObject("life");
				Life life = new Life();
				life.setType(lifeJson.optString("type"));
				life.setValue(lifeJson.optString("value"));
				activity.setLife(life);
			}
			activitys.add(activity);
		}
		return activitys;
	}

	/**
	 * 解析流程路径
	 * 
	 * @param pathsJson
	 *            pathsJson
	 * @return List<Path>
	 */
	public List<Path> parsePath(JSONObject pathsJson) {
		Iterator pathIt = pathsJson.keys();
		List<Path> paths = new ArrayList<Path>();
		while (pathIt.hasNext()) {
			Path path = new Path();
			String key = (String) pathIt.next();
			JSONObject pathJson = pathsJson.optJSONObject(key);
			path.setFrom(pathJson.getString("from"));
			path.setTo(pathJson.getString("to"));
			paths.add(path);
		}
		return paths;
	}

	/**
	 * 通过电子表格ID返回流程定义第一个节点相关信息
	 * 
	 * @param spreedSheetId
	 *            spreedSheetId
	 * @return WorkflowInfo
	 */
	public Activity getFristActivityBySpreedSheetId(String spreedSheetId) {

		WorkflowResourceDefine resDefine = this.getResourceDefineBySpreadsheetId(spreedSheetId);
		WorkflowInfo info = this.getInfo(resDefine.getWorkflowId());
		Define define = this.parseDefine(info.getId(), info.getWorkflowDefine());
		Activity startAct = RuntimeService.getInstance().getStartActivity(define);
		Activity fristAct = RuntimeService.getInstance().getNextActivity(startAct.getId(), define);
		return fristAct;
	}

	/**
	 * 返回当前角色下所有的可启动的流程信息
	 * 
	 * @param
	 * @return List<WorkflowInfo>
	 */
	public List<WorkflowInfo> getAllWorkflowInfoWidthAuthority() {
		List<WorkflowInfo> resultInfos = new ArrayList<WorkflowInfo>();
		List<WorkflowInfo> workflowInfos = this.workflowInfoDao.getAllInfo();
		userManagerModule = UserManagerModule.getInstance();
		stateModule = userManagerModule.getStateModule();
		User user = userManagerModule.getUserById(stateModule.getCurrentUser().getId());
		for (WorkflowInfo workflowInfo : workflowInfos) {
			String initiateRoleStr = workflowInfo.getWorkflowInitiateRole();
			JSONArray initiateRoles = JSONArray.fromString(initiateRoleStr);
			@SuppressWarnings("rawtypes")
			Iterator iterator = initiateRoles.iterator();
			while (iterator.hasNext()) {
				JSONObject initiateRole = (JSONObject) iterator.next();
				String value = initiateRole.optString("value");
				for (Role role : user.getAssignedRoles()) {
					if (value.equals(role.getId())) {
						resultInfos.add(workflowInfo);
					}
				}
				if (value.equals(user.getId())) {
					resultInfos.add(workflowInfo);
				}

				for (Group group : user.getAssignedGroups()) {
					if (value.equals(group.getId())) {
						resultInfos.add(workflowInfo);
					}
				}
			}
		}
		return resultInfos;
	}
	
	/**
	 * 返回当前角色下所有的可启动的流程以及对应电子表格相关信息
	 * 
	 * @param
	 * @return List<WorkflowResInfo>
	 */
	public List<WorkflowResInfo> getAllInfoByStartRoal() {
		List<WorkflowInfo> workflowInfos = this.workflowInfoDao.getAllInfo();
		List<WorkflowResInfo> ret = new ArrayList<WorkflowResInfo>();
		userManagerModule = UserManagerModule.getInstance();
		stateModule = userManagerModule.getStateModule();
		User user = userManagerModule.getUserById(stateModule.getCurrentUser().getId());
		for (WorkflowInfo workflowInfo : workflowInfos) {
			String initiateRoleStr = workflowInfo.getWorkflowInitiateRole();
			JSONArray initiateRoles = JSONArray.fromString(initiateRoleStr);
			Iterator iterator = initiateRoles.iterator();
			while (iterator.hasNext()) {
				JSONObject initiateRole = (JSONObject) iterator.next();
				String value = initiateRole.optString("value");
				for (Role role : user.getAssignedRoles()) {
					if (value.equals(role.getId())) {
						addWorkflowResInfos(ret, workflowInfo);
					}
				}
				if (value.equals(user.getId())) {
					addWorkflowResInfos(ret, workflowInfo);
				}

				for (Group group : user.getAssignedGroups()) {
					if (value.equals(group.getId())) {
						addWorkflowResInfos(ret, workflowInfo);
					}
				}
			}
		}
		return ret;
	}

	/**
	 * 添加流程及电子表格相关信息到返回数组中
	 * 
	 * @param ret
	 *            ret
	 * @param workflowInfo
	 *            workflowInfo
	 */
	public void addWorkflowResInfos(List<WorkflowResInfo> ret, WorkflowInfo workflowInfo) {
		List<WorkflowResourceDefine> resDefines = this.getResourceDefineByWorkflowId(workflowInfo.getId());
		for (WorkflowResourceDefine resDefine : resDefines) {
			WorkflowResInfo resInfo = new WorkflowResInfo();
			ICatalogElement elem = catalogTreeModule.getCatalogElementById(resDefine.getSpreadsheetId());
			if (elem != null) {
				resInfo.setSpreadsheetName(elem.getName());
			}
			resInfo.setResDefineId(resDefine.getId());
			resInfo.setSpreadsheetId(resDefine.getSpreadsheetId());
			resInfo.setMechanismParameter(resDefine.getMechanismParameter());
			resInfo.setOtherParameters(resDefine.getOtherParameters());
			resInfo.setWorkflowId(workflowInfo.getId());
			resInfo.setWorkflowName(workflowInfo.getName());
			resInfo.setWorkflowDesc(workflowInfo.getDesc());
			resInfo.setWorkflowType(workflowInfo.getWorkflowType());
			boolean flg = false;
			for (WorkflowResInfo wri : ret) {
				if (wri.getResDefineId().equals(resInfo.getResDefineId())) {
					flg = true;
				}
			}
			if (!flg) {
				ret.add(resInfo);
			}
		}
	}

	/**
	 * 根据电子表格ID判断当前角色下是否是启动角色
	 * 
	 * @param spreadSheetId
	 *            spreadSheetId
	 * @return WorkflowInfo
	 */
	public WorkflowInfo getInfoByStartRoalAndSpreadsheetId(String spreadSheetId) {
		WorkflowResourceDefine resDefine = WorkflowResourceDefineDao.getInstance().getBySpreadsheetId(spreadSheetId);
		if (resDefine == null) {
			return null;
		}
		userManagerModule = UserManagerModule.getInstance();
		stateModule = userManagerModule.getStateModule();
		User user = userManagerModule.getUserById(stateModule.getCurrentUser().getId());
		WorkflowInfo workflowInfo = this.getInfo(resDefine.getWorkflowId());
		if (workflowInfo == null) {
			return null;
		}
		boolean accessible2 = catalogTreeModule.isAccessible(workflowInfo.getId(), PurviewType.READ);
		if (!accessible2) {
			return null;
		}
		String initiateRoleStr = workflowInfo.getWorkflowInitiateRole();
		JSONArray initiateRoles = JSONArray.fromString(initiateRoleStr);
		Iterator iterator = initiateRoles.iterator();
		while (iterator.hasNext()) {
			JSONObject initiateRole = (JSONObject) iterator.next();
			String type = initiateRole.optString("type");
			String value = initiateRole.optString("value");
			if (value.equals(user.getId())) {
				return workflowInfo;
			}

			for (Role role : user.getAssignedRoles()) {
				if (value.equals(role.getId())) {
					return workflowInfo;
				}
			}

			for (Group group : user.getAssignedGroups()) {
				if (value.equals(group.getId())) {
					return workflowInfo;
				} else {
					List<GroupToRole> groupToRole = group.getGroupToRole();
					for (GroupToRole groupToRole2 : groupToRole) {
						if (value.equals(groupToRole2.getRole().getId())) {
							return workflowInfo;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 获得启动角色的第一个节点的操作信息
	 * 
	 * @param spreadSheetId
	 *            spreadSheetId
	 * @return String
	 */
	public String getOptByStartRoalAndSpreadsheetId(String spreadSheetId) {
		WorkflowInfo info = this.getInfoByStartRoalAndSpreadsheetId(spreadSheetId);
		if (info == null) {
			return null;
		}
		Define define = this.parseDefine(info.getId(), info.getWorkflowDefine());
		Activity startAct = RuntimeService.getInstance().getStartActivity(define);
		Activity fristAct = RuntimeService.getInstance().getNextActivity(startAct.getId(), define);
		return fristAct.getOpt();
	}

	/**
	 * 判断是否是每个SHEET页一个流程
	 * 
	 * @param spreadSheetId
	 *            spreadSheetId
	 * @return boolean
	 */
	public boolean isBySheet(String spreadSheetId) {
		WorkflowResourceDefine resDefine = WorkflowResourceDefineDao.getInstance().getBySpreadsheetId(spreadSheetId);
		return resDefine.isBySheet();

	}

	/**
	 * 获得所有资源定义
	 * 
	 * @return List<WorkflowResourceDefine>
	 */
	public List<WorkflowResourceDefine> getAllResourceDefine() {
		return WorkflowResourceDefineDao.getInstance().findAll();
	}

	/**
	 * 获得所有流程信息
	 * 
	 * @return List<WorkflowInfo>
	 */
	public List<WorkflowInfo> getAllWorkflowInfo() {
		return WorkflowInfoDao.getInstance().getAllInfo();
	}

	/**
	 * 根据流程ID获取流程信息
	 * 
	 * @param workFlowId
	 *            workFlowId
	 * @return WorkflowInfo
	 */
	public WorkflowInfo getInfo(String workFlowId) {
		return WorkflowInfoDao.getInstance().load(workFlowId);
	}

	/**
	 * 根据电子表格ID获取流程信息
	 * 
	 * @param spreadSheetId
	 *            spreadSheetId
	 * @return WorkflowInfo
	 */
	public WorkflowInfo getInfoBySpreadsheetId(String spreadSheetId) {

		WorkflowResourceDefine resDefine = this.getResourceDefineBySpreadsheetId(spreadSheetId);
		if (resDefine != null) {
			WorkflowInfo info = this.getInfo(resDefine.getWorkflowId());
			boolean accessible2 = catalogTreeModule.isAccessible(info.getId(), PurviewType.READ);
			if (!accessible2) {
				return null;
			}
			return info;
		}
		return null;
	}

	/**
	 * 根据电子表格ID获得资源定义
	 * 
	 * @param spreadSheetId
	 *            spreadSheetId
	 * @return WorkflowResourceDefine
	 */
	public WorkflowResourceDefine getResourceDefineBySpreadsheetId(String spreadSheetId) {
		return WorkflowResourceDefineDao.getInstance().getBySpreadsheetId(spreadSheetId);
	}

	/**
	 * 通过流程信息ID获得相关资源定义
	 * 
	 * @param workflowId
	 *            workflowId
	 * @return List<WorkflowResourceDefine>
	 */
	public List<WorkflowResourceDefine> getResourceDefineByWorkflowId(String workflowId) {
		return WorkflowResourceDefineDao.getInstance().getByWorkflowId(workflowId);
	}

	/**
	 * 通过ID获得流程资源
	 * 
	 * @param resId
	 *            resId
	 * @return WorkflowResource
	 */
	public WorkflowResource getResByResId(String resId) {
		return resourceDao.load(resId);
	}

	/**
	 * 通过电子表格ID及参数获得流程资源
	 * 
	 * @param spreadSheetId
	 *            spreadSheetId
	 * @param mechanismParam
	 *            mechanismParam
	 * @param otherParams
	 *            otherParams
	 * @return WorkflowResource
	 */
	public WorkflowResource getResBySpreadSheetAndParam(String spreadSheetId, String mechanismParam,
			String otherParams) {
		return resourceDao.getBySpreadSheetAndParam(spreadSheetId, mechanismParam, otherParams);
	}

}
