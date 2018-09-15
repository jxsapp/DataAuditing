package smartbi.auditing.upgrade;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import smartbi.repository.UpgradeHelper;
import smartbi.repository.UpgradeTask;
import smartbi.util.DBType;
import smartbi.util.ValueType;

public class UpgradeTask_0_0_1 extends UpgradeTask {

	/** */
	private static final Logger LOG = Logger.getLogger(UpgradeTask_New.class);

	@Override
	public boolean doUpgrade(Connection conn, DBType type) {
		Statement stat = null;
		try {
			stat = conn.createStatement();

			// init  t_ext_workflow_redefine
			if (!UpgradeHelper.isTableExists(conn, "t_ext_workflow_resdefine")) {
				String sql = UpgradeHelper.getCreateSQL(type, "t_ext_workflow_resdefine", new String[] { "c_id",
						"c_spreadsheetid", "c_workflowid", "c_mechanismparam", "c_otherparams", "c_isbysheet" },
						new ValueType[] { ValueType.STRING, ValueType.STRING, ValueType.STRING, ValueType.STRING,
								ValueType.ASCII, ValueType.INTEGER }, new boolean[] { false, true, true, true, true,
								true }, new String[] { "c_id" });
				LOG.debug(sql);
				stat.executeUpdate(sql);
			}
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

	@Override
	public String getNewVersion() {
		return "0.0.2";
	}

}
