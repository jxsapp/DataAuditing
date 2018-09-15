package smartbi.auditing.sdk;

import smartbi.sdk.ClientConnector;

public class ClientAuditingService {
	/**
	 * 客户端连接器
	 */
	protected ClientConnector connector;
	
	/**
	 * @param connector
	 *            客户端连接器
	 */
	public ClientAuditingService(ClientConnector connector) {
		this.connector = connector;
	}
	
	
	/**
	 * 
	 * @param workflowId workflowId
	 */
	public void startInstanceBySchedule(String workflowId) {
		connector.remoteInvoke("RuntimeService", "startInstanceBySchedule", new Object[] { workflowId });
	}
}
