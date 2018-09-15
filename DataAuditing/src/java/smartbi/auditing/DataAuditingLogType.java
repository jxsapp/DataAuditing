package smartbi.auditing;

import smartbi.util.StringUtil;

public enum DataAuditingLogType {
	/**
	 * 新建流程定义
	 */
	NewDataAuditingLogType("NewDataAuditing"),
	
	/**
	 * 修改流程定义
	 */
	ModifyDataAuditingLogType("ModifyDataAuditing"),

	/**
	 * 删除流程定义
	 */
	DeleteDataAuditingLogType("DeleteDataAuditing"),
	/**
	 * 浏览流程定义
	 */
	BrowsesDataAuditingLogType("BrowsesDataAuditing");

	/**
	 * 信息
	 */
	private final String msgKey;

	/**
	 * 构造方法
	 */
	private DataAuditingLogType() {
		this.msgKey = null;
	}

	/**
	 * 构造方法
	 * 
	 * @param msgKey
	 *            信息
	 */
	private DataAuditingLogType(String msgKey) {
		this.msgKey = msgKey;
	}

	/**
	 * 获取信息
	 * 
	 * @return 信息
	 */
	public String getMsg() {
		return msgKey == null ? null : StringUtil.getLanguageValue(msgKey);
	}
}
