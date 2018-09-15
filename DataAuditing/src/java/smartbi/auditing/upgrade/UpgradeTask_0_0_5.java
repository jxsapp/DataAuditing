package smartbi.auditing.upgrade;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import smartbi.repository.UpgradeTask;
import smartbi.util.DBType;

/**
 * 知识库升级类
 */
public class UpgradeTask_0_0_5 extends UpgradeTask {

	/** */
	private static final Logger LOG = Logger.getLogger(UpgradeTask_New.class);

	/**
	 * {@inheritDoc}
	 */
	public boolean doUpgrade(Connection conn, DBType type) {
		Statement stat = null;
		try {
			stat = conn.createStatement();	
			String sql = "update t_funclist set c_pfuncid = 'CREATENEW' where c_funcid = 'MANAGE_CREATEREPORT_WORKFLOW'";
			LOG.debug(sql);
			stat.executeUpdate(sql);
			return true;
		} catch (SQLException e) {
			LOG.error("Upgrade module '" + this.getClass().getPackage().getName() + "' to " + getNewVersion()
					+ " fail.", e);
		} finally {
			if (stat != null) {
				try {
					stat.close();
				} catch (SQLException e) {
					//
				}
				stat = null;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getNewVersion() {
		return "0.0.6";
	}

}
