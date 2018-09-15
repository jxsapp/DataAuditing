package smartbi.auditing;

import smartbi.util.MessageHelper;

/*
 * 新增或修改枚举项的时候请同步更新与类名同名的properties文件
 */
public enum DataAuditingErrorCode {
	/** */
	CANNOT_TO_COMPLETE_TASK, NO_RIGHT_OF_SPREADSHEED, NO_RIGHT_OF_SCHEDULETASK, SUMMARY_PARAM_IS_EMPTY, OPERATER_IS_EMPTY;

	/** */
	private static final MessageHelper HELPER = MessageHelper.getInstance(DataAuditingErrorCode.class);

	/**
	 * @return message
	 */
	public String toString() {
		return HELPER.getMessage(this.name());
	}
}
