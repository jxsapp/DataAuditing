var util = jsloader.resolve('freequery.common.util');
var dialogFactory = jsloader.resolve("freequery.dialog.dialogFactory");
var FlowAction = jsloader.resolve("smartbi.flow.FlowAction");
var SelectFlowDialog = jsloader.resolve("smartbi.flow.dialog.SelectFlowDialog");
var modalWindow = jsloader.resolve("freequery.common.modalWindow");

// 定制界面的右键菜单
var SpreadsheetReportDataAuditingHandler = function(popupMenu) {
	this.popupMenu = popupMenu;
};

SpreadsheetReportDataAuditingHandler.prototype.destroy = function() {
}

SpreadsheetReportDataAuditingHandler.prototype.initMenu = function() {
	this.popupMenu.processMenu = this.popupMenu.createMenuItem("${ProcessDefinition}",null,null,null,"${Movetothe}");
	this.popupMenu.processMenu.createProcessMenu = this.popupMenu.processMenu.createMenuItem("${NewFlow}",
			"createProcess");
	this.popupMenu.processMenu.bindingProcessMenu = this.popupMenu.processMenu.createMenuItem("${BindingProcess}",
			"bindingProcess");
	this.popupMenu.processMenu.modifyProcessMenu = this.popupMenu.processMenu.createMenuItem("${ModifyProcess}",
			"modifyProcess");
	this.popupMenu.processMenu.cancelBindingMenu = this.popupMenu.processMenu.createMenuItem("${CancelBinding}",
			"cancelBinding");
};

SpreadsheetReportDataAuditingHandler.prototype.clearMenuState = function() {
	this.popupMenu.processMenu.setVisibility(false);
	this.popupMenu.processMenu.createProcessMenu.setVisibility(false);
	this.popupMenu.processMenu.bindingProcessMenu.setVisibility(false);
	this.popupMenu.processMenu.modifyProcessMenu.setVisibility(false);
	this.popupMenu.processMenu.cancelBindingMenu.setVisibility(false);
}

SpreadsheetReportDataAuditingHandler.prototype.resetMenuState = function(node) {
	if (!util.hasLicense('WriteBack')) {
		return;
	}
	if (!util.checkFunctionValid('MANAGE_CREATEREPORT_WORKFLOW')) {
		return;
	}
	if (!node)
		return;
	if(node.parentNode&&node.parentNode._type == "SELF_TREENODE"){
		this.popupMenu.processMenu.setVisibility(false);
		return;
	}
	if (!util.hasLicense('ReportAudit')) {
		this.clearMenuState();
		return;
	}
	if (node) {
		if (node._type == "WORKFLOW") {
			this.popupMenu.open.setVisibility(true);
			this.popupMenu.openInNewWindow.setVisibility(false);
			this.popupMenu.listMacroModulesM.setVisibility(true);
		} else if (node._type == "SPREADSHEET_REPORT") {
			this.popupMenu.processMenu.setVisibility(true);
			var hasProcessBinding = false;

			var ret = util.remoteInvokeEx("RepositoryService", "getInfoBySpreadsheetId", [ node._id ]);
			if (ret && ret.succeeded && ret.result) {
				hasProcessBinding = true;
				this.workFlowInfo = ret.result;
			}

			if (!hasProcessBinding) {
				this.popupMenu.processMenu.createProcessMenu.setVisibility(true);
				this.popupMenu.processMenu.bindingProcessMenu.setVisibility(true);
			} else {
				this.popupMenu.processMenu.modifyProcessMenu.setVisibility(true);
				this.popupMenu.processMenu.cancelBindingMenu.setVisibility(true);
			}
		}

	}
};
SpreadsheetReportDataAuditingHandler.prototype.getCommand = function(cmdFactory, cmdName, isGetOnly) {
	if (!cmdFactory.cmdMapping[cmdName]) {
		var ext = {
			'FlowCommand' : 'smartbi.flow.FlowCommand'
		}
		for ( var x in ext) {
			cmdFactory.cmdMapping[x] = ext[x];
		}
	}
	var command = cmdFactory.getCommandOnly(cmdName);
	if (command && !isGetOnly)
		command = cmdFactory.getCommand(cmdName);
	return command;
};

