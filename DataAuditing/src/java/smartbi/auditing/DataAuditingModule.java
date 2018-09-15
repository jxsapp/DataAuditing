package smartbi.auditing;

import smartbi.framework.IModule;

//用于触发upgrade 和 postupgrade，无其它功能
public class DataAuditingModule implements IModule {
	/** */
	private static DataAuditingModule instance = null;

	/**
	 * getInstance
	 * @return DataAuditingModule
	 */
	public static DataAuditingModule getInstance() {
		if (instance == null) {
			instance = new DataAuditingModule();
		}
		return instance;
	}

	@Override
	public void activate() {

	}

}
