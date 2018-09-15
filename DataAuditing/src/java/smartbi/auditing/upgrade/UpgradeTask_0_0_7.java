package smartbi.auditing.upgrade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import smartbi.repository.UpgradeTask;
import smartbi.util.DBType;

/**
 * 增加 func_GetFlowCreatorID 函数
 * 
 * @author qianjijin
 *
 */
public class UpgradeTask_0_0_7 extends UpgradeTask {
	/**
	 * {@inheritDoc}
	 */
	private static final Logger LOG = Logger.getLogger(UpgradeTask_0_0_7.class);

	@Override
	public boolean doUpgrade(Connection conn, DBType type) {
		PreparedStatement prep = null;
		try {
			Statement sta = conn.createStatement();
			ResultSet rs = sta
					.executeQuery("select c_resid from t_restree where c_resid = 'func_GetFlowCreatorID'");
			if (!rs.next()) {
				prep = conn
						.prepareStatement("insert into t_restree(c_resid, c_resname, c_resalias, c_pid, c_restype, c_order, c_perm, c_resdesc, c_created, c_lastmodified) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				prep.setString(1, "func_GetFlowCreatorID");
				prep.setString(2, "GetFlowCreatorID");
				prep.setString(3, "GetFlowCreatorID");
				prep.setString(4, "catalog_string");
				prep.setString(5, "FUNCTION");
				prep.setInt(6, 6999);
				prep.setString(7, PERM_INHERITED_FALSE);
				prep.setString(8, getProperty("func_GetFlowCreatorID_Desc"));
				prep.setTimestamp(9,
						new Timestamp(new java.util.Date().getTime()));
				prep.setTimestamp(10,
						new Timestamp(new java.util.Date().getTime()));
				prep.executeUpdate();
				prep.close();
			}
			rs.close();
			sta.close();
			return true;
		} catch (SQLException e) {
			LOG.error("Upgrade " + this.getClass().getPackage().getName()
					+ "' to " + getNewVersion() + " fail.", e);
			return false;
		}
	}

	@Override
	public String getNewVersion() {
		return "0.0.8";
	}

}
