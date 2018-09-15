package smartbi.auditing.upgrade;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import smartbi.repository.UpgradeHelper;
import smartbi.repository.UpgradeTask;
import smartbi.util.DBType;
import smartbi.util.ValueType;

/**
 * 知识库升级类
 */
public class UpgradeTask_0_0_3 extends UpgradeTask {

	/** */
	private static final Logger LOG = Logger.getLogger(UpgradeTask_New.class);

	/**
	 * {@inheritDoc}
	 */
	public boolean doUpgrade(Connection conn, DBType type) {
		Statement stat = null;
		try {
			stat = conn.createStatement();

			// t_ext_workflow_resdefine  add column c_singlesheet
			if (!UpgradeHelper.isFieldExists(conn, "t_ext_workflow_resdefine", "c_isbysheet")) {
				String sql = UpgradeHelper.getAddColumnSQL(type, "t_ext_workflow_resdefine", "c_isbysheet",
						ValueType.INTEGER, true);
				LOG.debug(sql);
				stat.executeUpdate(sql);
			}
			
			String sql = "update t_ext_workflow_resdefine set c_isbysheet = 0 where c_isbysheet is null";
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
		return "0.0.4";
	}

}
