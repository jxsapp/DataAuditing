package smartbi.auditing.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import smartbi.SmartbiException;
import smartbi.auditing.DataAuditingErrorCode;
import smartbi.auditing.repository.Activity;
import smartbi.auditing.repository.Define;
import smartbi.auditing.repository.WorkflowInfo;
import smartbi.auditing.repository.WorkflowInfoDao;
import smartbi.auditing.repository.WorkflowResourceDefine;
import smartbi.auditing.repository.WorkflowResourceDefineDao;
import smartbi.catalogtree.CatalogElement;
import smartbi.catalogtree.CatalogTreeModule;
import smartbi.catalogtree.PurviewType;
import smartbi.framework.IModule;
import smartbi.net.sf.json.JSONArray;
import smartbi.net.sf.json.JSONObject;
import smartbi.param.IParameter;
import smartbi.repository.IDAOModule;
import smartbi.scheduletask.repository.ScheduleTaskStorageService;
import smartbi.scheduletask.runneragent.ScheduleSDK;
import smartbi.spreadsheetreport.SpreadsheetReport;
import smartbi.spreadsheetreport.SpreadsheetReportModule;
import smartbi.spreadsheetreport.core.SpreadsheetReportParameterPanel;
import smartbi.state.IStateModule;
import smartbi.user.IDepartment;
import smartbi.user.IGroup;
import smartbi.user.IUser;
import smartbi.usermanager.Group;
import smartbi.usermanager.User;
import smartbi.usermanager.UserManagerModule;
import smartbi.util.StringUtil;
import smartbi.util.XmlUtility;

/**
 * 
 * @author huangpeng
 *
 */
public class ManagerService implements IModule {

	/** */
	private static ManagerService instance;
	/** */
	private UserManagerModule userManagerModule = UserManagerModule.getInstance();
	/** */
	private IStateModule stateModule;
	/** */
	private CatalogTreeModule catalogTreeModule = CatalogTreeModule.getInstance();

	/** */
	private IDAOModule daoModule;

	/**
	 * 
	 * @return ManagerService
	 */
	public static ManagerService getInstance() {
		if (instance == null) {
			instance = new ManagerService();
		}
		return instance;
	}

	/**
	 * 
	 */
	protected ManagerService() {
	}

