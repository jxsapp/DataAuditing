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
 * 初始化流程需要使用的表
 */
public class UpgradeTask_New extends UpgradeTask {

	/** */
	private static final Logger LOG = Logger.getLogger(UpgradeTask_New.class);

	@Override
	public boolean doUpgrade(Connection conn, DBType type) {
		Statement stat = null;
		try {
			stat = conn.createStatement();

			// init t_ext_workflow_info
			if (!UpgradeHelper.isTableExists(conn, "t_ext_workflow_info")) {
				String sql = UpgradeHelper.getCreateSQL(type, "t_ext_workflow_info", new String[] { "c_workflowid",
						"c_workflowname", "c_workflowalias", "c_workflowdesc", "c_workflowtype", "c_workflowlifecycle",
						"c_workflowinstances", "c_workflowinitiaterole", "c_workflowdefine" }, new ValueType[] {
						ValueType.STRING, ValueType.STRING, ValueType.STRING, ValueType.STRING, ValueType.STRING,
						ValueType.STRING, ValueType.INTEGER, ValueType.STRING, ValueType.ASCII }, new boolean[] {
						false, true, true, true, true, true, true, true, true }, new String[] { "c_workflowid" });
				LOG.debug(sql);
				stat.executeUpdate(sql);
			}

			// init t_ext_workflow_instance
			if (!UpgradeHelper.isTableExists(conn, "t_ext_workflow_instance")) {
				String sql = UpgradeHelper.getCreateSQL(type, "t_ext_workflow_instance", new String[] { "c_instanceid",
						"c_resourceid", "c_workflowid", "c_instancedesc", " c_activity", "c_instancestate",
						" c_instancecreatedate", "c_instancecreatorid", "c_completeflag", "c_completedate",
						"c_parentinstanceid" }, new ValueType[] { ValueType.STRING, ValueType.STRING, ValueType.STRING,
						ValueType.STRING, ValueType.STRING, ValueType.STRING, ValueType.DATETIME, ValueType.STRING,
						ValueType.STRING, ValueType.DATETIME, ValueType.STRING }, new boolean[] { false, true, true,
						true, true, true, true, true, true, true, true }, new String[] { "c_instanceid" });
				LOG.debug(sql);
				stat.executeUpdate(sql);
			}

			// init t_ext_workflow_resource
			if (!UpgradeHelper.isTableExists(conn, "t_ext_workflow_resource")) {
				String sql = UpgradeHelper.getCreateSQL(type, "t_ext_workflow_resource", new String[] { "c_resourceid",
						"c_spreadsheetid", "c_workflowid", "c_mechanismparam", "c_otherparams" }, new ValueType[] {
						ValueType.STRING, ValueType.STRING, ValueType.STRING, ValueType.STRING, ValueType.ASCII },
						new boolean[] { false, true, true, true, true, true }, new String[] { "c_resourceid" });
				LOG.debug(sql);
				stat.executeUpdate(sql);
			}

			// init t_ext_workflow_resource
			if (!UpgradeHelper.isTableExists(conn, "t_ext_workflow_log")) {
				String sql = UpgradeHelper.getCreateSQL(type, "t_ext_workflow_log", new String[] { "c_id",
						"c_username", "c_useralias", "c_time", "c_taskid", "c_taskname", "c_instanceid", "c_detail" },
						new ValueType[] { ValueType.STRING, ValueType.STRING, ValueType.STRING, ValueType.DATETIME,
								ValueType.STRING, ValueType.STRING, ValueType.STRING, ValueType.ASCII }, new boolean[] {
								false, true, true, true, true, true, true, true }, new String[] { "c_id" });
				LOG.debug(sql);
				stat.executeUpdate(sql);
			}

			// init t_ext_workflow_task
			if (!UpgradeHelper.isTableExists(conn, "t_ext_workflow_task")) {
				String sql = UpgradeHelper.getCreateSQL(type, "t_ext_workflow_task", new String[] { "c_taskid",
						"c_tasktype", "c_assignee", "c_assigneetype", "c_createtime", "c_workflowid", "c_resourceid",
						"c_instanceid", "c_taskname", "c_taskstate", "c_taskdesc", "c_taskoperator", "c_operatetime",
						"c_taskrule", "c_tasksugget", "c_taskactioncontent", "c_taskopt" }, new ValueType[] {
						ValueType.STRING, ValueType.STRING, ValueType.STRING, ValueType.STRING, ValueType.DATETIME,
						ValueType.STRING, ValueType.STRING, ValueType.STRING, ValueType.STRING, ValueType.STRING,
						ValueType.STRING, ValueType.STRING, ValueType.DATETIME, ValueType.STRING, ValueType.STRING,
						ValueType.ASCII, ValueType.STRING }, new boolean[] { false, true, true, true, true, true, true,
						true, true, true, true, true, true, true, true, true, true }, new String[] { "c_taskid" });
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
		return "0.0.1";
	}

}
