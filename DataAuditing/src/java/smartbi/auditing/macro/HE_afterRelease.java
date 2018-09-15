package smartbi.auditing.macro;

import smartbi.macro.HostEvent;
import smartbi.macro.ModuleType;
import smartbi.spreadsheetreport.macro.HO_SpreadsheetReport;

public interface HE_afterRelease extends HostEvent {
	/** */
	String name = "afterRelease";
	/** */
	String displayName = "afterRelease(${WorkflowAfterRelease})";
	/** */
	ModuleType moduleType = ModuleType.ClientSide;
	/** */
	Class<?>[] parameters = new Class[] { HO_SpreadsheetReport.class, HO_CurrentNode.class };

}
