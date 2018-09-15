package smartbi.freequery.expression.function;

import smartbi.freequery.metadata.SQLPart;
import smartbi.freequery.util.SQLPartType;
import smartbi.oltp.FreeQueryModule;

public class GetFlowCreatorID extends Function {

	@Override
	public boolean checkParams() {
		return true;
	}

	@Override
	public void execute() {
		Object flowInstanceId = FreeQueryModule.getInstance().getStateModule().getSessionAttribute("flowInstanceId");
		if (flowInstanceId != null && flowInstanceId.toString() != "") {
			StringBuilder sb = new StringBuilder();
			sb.append("(select c_instancecreatorid from t_ext_workflow_instance where c_instanceid = '");
			sb.append(flowInstanceId);
			sb.append("')");
			result.add(new SQLPart(SQLPartType.SQLSTR, sb.toString()));
		}
	}

	@Override
	public String getMDXValue() {
		// TODO Auto-generated method stub
		return null;
	}
}