SpreadsheetReportDataAuditingHandler.prototype.doCmd = function(node, func) {
	if(node._type == "SPREADSHEET_REPORT" || node._type == "WORKFLOW"){
		if (!node || !func)
			return;

		if (func == "bindingProcess" && node._type == "SPREADSHEET_REPORT") {
			this.bindingProcess(node, func);
			return;
		} else if (func == "cancelBinding" && node._type == "SPREADSHEET_REPORT") {
			this.cancelProcess(node, func);
			return;
		}

		var cmdFactory = node.tree.commandFactory, cmdName, action;
		if (typeof func == 'string') {
			cmdName = 'FlowCommand';
			action = func;
		} else if (func.length == 2) {
			cmdName = func[0];
			action = func[1];
		} else {
			throw new Error('Unknown ' + func);
		}

		var ops = {
			'CREATE' : true,
			'CREATEWITHID' : true,
			'createProcess' : true,
			'modifyProcess' : true,
			'OPEN' : true
		};

		if (!ops[action] || (cmdName != 'FlowCommand'))
			return;
		var nodeType = node._type;
		if (action == 'CREATE' || action == 'createProcess') {
			action = 'CREATE';
		} else if (action == 'modifyProcess') {
			action = "OPEN";
			node = {};
			node._id = this.workFlowInfo.id;
		} else if (nodeType != 'WORKFLOW') {
			return;
		}

		var command = this.getCommand(cmdFactory, cmdName, false);
		if (command) {
			command.execute(action, node);
			return;
		}
	}
	
};

SpreadsheetReportDataAuditingHandler.prototype.bindingProcess = function(node, func) {
	this.node = node;
	var dialogConfig = {};
	dialogConfig.title = "${BindingProcess}";
	dialogConfig.fullName = "smartbi.flow.dialog.SelectFlowDialog";
	dialogConfig.size = dialogFactory.size.MIDDLE;
	dialogFactory.showDialog(dialogConfig, {}, this.callBackBinding, this);
}

SpreadsheetReportDataAuditingHandler.prototype.callBackBinding = function(flag, obj) {
	if (flag && obj) {
		var workflowId = obj._id;
		var reportParam = null;
		var ret = util.remoteInvokeEx("RepositoryService", "getParametersBySpreadsheetId", [ this.node._id ]);
		if (ret && ret.succeeded && ret.result) {
			reportParam = ret.result;
		}

		var ret = util.remoteInvokeEx("RepositoryService", "getWorkflowResourceDefineByWorkflowId", [ workflowId ]);
		if (ret && ret.succeeded && ret.result && ret.result.length > 0) {
			var mechanismParameter = ret.result[0].mechanismParameter;
			var otherParameters = ret.result[0].otherParameters;
			var isBySheet = ret.result[0].bySheet;

			// 检查流程上绑定的参数是否在新报表中存在
			var flag = true;
			if (mechanismParameter || otherParameters) {
				if (mechanismParameter) {
					var tFlag = false
					for ( var j = 0; j < reportParam.length; j++) {
						if (mechanismParameter == reportParam[j].name) {
							tFlag = true;
							break;
						}
					}
					if (!tFlag) {
						flag = false;
					}
				}
				var nameList = otherParameters.split(",");
				for ( var i = 0; i < nameList.length; i++) {
					if (nameList[i]) {
						var tFlag = false
						for ( var j = 0; j < reportParam.length; j++) {
							if (nameList[i] == reportParam[j].name) {
								tFlag = true;
								break;
							}
						}
						if (!tFlag) {
							flag = false;
							break;
						}
					}
				}
			}

			if (flag) {
				ret = util.remoteInvokeEx("RepositoryService", "addBinding", [ workflowId, this.node._id,
						mechanismParameter, otherParameters, isBySheet ]);
				if (ret && ret.succeeded) {
					alert("绑定成功");
				}
			} else {
				// 不匹配
				alert("流程参数不一致，无法绑定! 请编辑流程设置!");
			}
		} else {
			// 没有绑定过，没有参数设置
			alert("流程缺少参数配置，无法绑定! 请编辑流程设置!");
		}
	}
}

SpreadsheetReportDataAuditingHandler.prototype.cancelProcess = function(node, func) {
	var flags = modalWindow.MB_YESNO | modalWindow.MB_ICONQUESTION;
	alert("确定要取消流程绑定吗?", "${Confirm}", flags, function(ret) {
		if (ret == modalWindow.ID_YES) {
			var ret = util.remoteInvokeEx("RepositoryService", "removeBinding", [ node._id ]);
			if (ret && ret.succeeded) {
				alert("取消绑定成功");
			}
		}
	}, this);
}