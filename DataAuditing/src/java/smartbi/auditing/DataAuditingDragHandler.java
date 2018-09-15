package smartbi.auditing;

import smartbi.catalogtree.ICatalogElement;
import smartbi.catalogtree.IDragListener;

/**
 * 资源拖动处理
 */
public class DataAuditingDragHandler implements IDragListener {

	/** {@inheritDoc} */
	public int canDrag(ICatalogElement element, ICatalogElement toDir) {
		String elemType = element.getType();
		String dirType = toDir.getType();
		String acceptType = DataAuditingElementType.WORKFLOW.name();
		String defType = smartbi.util.CatalogElementType.DEFAULT_TREENODE.name();
		String selfType = smartbi.util.CatalogElementType.SELF_TREENODE.name();
		if (acceptType.equals(elemType) && (defType.equals(dirType) || selfType.equals(dirType))) {
			return 1;
		} else {
			return -1;
		}
	}

}
