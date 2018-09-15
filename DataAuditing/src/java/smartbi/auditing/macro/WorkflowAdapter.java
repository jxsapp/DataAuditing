package smartbi.auditing.macro;

import java.util.HashMap;
import java.util.Map;

import smartbi.auditing.repository.WorkflowInfo;
import smartbi.macro.DefaultLogger;
import smartbi.macro.HostObject;
import smartbi.macro.IResourceAdapter;
import smartbi.macro.IResourceOutline;
import smartbi.macro.adapters.ResourceOutlineFactory;
import smartbi.macro.scriptable.HO_Application;
import smartbi.macro.scriptable.HO_Logger;
import smartbi.macro.scriptable.HO_Resource;

public class WorkflowAdapter implements IResourceAdapter {
	/** */
	private WorkflowInfo info;
	/** */
	private WorkflowProxy proxy;
	/** */
	private IResourceOutline outline;
	/** */
	private HO_Application application;
	/** */
	private HO_Logger logger;
	/** */
	private String targetName;
	/** */
	private String eventName;
	/** */
	private Map<String, ? extends HostObject> targets;
	/** */
	private Object[] args;
	/** */
	private Object[] arguments;

	/**
	 * @param resourceBO resourceBO
	 * @param targetName targetName
	 * @param eventName eventName
	 * @param args args
	 */
	public void init(Object resourceBO, String targetName, String eventName, Object[] args) {
		this.info = (WorkflowInfo) resourceBO;
		this.outline = ResourceOutlineFactory.getInstance().createOutline(this.info.getId());
		this.targetName = targetName;
		this.eventName = eventName;
		this.args = args;
	}

	/**
	 * @return null
	 */
	public HO_Application getApplication() {
		return application;
	}

	/**
	 * @return logger
	 */
	public HO_Logger getLogger() {
		if (logger == null) {
			logger = new DefaultLogger();
		}
		return logger;
	}

	/**
	 * @return targetName
	 */
	public String getTargetName() {
		return targetName;
	}

	/**
	 * @return eventName
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 * @return insightProxy
	 */
	public HO_Resource getResource() {
		if (proxy == null) {
			proxy = new WorkflowProxy(info);
		}
		return proxy;
	}

	/**
	 * @return targets
	 */
	public Map<String, ? extends HostObject> getTargets() {
		if (targets == null) {
			Map map = new HashMap();
			map.put(WorkflowOutline.WORKFLOW.name(), proxy);
			targets = map;
		}
		return targets;
	}

	/**
	 * @return arguments
	 */
	public Object[] getArguments() {
		if (arguments == null) {
			arguments = new HostObject[0];
		}
		return arguments;
	}

	/**
	 * 
	 * @return outline
	 */
	public IResourceOutline getOutline() {
		return outline;
	}

	/**
	 * 
	 * @param outline outline
	 */
	public void setOutline(IResourceOutline outline) {
		this.outline = outline;
	}

	/**
	 * 
	 * @param application application
	 */
	public void setApplication(HO_Application application) {
		this.application = application;
	}
}
