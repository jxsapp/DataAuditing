package smartbi.auditing;

import java.util.ArrayList;
import java.util.List;

import smartbi.auditing.repository.WorkflowResourceDefine;
import smartbi.auditing.service.RepositoryService;
import smartbi.catalogtree.ICatalogElement;
import smartbi.catalogtree.ICatalogTreeModule;
import smartbi.macro.IResourcePack;
import smartbi.metadata.IReference;
import smartbi.metadata.assist.RefResource;
import smartbi.util.StringUtil;

/**
 * 记录审核流程对电子表格的引用
 */
public class DataAuditingReference implements IReference {
	/** */
	@SuppressWarnings("unused")
	private List<? extends IResourcePack> resourcePacks = null;

	/**
	 * {@inheritDoc}
	 */
	public boolean accept(String type) {
		return StringUtil.equals(type, DataAuditingElementType.WORKFLOW.name());
	}

	/**
	 * {@inheritDoc}
	 */
	public RefResource getResource(ICatalogElement catalog) {
		if (catalog == null || !accept(catalog.getType())) {
			return null;
		}
		ICatalogTreeModule treeModule = RepositoryService.getInstance().getCatalogTreeModule();
		DataAuditingElementType elementType = DataAuditingElementType.valueOf(catalog.getType());

		String id = catalog.getId();
		String path = treeModule.getCatalogElementFullPath(id);
		List<String> refIdList = new ArrayList<String>();
		List<String> affIdList = new ArrayList<String>();
		String content = null;

		switch (elementType) {
		case WORKFLOW:
			//引用到的电子表格id
			List<WorkflowResourceDefine> rdList = RepositoryService.getInstance().getResourceDefineByWorkflowId(id);
			if (rdList != null && rdList.size() > 0) {
				for (WorkflowResourceDefine rd : rdList) {
					refIdList.add(rd.getSpreadsheetId());
				}
			}
			break;
		default:
			break;
		}

		return new RefResource(refIdList, affIdList, content, path);
	}

	/**
	 * {@inheritDoc}
	 */
	public void resetObjectCache() {
		resourcePacks = null;
	}

}
