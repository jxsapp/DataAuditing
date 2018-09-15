package smartbi.auditing.upgrade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import smartbi.repository.UpgradeTask;
import smartbi.util.DBType;

/**
 * 知识库升级类
 */
public class UpgradeTask_0_0_2 extends UpgradeTask {

	/**
	 * {@inheritDoc}
	 */
	public boolean doUpgrade(Connection conn, DBType type) {
		Statement stat = null;
		try {
			// 增加新的操作权限
			String[][] funcs = new String[][] { { "MANAGE_CREATEREPORT_WORKFLOW", getProperty("WorkFlow") } };

			addFunctions(conn, funcs);
		} catch (SQLException e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
		} finally {
			closeDBObject(null, stat);
		}
		return true;
	}

	/**
	 * 添加操作权限
	 */
	private void addFunctions(Connection conn, String[][] funcs) throws SQLException {
		boolean bs = false;
		PreparedStatement prep = conn
				.prepareStatement("insert into t_funclist(c_funcid, c_funcname, c_funcalias, c_funcdesc, c_sysid, c_pfuncid, c_order) values(?,?,?,?,?,?,?)");
		bs = false;
		for (String[] func : funcs) {
			String id = func[0].toUpperCase();
			prep.setString(1, id);
			prep.setString(2, func[1]);
			prep.setString(3, func[1]);
			prep.setString(4, func[1]);
			prep.setString(5, "DEFAULT_SYS");
			String pid = id;
			int index = id.lastIndexOf("_");
			if (index == -1) {
				pid = null;
			} else {
				pid = id.substring(0, index);
			}
			prep.setString(6, pid);
			prep.setInt(7, 0);
			prep.addBatch();
			bs = true;
		}
		if (bs) {
			prep.executeBatch();
		}
		prep.close();

		prep = conn.prepareStatement("insert into t_role_func(c_roleid, c_funcid) values(?,?)");
		bs = false;
		for (String[] func : funcs) {
			prep.setString(1, "ADMINS");
			prep.setString(2, func[0].toUpperCase());
			prep.addBatch();
			bs = true;
		}
		if (bs) {
			prep.executeBatch();
		}
		prep.close();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getNewVersion() {
		return "0.0.3";
	}

}
