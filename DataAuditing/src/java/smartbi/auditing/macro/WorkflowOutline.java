package smartbi.auditing.macro;

import smartbi.macro.HostObject;
import smartbi.macro.IResourceOutline;
import smartbi.macro.ModuleType;
import smartbi.macro.ResourceType;
import smartbi.macro.scriptable.HO_Param;
import smartbi.macro.scriptable.chart.HO_Chart;
import smartbi.macro.scriptable.chart.HO_Point;

public class WorkflowOutline implements IResourceOutline {
	/** */
	public static final ResourceType WORKFLOW = new ResourceType("WORKFLOW", "WORKFLOW");

	/** */
	private String resourceId;

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public Class<? extends HostObject>[] getAvailableHostObjectClasses() {
		return new Class[] { HO_Workflow.class, HO_Chart.class, HO_Param.class, HO_Point.class };
	}

	/** {@inheritDoc} */
	public Class<? extends HostObject> getHostObjectClass(String objectName) {
		if (WORKFLOW.name().equalsIgnoreCase(objectName)) {
			return HO_Workflow.class;
		}
		return null;

	}

	/** {@inheritDoc} */
	public String[] getObjectNames(ModuleType moduleType) {
		if (ModuleType.ServerSide.equals(moduleType)) {
			return new String[] {};
		} else {
			return new String[] { WORKFLOW.name() };
		}
	}

	/** {@inheritDoc} */
	public String getResourceId() {
		return resourceId;
	}

	/** {@inheritDoc} */
	public ResourceType getResourceType() {
		return WORKFLOW;
	}

	/** {@inheritDoc} */
	public void init(String resourceId) {
		this.resourceId = resourceId;
	}

}