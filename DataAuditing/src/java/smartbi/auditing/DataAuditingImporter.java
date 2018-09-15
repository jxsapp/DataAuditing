package smartbi.auditing;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import smartbi.auditing.repository.WorkflowInfo;
import smartbi.auditing.repository.WorkflowInfoDao;
import smartbi.auditing.service.RepositoryService;
import smartbi.catalogtree.ICatalogElement;
import smartbi.catalogtree.ICatalogTreeModule;
import smartbi.catalogtree.ImportResourceItem;
import smartbi.catalogtree.Importer;
import smartbi.macro.IMacroService;
import smartbi.util.XmlUtility;

/**
 * 流程定义导入
 * 
 */
public class DataAuditingImporter extends Importer {

	/**
	 * 
	 * @param catalogTreeModule catalogTreeModule
	 */
	public DataAuditingImporter(ICatalogTreeModule catalogTreeModule) {
		super(catalogTreeModule);
	}

	@Override
	public boolean accept(ImportResourceItem item) {
		DataAuditingElementType type = null;
		try {
			type = DataAuditingElementType.valueOf(item.getType());
			switch (type) {
			case WORKFLOW:
				return true;
			default:
				return false;
			}
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	@Override
	public void importResource(ImportResourceItem item, ICatalogElement parent) {
		DataAuditingElementType type = null;
		try {
			type = DataAuditingElementType.valueOf(item.getType());
			switch (type) {
			case WORKFLOW:
				importWorkflow(item, parent);
				break;
			default:
				break;
			}
		} catch (IllegalArgumentException e) {
			//
		}
	}

	private void importWorkflow(ImportResourceItem item, ICatalogElement parent) {
		String id = item.getId();

		WorkflowInfo info = RepositoryService.getInstance().getInfo(id);
		boolean exists = (info != null);
		if (!exists) {
			info = new WorkflowInfo();
			info.setId(id);
		}

		Element element = item.getElement();
		String name = item.getName();
		String alias = item.getAlias();
		String desc = element.getAttribute("desc");
		Element workflowDefineElem = XmlUtility.getChildElementByTagName(element, "workflowDefine");
		Element workflowInitiateRoleElem = XmlUtility.getChildElementByTagName(element, "workflowInitiateRole");
		Element workflowLifeCycleElem = XmlUtility.getChildElementByTagName(element, "workflowLifeCycle");
		info.setName(name);
		info.setAlias(alias);
		info.setDesc(desc);
		info.setWorkflowType(element.getAttribute("workflowType"));
		info.setWorkflowDefine(XmlUtility.getTextContent(workflowDefineElem));
		info.setWorkflowInitiateRole(XmlUtility.getTextContent(workflowInitiateRoleElem));
		info.setWorkflowLifeCycle(XmlUtility.getTextContent(workflowLifeCycleElem));

		String spreadsheetId = "";
		String mechanismParameter = "";
		String otherParameters = "";
		boolean isBySheet = false;
		Element reportsElement = XmlUtility.getChildElementByTagName(element, "reports");
		if (reportsElement != null) {
			NodeList children = reportsElement.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Element child = (Element) children.item(i);
				spreadsheetId = spreadsheetId + (i == 0 ? "" : ",") + child.getAttribute("reportId");
				if (i == 0) {
					mechanismParameter = child.getAttribute("mp");
					otherParameters = child.getAttribute("op");
					isBySheet = "true".equals(child.getAttribute("bySheet"));
				}
			}
		}
		if (!exists) {
			WorkflowInfoDao.getInstance().save(info);
			catalogTreeModule.createResourceNode(parent.getId(), info, DataAuditingElementType.WORKFLOW.name());
		}
		RepositoryService.getInstance().updateWorkFlowInfo(info, spreadsheetId, mechanismParameter, otherParameters,
				isBySheet);

		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Element child = (Element) children.item(i);
			if (child.getTagName().equals("macro")) {
				importMacro(child);
			}
		}
	}

	private void importMacro(Element element) {
		if (element == null) {
			return;
		}
		Element modulesElement = XmlUtility.getChildElementByTagName(element, "modules");
		IMacroService macroService = RepositoryService.getInstance().getMacroService();

		if (modulesElement != null) {
			NodeList children = modulesElement.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Element child = (Element) children.item(i);
				macroService.importMacroModule(child);
			}
		}
	}

	@Override
	public void importByType(String type, Object[] params) {
		//
	}

}
