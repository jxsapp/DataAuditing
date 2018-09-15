package smartbi.auditing.macro;

import smartbi.macro.HostEvent;
import smartbi.macro.ModuleType;
import smartbi.spreadsheetreport.macro.HO_SpreadsheetReport;

public interface HE_afterBackToStart extends HostEvent {
	/** */
	String name = "afterBackToStart";
	/** */
	String displayName = "afterBackToStart(${WorkflowAfterBackToStart})";
	/** */
	ModuleType moduleType = ModuleType.ClientSide;
	/** */
	Class<?>[] parameters = new Class[] { HO_SpreadsheetReport.class, HO_CurrentNode.class };
}
