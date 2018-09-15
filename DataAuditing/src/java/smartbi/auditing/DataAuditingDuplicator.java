package smartbi.auditing;

import smartbi.auditing.repository.WorkflowInfo;
import smartbi.auditing.service.RepositoryService;
import smartbi.catalogtree.Duplicator;
import smartbi.catalogtree.ICatalogElement;
import smartbi.util.StringUtil;

/**
 * 流程定义复制
 */
public class DataAuditingDuplicator extends Duplicator {

	/**  */
	public DataAuditingDuplicator() {
		super(RepositoryService.getInstance().getCatalogTreeModule());
	}

	@Override
	public boolean accept(ICatalogElement srcElement) {
		return StringUtil.equals(DataAuditingElementType.WORKFLOW.name(), srcElement.getType());
	}

	@Override
	public String duplicate(ICatalogElement toElement, ICatalogElement srcElement, String name, String alias,
			String desc) {
		WorkflowInfo info = RepositoryService.getInstance().getInfo(srcElement.getId());

		// 一个电子表格只能绑定一个流程，因此流程和电子表格之间的关联信息不复制
		if (info != null) {
			RepositoryService.getInstance().getCatalogTreeModule().checkDuplicateName(toElement.getId(), name, alias);

			return RepositoryService.getInstance().saveAsWorkFlowInfo(info, null, null, null, false, name, alias, desc,
					toElement.getId());
		}

		return null;
	}

}
