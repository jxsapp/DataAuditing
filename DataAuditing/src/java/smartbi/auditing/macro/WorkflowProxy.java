package smartbi.auditing.macro;

import smartbi.auditing.repository.WorkflowInfo;
import smartbi.macro.ResourceType;
import smartbi.macro.Tips;

public class WorkflowProxy implements HO_Workflow {
	/** */
	private WorkflowInfo info;

	/** */
	public WorkflowProxy(WorkflowInfo info) {
		this.info = info;
	}

	/**
	 * @return id
	 */
	@Tips("${HO_Resource_getId}")
	public String getId() {
		return info.getId();
	}

	/**
	 * @return resType
	 */
	@Tips("${HO_Resource_getResourceType}")
	public ResourceType getResourceType() {
		return WorkflowOutline.WORKFLOW;
	}

	/**
	 * @return clientId
	 */
	@Tips("ClientId")
	public String getClientId() {
		return info.getId();
	}

}