	/**
	 * 
	 */
	public void activate() {
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
	 * @param info
	 *            info
	 * @param resDefine
	 *            resDefine
	 */
	public void startInstanceByResDefine(WorkflowInfo info, WorkflowResourceDefine resDefine) {
		String workflowId = info.getId();
		String infoId = info.getId();
		String initRole = info.getWorkflowInitiateRole();
		JSONArray initRolesJson = JSONArray.fromString(initRole);
		SpreadsheetReport spreadsheetReport = SpreadsheetReportModule.getInstance()
				.getSpreadsheetReport(resDefine.getSpreadsheetId());

		// 获取电子表格sheet名
		List<String> sheetNames = new ArrayList<String>();
		Document doc = XmlUtility.parse(spreadsheetReport.getDefine());
		NodeList sheets = doc.getElementsByTagName("Sheet");
		for (int i = 0; i < sheets.getLength(); i++) {
			Element item = (Element) sheets.item(i);
			String sheetName = item.getAttribute("n");
			sheetNames.add(sheetName);
		}

		Iterator rIt = initRolesJson.iterator();
		Map<String, String> groupsMap = new HashMap<String, String>();
		Set<String> userset = new HashSet<String>();
		while (rIt.hasNext()) {
			JSONObject roleJson = (JSONObject) rIt.next();
			String rType = roleJson.optString("type");

			if (rType.equals("ROLE")) {
				String roleValue = roleJson.optString("value");
				// 获得角色下所有组
				Set<IUser> users = (Set<IUser>) userManagerModule.getAllUsersOfRole(roleValue);
				for (IUser user : users) {
					List<String> groupsStr = userManagerModule.getDefaultGroups(user.getId());
					for (String groupStr : groupsStr) {
						Group tgroup = userManagerModule.getGroupById(groupStr);
						if (!groupsMap.containsKey(tgroup.getName())) {
							groupsMap.put(tgroup.getName(), user.getId());
						}
					}
				}
			} else if (rType.equals("USER")) {
				String value = roleJson.optString("value");
				userset.add(value);
			}
		}

		String[] mechanismParameters = new String[] {};
		if (!StringUtil.isNullOrEmpty(resDefine.getMechanismParameter())) {
			mechanismParameters = resDefine.getMechanismParameter().split(",");
		}
		String[] otherParameters = resDefine.getOtherParameters().split(",");
		Define define = RepositoryService.getInstance().parseDefine(info.getId(), info.getWorkflowDefine()); 
		String first = define.getPaths().get(0).getTo();
		Activity act = RuntimeService.getInstance().getActivitByIndex(define, first);
		String vsType = act == null ? "" : act.getVisiableType();
		IUser currentUser = userManagerModule.getStateModule().getCurrentUser();
		List<IParameter> outputParameters = null;
		if (StringUtil.isNullOrEmpty(vsType) || vsType.equals("no")) {
			outputParameters = getOutputParameters(userManagerModule.getCurrentUser().getId(), resDefine.getSpreadsheetId());
		}
		for (String userId : userset) {
			if (!StringUtil.isNullOrEmpty(vsType) && vsType.equals("yes") || vsType.equals("sameOrganization")) {
				outputParameters = getOutputParameters(userId, resDefine.getSpreadsheetId());
			}
			IDepartment dept = userManagerModule.getDefaultDepartment(userId);
			startInstance(workflowId, userId, dept == null ? "" : dept.getName(), resDefine, 
					otherParameters, mechanismParameters, sheetNames, outputParameters);
		}
		for (String key : groupsMap.keySet()) {
			String displayValue = key;
			IGroup group = userManagerModule.getGroupByName(key);
			if (group != null) {
				displayValue = group.getAlias();
			}
			String userId = groupsMap.get(key);
			if (!userset.contains(userId)) {
				if (!StringUtil.isNullOrEmpty(vsType) && vsType.equals("yes") || vsType.equals("sameOrganization")) {
					outputParameters = getOutputParameters(userId, resDefine.getSpreadsheetId());
				}
				startInstance(workflowId, userId, displayValue, resDefine, 
						otherParameters, mechanismParameters, sheetNames, outputParameters);
			}
		}
		userManagerModule.getStateModule().setCurrentUser(currentUser);
	}

	private List<IParameter> getOutputParameters(String userId, String spreadsheetId) {
		User user = userManagerModule.getUserById(userId);
		if (user == null) {
			return null;
		}
		List<IParameter> outputParameters = null;
		String parameterPanelClientId = null;
		try {
			userManagerModule.getStateModule().setCurrentUser(user);
			Method method = SpreadsheetReportModule.class.getMethod("openQuery", String.class);
			Object sreport = method.invoke(SpreadsheetReportModule.getInstance(), spreadsheetId);
			Method getParameterPanelClientId = sreport.getClass().getMethod("getParameterPanelClientId");
			parameterPanelClientId = (String) getParameterPanelClientId.invoke(sreport);
			SpreadsheetReportParameterPanel panel = (SpreadsheetReportParameterPanel) userManagerModule.getStateModule().getSessionAttribute(parameterPanelClientId);
			Method getOutputParameters = sreport.getClass().getMethod("getOutputParameters");
			outputParameters = (List<IParameter>) getOutputParameters.invoke(sreport);
			for (IParameter param : outputParameters) {
				ArrayList<String> defaultParam = panel.getParamDefaultValue(param.getId());
				if (defaultParam != null && defaultParam.size() >= 2) {
					param.setValue(defaultParam.get(0), defaultParam.get(1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outputParameters;
	}
	
	private void startInstance(String workflowId, String userId, String displayValue, WorkflowResourceDefine resDefine, 
			String[] otherParameters, String[] mechanismParameters, List<String> sheetNames, List<IParameter> outputParameters) {
		IParameter mechanismParma = null;
		List<IParameter> otherParmas = new ArrayList<IParameter>();
		// 查找流程定义中设置的机构参数和其他参数
		for (IParameter param : outputParameters) {
			for (int i = 0; i < mechanismParameters.length; i++) {
				if (mechanismParameters[i].equals(param.getName())) {
					mechanismParma = param;
				}
			}
			for (int j = 0; j < otherParameters.length; j++) {
				if (otherParameters[j].equals(param.getName())) {
					otherParmas.add(param);
				}
			}
		}
		// 取参数默认值为启动值
		String otherParmasStr = "[";
		int count = 0;
		for (IParameter otherParma : otherParmas) {
			if (count > 0) {
				otherParmasStr += ",";
			}
			otherParmasStr += "{\"name\":\"" + otherParma.getName() + "\",\"value\":\"" + otherParma.getValue()
					+ "\",\"displayValue\":\"" + otherParma.getDisplayValue() + "\"}";
			count++;
		}
		otherParmasStr += "]";
		String mechanismParmasStr = "[";
		if (mechanismParma != null) {
			mechanismParmasStr += "{\"name\":\"" + mechanismParma.getName() + "\",\"value\":\"" + displayValue
					+ "\",\"displayValue\":\"" + displayValue + "\"}";
		} else {
			userId = "system"; // 如果机构参数为空，流程创建者用system，在上报的时候再改为上报操作用户
		}
		mechanismParmasStr += "]";
		if (resDefine.isBySheet()) {
			for (String sheetName : sheetNames) {
				RuntimeService.getInstance().startInstanceById(workflowId,
						resDefine.getSpreadsheetId() + "_" + sheetName, mechanismParmasStr, otherParmasStr, userId);
			}
		} else {
			RuntimeService.getInstance().startInstanceById(workflowId, resDefine.getSpreadsheetId(),
					mechanismParmasStr, otherParmasStr, userId);
		}
	}
	
	/**
	 * 
	 * @param workflowId
	 *            workflowId
	 */
	public void startInstanceBySchedule(String workflowId) {
		WorkflowInfo info = WorkflowInfoDao.getInstance().load(workflowId);
		List<WorkflowResourceDefine> resDefines = WorkflowResourceDefineDao.getInstance().getByWorkflowId(info.getId());
		for (WorkflowResourceDefine resDefine : resDefines) {
			this.startInstanceByResDefine(info, resDefine);
		}
	}

	/**
	 * 根据流程创建任务和计划
	 * 
	 * @param workflowInfo
	 *            流程
	 */
	public void createSchedule(WorkflowInfo workflowInfo) {
		createScheduleWithAlias(workflowInfo, null);
	}

	/**
	 * 根据流程使用指定别名创建任务和计划
	 * 
	 * @param workflowInfo
	 *            流程
	 * @param alias
	 *            指定的别名
	 */
	public void createScheduleWithAlias(WorkflowInfo workflowInfo, String alias) {
		String cycle = workflowInfo.getWorkflowLifeCycle();
		String script = "importPackage(Packages.smartbi.auditing.sdk);"
				+ "var auditingService =  ClientAuditingService(connector);"
				+ "auditingService.startInstanceBySchedule('" + workflowInfo.getId() + "');";
		if (isExistSchedule(workflowInfo)) {
			deleteSchedule(workflowInfo);
		}
		
		if (StringUtil.isNullOrEmpty(alias) || scheduleAndTaskAliasExists(alias)) {
			// 在复制流程时，可能会出现计划/任务别名冲突的情况，而计划/任务的别名是不可以重复的，需要获取唯一的别名
			String name = workflowInfo.getAlias();
			if (StringUtil.isNullOrEmpty(name)) {
				name = workflowInfo.getName();
			}
			alias = getScheduleAndTaskAliasByWorkflow(name);
		}
		
		String taskids = this.createScheduleTask(workflowInfo.getId(), alias,
				workflowInfo.getDesc(), script);
		this.createTimeSchedule(workflowInfo.getId(), alias, workflowInfo.getDesc(),
				taskids, cycle);
	}
	
	/**
	 * 获取流程自动生成的任务/计划的别名
	 * 
	 * @param workflowInfo
	 *            流程
	 * @return 流程自动生成的任务/计划的别名
	 */
	public String getWorkflowCreateAlias(WorkflowInfo workflowInfo) {
		CatalogElement taskWorkflowCatalog = catalogTreeModule.getChildElementByName("TASKS", "workflow");
		CatalogElement scheduleWorkflowCatalog = catalogTreeModule.getChildElementByName("SCHEDULES", "workflow");
		if (taskWorkflowCatalog == null || scheduleWorkflowCatalog == null) {
			return "";
		}
		CatalogElement taskElem = catalogTreeModule.getChildElementByName(taskWorkflowCatalog.getId(),
				workflowInfo.getId());
		CatalogElement scheduleElem = catalogTreeModule.getChildElementByName(scheduleWorkflowCatalog.getId(),
				workflowInfo.getId());
		if (taskElem == null || scheduleElem == null) {
			return "";
		}
		String taskAlias = taskElem.getAlias();
		String scheduleAlias = scheduleElem.getAlias();
		if (taskAlias.equals(scheduleAlias)) {
			return taskAlias;
		} else {
			return "";
		}
	}

	/**
	 * 判断指定别名是否已经被其他流程生成的任务和计划占用
	 * 
	 * @param alias
	 *            待判断别名
	 * @return 指定别名是否已经被其他流程生成的任务和计划占用
	 */
	private boolean scheduleAndTaskAliasExists(String alias) {
		CatalogElement taskWorkflowCatalog = catalogTreeModule.getChildElementByName("TASKS", "workflow");
		CatalogElement scheduleWorkflowCatalog = catalogTreeModule.getChildElementByName("SCHEDULES", "workflow");
		if (taskWorkflowCatalog == null || scheduleWorkflowCatalog == null) {
			return false;
		}
		List<CatalogElement> taskChildren = catalogTreeModule.getAllChildren(taskWorkflowCatalog.getId());
		for (CatalogElement child : taskChildren) {
			String n = child.getAlias();
			if (StringUtil.isNullOrEmpty(n)) {
				continue;
			}
			if (n.equals(alias)) {
				return true;
			}
		}
		List<CatalogElement> scheduleChildren = catalogTreeModule.getAllChildren(scheduleWorkflowCatalog.getId());
		for (CatalogElement child : scheduleChildren) {
			String n = child.getAlias();
			if (StringUtil.isNullOrEmpty(n)) {
				continue;
			}
			if (n.equals(alias)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据流程名称获取一个合适的别名，供自生成的任务和计划使用
	 * 
	 * @param name
	 *            流程名称
	 * @return 别名
	 */
	private String getScheduleAndTaskAliasByWorkflow(String name) {
		CatalogElement taskWorkflowCatalog = catalogTreeModule.getChildElementByName("TASKS", "workflow");
		CatalogElement scheduleWorkflowCatalog = catalogTreeModule.getChildElementByName("SCHEDULES", "workflow");
		if (taskWorkflowCatalog == null || scheduleWorkflowCatalog == null) {
			return name;
		}

		if (!scheduleAndTaskAliasExists(name)) {
			return name;
		}

		List<Integer> suffix = new ArrayList<Integer>();
		List<CatalogElement> taskChildren = catalogTreeModule.getAllChildren(taskWorkflowCatalog.getId());
		for (CatalogElement child : taskChildren) {
			String n = child.getAlias();
			if (StringUtil.isNullOrEmpty(n)) {
				continue;
			}
			Matcher m = Pattern.compile("(?<=^" + name + "_)\\d+$").matcher(n); // 获取后缀数字
			if (m.find()) {
				int f = Integer.parseInt(m.group());
				suffix.add(f);
			}
		}
		List<CatalogElement> scheduleChildren = catalogTreeModule.getAllChildren(scheduleWorkflowCatalog.getId());
		for (CatalogElement child : scheduleChildren) {
			String n = child.getAlias();
			if (StringUtil.isNullOrEmpty(n)) {
				continue;
			}
			Matcher m = Pattern.compile("(?<=^" + name + "_)\\d+$").matcher(n); // 获取后缀数字
			if (m.find()) {
				int f = Integer.parseInt(m.group());
				suffix.add(f);
			}
		}

		Collections.sort(suffix);
		if (suffix.size() == 0) {
			return name + "_2";
		} else {
			int last = suffix.get(suffix.size() - 1);
			return name + "_" + (last + 1);
		}
	}

	/**
	 * 
	 * @param workflowInfo
	 *            workflowInfo
	 */
	public void deleteSchedule(WorkflowInfo workflowInfo) {
		CatalogElement taskWorkflowCatalog = catalogTreeModule.getChildElementByName("TASKS", "workflow");
		CatalogElement scheduleWorkflowCatalog = catalogTreeModule.getChildElementByName("SCHEDULES", "workflow");
		if (taskWorkflowCatalog == null || scheduleWorkflowCatalog == null) {
			return;
		}
		CatalogElement taskElem = catalogTreeModule.getChildElementByName(taskWorkflowCatalog.getId(),
				workflowInfo.getId());
		CatalogElement scheduleElem = catalogTreeModule.getChildElementByName(scheduleWorkflowCatalog.getId(),
				workflowInfo.getId());
		if (scheduleElem != null) {
			catalogTreeModule.deleteCatalogElement(scheduleElem.getId());
		}
		if (taskElem != null) {
			catalogTreeModule.deleteCatalogElement(taskElem.getId());
		}

	}

	/**
	 * 
	 * @param workflowInfo
	 *            workflowInfo
	 * @return boolean
	 */
	public boolean isExistSchedule(WorkflowInfo workflowInfo) {
		CatalogElement taskWorkflowCatalog = catalogTreeModule.getChildElementByName("TASKS", "workflow");
		CatalogElement scheduleWorkflowCatalog = catalogTreeModule.getChildElementByName("SCHEDULES", "workflow");
		if (taskWorkflowCatalog == null) {
			return false;
		}
		if (scheduleWorkflowCatalog == null) {
			return false;
		}
		CatalogElement taskElem = catalogTreeModule.getChildElementByName(taskWorkflowCatalog.getId(),
				workflowInfo.getId());
		CatalogElement scheduleElem = catalogTreeModule.getChildElementByName(scheduleWorkflowCatalog.getId(),
				workflowInfo.getId());
		if (taskElem == null) {
			return false;
		}
		if (scheduleElem == null) {
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @param name
	 *            name
	 * @param alias
	 *            alias
	 * @param desc
	 *            desc
	 * @param script
	 *            script
	 * @return String
	 */
	public String createScheduleTask(String name, String alias, String desc, String script) {
		CatalogElement workflowCatalog = catalogTreeModule.getChildElementByName("TASKS", "workflow");
		if (workflowCatalog == null) {
			workflowCatalog = catalogTreeModule.createFolderElement("TASKS", "workflow", "workflow", "workflow", null,
					false, "");
		}
		if (workflowCatalog.getId() != null) {
			boolean accessible = catalogTreeModule.isAccessible(workflowCatalog.getId(), PurviewType.WRITE);
			if (!accessible) {
				throw new SmartbiException(DataAuditingErrorCode.NO_RIGHT_OF_SCHEDULETASK);
			}
		}
		return ScheduleTaskStorageService.getInstance().creatSelfDefineTask(name, alias, desc, script,
				workflowCatalog.getId());
	}

	/**
	 * 
	 * @param name
	 *            name
	 * @param alias
	 *            alias
	 * @param desc
	 *            desc
	 * @param taskids
	 *            taskids
	 * @param cycle
	 *            cycle
	 */
	public void createTimeSchedule(String name, String alias, String desc, String taskids, String cycle) {
		CatalogElement workflowCatalog = catalogTreeModule.getChildElementByName("SCHEDULES", "workflow");
		JSONObject cycleJson = JSONObject.fromString(cycle);
		String type = cycleJson.optString("type");
		if (workflowCatalog == null) {
			workflowCatalog = catalogTreeModule.createFolderElement("SCHEDULES", "workflow", "workflow", "workflow",
					null, false, "");
		}

		stateModule = userManagerModule.getStateModule();
		User user = (User) stateModule.getCurrentUser();
		if (type.equals("day")) {
			String sid = ScheduleTaskStorageService.getInstance().creatTimeSchedule(name, alias, desc, taskids, true,
					"0", "0", "", "12", "00", "DAY", "0 0 12 /1 * ? *", true, user.getName(), user.getPassword(), "",
					workflowCatalog.getId(), false, "", "");
			ScheduleSDK.getInstance().active(sid);
		}

		if (type.equals("month")) {
			String monthDay = cycleJson.optString("monthDay");
			String sid = ScheduleTaskStorageService.getInstance().creatTimeSchedule(name, alias, desc, taskids, true,
					"0", "0", "", "12", "00", "MONTH", "0 0 12 " + monthDay + " 1,2,3,4,5,6,7,8,9,10,11,12 ? *", true,
					user.getName(), "", "", workflowCatalog.getId(), false, "", "");
			ScheduleSDK.getInstance().active(sid);
		}
		if (type.equals("quarter")) {
			String quarterMonth = cycleJson.optString("quarterMonth");
			String quarterDay = cycleJson.optString("quarterDay");
			int q1 = 0 + Integer.parseInt(quarterMonth);
			int q2 = 3 + Integer.parseInt(quarterMonth);
			int q3 = 6 + Integer.parseInt(quarterMonth);
			int q4 = 9 + Integer.parseInt(quarterMonth);
			String quarterTimeStr = "0 0 12 " + quarterDay + " " + q1 + "," + q2 + "," + q3 + "," + q4 + " ? *";

			String sid = ScheduleTaskStorageService.getInstance().creatTimeSchedule(name, alias, desc, taskids, true,
					"0", "0", "", "12", "00", "MONTH", quarterTimeStr, true, user.getName(), user.getPassword(), "",
					workflowCatalog.getId(), false, "", "");
			ScheduleSDK.getInstance().active(sid);
		}
	}

}
