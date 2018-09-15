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
public class UpgradeTask_0_0_4 extends UpgradeTask {

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
			if (!UpgradeHelper.isFieldExists(conn, "t_ext_workflow_info", "c_workflowinitiaterole_temp")) {
				String sql = UpgradeHelper.getAddColumnSQL(type, "t_ext_workflow_info", "c_workflowinitiaterole_temp",
						ValueType.ASCII, true);
				LOG.debug(sql);
				stat.executeUpdate(sql);
				if (type == DBType.DB2_V9) {
					//reorg table <tablename> 通过重构行来消除“碎片”数据并压缩信息，对表进行重组。
					sql = "CALL SYSPROC.ADMIN_CMD('reorg table t_ext_workflow_info')";
					stat.executeUpdate(sql);
				}
				
				sql = "update t_ext_workflow_info set c_workflowinitiaterole_temp = c_workflowinitiaterole ";
				LOG.debug(sql);
				stat.executeUpdate(sql);
				if (type == DBType.DB2_V9) {
					//reorg table <tablename> 通过重构行来消除“碎片”数据并压缩信息，对表进行重组。
					sql = "CALL SYSPROC.ADMIN_CMD('reorg table t_ext_workflow_info')";
					stat.executeUpdate(sql);
				}
				
				sql = UpgradeHelper.getDropColumnSQL(type,  "t_ext_workflow_info", "c_workflowinitiaterole");
				LOG.debug(sql);
				stat.executeUpdate(sql);
				if (type == DBType.DB2_V9) {
					//reorg table <tablename> 通过重构行来消除“碎片”数据并压缩信息，对表进行重组。
					sql = "CALL SYSPROC.ADMIN_CMD('reorg table t_ext_workflow_info')";
					stat.executeUpdate(sql);
				}
				
				sql = UpgradeHelper.getAddColumnSQL(type, "t_ext_workflow_info", "c_workflowinitiaterole",
						ValueType.ASCII, true);
				LOG.debug(sql);
				stat.executeUpdate(sql);
				if (type == DBType.DB2_V9) {
					//reorg table <tablename> 通过重构行来消除“碎片”数据并压缩信息，对表进行重组。
					sql = "CALL SYSPROC.ADMIN_CMD('reorg table t_ext_workflow_info')";
					stat.executeUpdate(sql);
				}
				
				sql = "update t_ext_workflow_info set c_workflowinitiaterole = c_workflowinitiaterole_temp ";
				LOG.debug(sql);
				stat.executeUpdate(sql);
				if (type == DBType.DB2_V9) {
					//reorg table <tablename> 通过重构行来消除“碎片”数据并压缩信息，对表进行重组。
					sql = "CALL SYSPROC.ADMIN_CMD('reorg table t_ext_workflow_info')";
					stat.executeUpdate(sql);
				}
				
				sql = UpgradeHelper.getDropColumnSQL(type,  "t_ext_workflow_info", "c_workflowinitiaterole_temp");
				LOG.debug(sql);
				stat.executeUpdate(sql);
				if (type == DBType.DB2_V9) {
					//reorg table <tablename> 通过重构行来消除“碎片”数据并压缩信息，对表进行重组。
					sql = "CALL SYSPROC.ADMIN_CMD('reorg table t_ext_workflow_info')";
					stat.executeUpdate(sql);
				}
			}
			
			
			if (!UpgradeHelper.isFieldExists(conn, "t_ext_workflow_task", "c_assignee_temp")) {
				String sql = UpgradeHelper.getAddColumnSQL(type, "t_ext_workflow_task", "c_assignee_temp",
						ValueType.ASCII, true);
				LOG.debug(sql);
				stat.executeUpdate(sql);
				if (type == DBType.DB2_V9) {
					//reorg table <tablename> 通过重构行来消除“碎片”数据并压缩信息，对表进行重组。
					sql = "CALL SYSPROC.ADMIN_CMD('reorg table t_ext_workflow_task')";
					stat.executeUpdate(sql);
				}
				
				sql = "update t_ext_workflow_task set c_assignee_temp = c_assignee ";
				LOG.debug(sql);
				stat.executeUpdate(sql);
				if (type == DBType.DB2_V9) {
					//reorg table <tablename> 通过重构行来消除“碎片”数据并压缩信息，对表进行重组。
					sql = "CALL SYSPROC.ADMIN_CMD('reorg table t_ext_workflow_task')";
					stat.executeUpdate(sql);
				}
				
				sql = UpgradeHelper.getDropColumnSQL(type,  "t_ext_workflow_task", "c_assignee");
				LOG.debug(sql);
				stat.executeUpdate(sql);
				if (type == DBType.DB2_V9) {
					//reorg table <tablename> 通过重构行来消除“碎片”数据并压缩信息，对表进行重组。
					sql = "CALL SYSPROC.ADMIN_CMD('reorg table t_ext_workflow_task')";
					stat.executeUpdate(sql);
				}
				
				sql = UpgradeHelper.getAddColumnSQL(type, "t_ext_workflow_task", "c_assignee",
						ValueType.ASCII, true);
				LOG.debug(sql);
				stat.executeUpdate(sql);
				if (type == DBType.DB2_V9) {
					//reorg table <tablename> 通过重构行来消除“碎片”数据并压缩信息，对表进行重组。
					sql = "CALL SYSPROC.ADMIN_CMD('reorg table t_ext_workflow_task')";
					stat.executeUpdate(sql);
				}
				
				sql = "update t_ext_workflow_task set c_assignee = c_assignee_temp ";
				LOG.debug(sql);
				stat.executeUpdate(sql);
				if (type == DBType.DB2_V9) {
					//reorg table <tablename> 通过重构行来消除“碎片”数据并压缩信息，对表进行重组。
					sql = "CALL SYSPROC.ADMIN_CMD('reorg table t_ext_workflow_task')";
					stat.executeUpdate(sql);
				}
				
				sql = UpgradeHelper.getDropColumnSQL(type,  "t_ext_workflow_task", "c_assignee_temp");
				LOG.debug(sql);
				stat.executeUpdate(sql);
				if (type == DBType.DB2_V9) {
					//reorg table <tablename> 通过重构行来消除“碎片”数据并压缩信息，对表进行重组。
					sql = "CALL SYSPROC.ADMIN_CMD('reorg table t_ext_workflow_task')";
					stat.executeUpdate(sql);
				}
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

	/**
	 * {@inheritDoc}
	 */
	public String getNewVersion() {
		return "0.0.5";
	}

}
