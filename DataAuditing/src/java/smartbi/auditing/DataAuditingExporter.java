package smartbi.auditing;

import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import smartbi.SmartbiException;
import smartbi.auditing.repository.WorkflowInfo;
import smartbi.auditing.repository.WorkflowResourceDefine;
import smartbi.auditing.service.RepositoryService;
import smartbi.catalogtree.Exporter;
import smartbi.catalogtree.ICatalogElement;
import smartbi.catalogtree.ICatalogTreeModule;
import smartbi.freequery.FreeQueryErrorCode;
import smartbi.macro.IMacroModule;
import smartbi.macro.IMacroService;
import smartbi.usermanager.IUserManagerModule;
import smartbi.util.StringUtil;
import smartbi.util.XmlUtility;

/**
 * 流程定义导出
 */
public class DataAuditingExporter extends Exporter {

	/**
	 * @param userManagerModule userManagerModule
	 * @param catalogTreeModule catalogTreeModule
	 */
	public DataAuditingExporter(IUserManagerModule userManagerModule, ICatalogTreeModule catalogTreeModule) {
		super(userManagerModule, catalogTreeModule);
	}

	@Override
	public boolean accept(ICatalogElement element) {
		return StringUtil.equals(DataAuditingElementType.WORKFLOW.name(), element.getType());
	}

	@Override
	public void export(ICatalogElement element, TransformerHandler handler) throws SAXException {
		String id = element.getId();
		WorkflowInfo info = RepositoryService.getInstance().getInfo(id);
		if (info == null) {
			throw new SmartbiException(FreeQueryErrorCode.REPORT_NOT_EXIST).setDetail(id);
		}

		AttributesImpl attrs = buildAttributes(element);
		XmlUtility.addAttribute(attrs, "workflowType", info.getWorkflowType());
		handler.startElement("", "", element.getType(), attrs);

		exportPermission(element, handler);

		attrs = new AttributesImpl();
		handler.startElement("", "", "workflowDefine", attrs);
		handler.startCDATA();
		char[] buff = info.getWorkflowDefine().toCharArray();
		handler.characters(buff, 0, buff.length);
		handler.endCDATA();
		handler.endElement("", "", "workflowDefine");

		attrs = new AttributesImpl();
		handler.startElement("", "", "workflowInitiateRole", attrs);
		handler.startCDATA();
		buff = info.getWorkflowInitiateRole().toCharArray();
		handler.characters(buff, 0, buff.length);
		handler.endCDATA();
		handler.endElement("", "", "workflowInitiateRole");

		attrs = new AttributesImpl();
		handler.startElement("", "", "workflowLifeCycle", attrs);
		handler.startCDATA();
		buff = info.getWorkflowLifeCycle().toCharArray();
		handler.characters(buff, 0, buff.length);
		handler.endCDATA();
		handler.endElement("", "", "workflowLifeCycle");

		//导出关联报表
		List<WorkflowResourceDefine> rdList = RepositoryService.getInstance().getResourceDefineByWorkflowId(id);
		handler.startElement("", "", "reports", new AttributesImpl());
		for (WorkflowResourceDefine rd : rdList) {
			AttributesImpl rdAttrs = new AttributesImpl();
			XmlUtility.addAttribute(rdAttrs, "id", rd.getId());
			XmlUtility.addAttribute(rdAttrs, "reportId", rd.getSpreadsheetId());
			XmlUtility.addAttribute(rdAttrs, "mp", rd.getMechanismParameter());
			XmlUtility.addAttribute(rdAttrs, "op", rd.getOtherParameters());
			XmlUtility.addAttribute(rdAttrs, "bySheet", rd.isBySheet() ? "true" : "false");

			handler.startElement("", "", "report", rdAttrs);
			handler.endElement("", "", "report");
		}
		handler.endElement("", "", "reports");

		// 导出报表宏信息
		handler.startElement("", "", "macro", new AttributesImpl());
		exportMacroModule(element, handler);
		handler.endElement("", "", "macro");

		handler.endElement("", "", element.getType());
	}

	private void exportMacroModule(ICatalogElement element, TransformerHandler handler) throws SAXException {
		IMacroService macroService = RepositoryService.getInstance().getMacroService();
		List<? extends IMacroModule> macroModules = macroService.getMacroModulesByResId(element.getId());
		handler.startElement("", "", "modules", new AttributesImpl());
		for (IMacroModule module : macroModules) {
			macroService.createMacroModuleElement(module, handler);
		}
		handler.endElement("", "", "modules");
	}
}
