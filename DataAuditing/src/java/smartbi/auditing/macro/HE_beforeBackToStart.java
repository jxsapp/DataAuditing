package smartbi.auditing.macro;

import smartbi.macro.HostEvent;
import smartbi.macro.ModuleType;
import smartbi.spreadsheetreport.macro.HO_SpreadsheetReport;

public interface HE_beforeBackToStart extends HostEvent {
	/** */
	String name = "beforeBackToStart";
	/** */
	String displayName = "beforeBackToStart(${WorkflowBeforeBackToStart})";
	/** */
	ModuleType moduleType = ModuleType.ClientSide;
	/** */
	Class<?>[] parameters = new Class[] { HO_SpreadsheetReport.class, HO_CurrentNode.class };

}
