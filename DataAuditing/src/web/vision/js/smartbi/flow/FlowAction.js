var FlowActionView = imports("smartbi.flow.FlowActionView");
var util = imports("freequery.common.util").getInstance();
var modalWindow = imports("freequery.common.modalWindow").getInstance();
var dialogFactory;

var FlowAction = function(parent) {
	this.viewSlot = parent ? parent : document.getElementById('rightContent');
	this.onClose = new CustomEvent();
	this.needRefresh = new CustomEvent('needRefresh', this);
	this.onRefreshTreeNode = new CustomEvent("refreshTreeNode", this);
};

lang.extend(FlowAction, "bof.baseajax.common.BasicAction");

FlowAction.prototype.open = function() {
	this.view = new FlowActionView(this.viewSlot);
	this.view.onClose.subscribe(this.closeView, this);
	this.view.onSave.subscribe(this.save, this);

	this.view.show();
}

FlowAction.prototype.setNode = function(node) {
	this.view.pageView.elemSpreadSheetId.value = node._id;
	this.view.pageView.elemSpreadSheet.value = node._alias;
	
	this.view.pageView.allSpreadSheet.push({
		alias:node._alias,
		id:node._id,
		name:node._name,
		type:node._type
	});
}

FlowAction.prototype.openWithId = function(id) {
	this.workflowId = id;
	var ret = util.remoteInvokeEx("RepositoryService", "openWorkFlowInfo", [ this.workflowId ]);
	if (ret&&ret.succeeded&&ret.result) {		
		this.workflow = ret.result[0];
		this.view = new FlowActionView(this.viewSlot);
		this.view.onClose.subscribe(this.closeView, this);
		this.view.onSave.subscribe(this.save, this);
		this.view.showWithInfo(ret.result);	
	} else {
		this.noRightConfirm();
		
	}

}

FlowAction.prototype.noRightConfirm = function(){
	var that = this;
	var msg = "${NoRightOfSpreadsheet}";
	var flags = modalWindow.MB_OK;
	alert(msg, "${Confirm}", flags, function(ret) {
		that.onClose.fire(that);
	}, this);
}

FlowAction.prototype.isDirty = function() {
	return this.view.isDirty();
}

FlowAction.prototype.close = function() {
	if (this.view) {
		this.view.destroy();
		this.view = null;
	}
}

FlowAction.prototype.closeView = function() {
	if (this.view && this.view.isDirty()) {
		var that = this;
		this.confirm(function(ret) {
			if (ret) {
				that.onClose.fire(that);
			}
		});
	} else {
		this.onClose.fire(this);
	}
}

FlowAction.prototype.getValue = function() {
	this.view.collectAllInfo();

	var info = this.view.pageBean;
	var defineObj = this.view.pageNextBean;
	if (defineObj == null) {
		defineObj = {};
	}
	defineObj.processVerifySheet = info.processVerifySheet;

	var workflow = {};
	if (this.workflow) {
		workflow = this.workflow;
	}
	if (defineObj.firstActivity) {
		if (defineObj.firstActivity.assignee) {
			workflow.workflowInitiateRole = JSON.stringify(defineObj.firstActivity.assignee);
		}
		delete defineObj.firstActivity;
	}
	workflow.workflowLifeCycle = JSON.stringify(info.life);
	workflow.workflowDefine = JSON.stringify(defineObj);

	var value = [];
	value[0] = workflow;
	value[1] = info.spreadsheetId;
	value[2] = info.mechanismParameters;
	value[3] = info.processParameters;
	value[4] = info.singleSheet;
	return value;
}

FlowAction.prototype.save = function() {
	if (!this.view.collectAllInfo())
		return;
	
	var workflowDefine =  eval("("+this.getValue()[0].workflowDefine+")")
	if(!workflowDefine)
		return;	
	var tactivitys = workflowDefine.activitys;
	if(!tactivitys)
		return;
	var sameNameFlg = false;
	for(var p in tactivitys){
		for(var a in tactivitys){
				if((tactivitys[p].name == tactivitys[a].name)&&(tactivitys[p].id != tactivitys[a].id)){
					sameNameFlg = true;
					break;
				}
			}		
		}

	if(sameNameFlg){
		alert("${CannotRename}!");
	}else{
		if (this.workflowId) {
			this.doUpdate()
		} else {
			this.doSaveAs();
			return;
		}
	}
	
}

FlowAction.prototype.doUpdate = function() {
	var info = this.getValue();
	var ret = util.remoteInvokeEx("RepositoryService", "updateWorkFlowInfo", [ info[0], info[1], info[2], info[3],
			info[4] ]);
	if (ret && ret.succeeded) {
		alert("${Savesuccessful}");
		this.needRefresh.fire(this, info[0].id);
		this.onClose.fire(this);
	}
}

FlowAction.prototype.doSaveAs = function() {
	var data = new Array();
	data[0] = [ "DEFAULT_TREENODE" ];	// 只显示“资源定制”
	data[1] = [ "WORKFLOW" ]
	data[2] = [ "${WorkFlow}" ];
	data[3] = "SAVE";
	data[4] = this.initFolderNode;

	if (!dialogFactory)
		dialogFactory = jsloader.resolve("freequery.dialog.dialogFactory");
	var dialogConfig = {
		title : "${Savereport}",
		fullName : "freequery.dialog.OpenSaveDialog"
	};
	dialogConfig.size = [ 500, 341 ];
	dialogFactory.showDialog(dialogConfig, data, this.saveQueryCallback, this);
}

FlowAction.prototype.saveQueryCallback = function(ret, dialog) {
	if (!ret)
		return false;

	var info = this.getValue();
	if (ret.actionType == "override") {
		var ret2 = util.remoteInvokeEx("RepositoryService", "overwriteFlowInfo", [ info[0], info[1], info[2], info[3],
				info[4], ret.fileId, ret.fileDesc ]);
		if (ret2 && ret2.succeeded) {
			this.workflowId = ret.fileId;
			this.needRefresh.fire(this, ret.folderId || (this.initFolderNode && this.initFolderNode.id));
			alert("${Savesuccessful}");
			this.name = ret.fileName;
			this.alias = ret.fileName;

			var registry = jsloader.resolve('freequery.lang.registry');
			var currentManager = registry.get("CurrentManager");
			if (currentManager && currentManager.tabControl && currentManager.tabControl.activeTab) {
				currentManager.tabControl.activeTab.setIdAndCaption([ this.queryId, "OPEN" ], this.alias);
				currentManager.tabControl.activeTab.resType = 'WORKFLOW';
			}
			this.onClose.fire(this);
		}
	} else {
		this.folderId = ret.folderId;
		var ret2 = util.remoteInvokeEx("RepositoryService", "saveAsWorkFlowInfo", [ info[0], info[1], info[2], info[3],
				info[4], ret.fileName, ret.fileName, ret.fileDesc, this.folderId ]);
		if (ret2 && ret2.succeeded) {
			this.workflowId = ret.result;

			this.needRefresh.fire(this, this.folderId);
			alert("${Savesuccessful}");
			this.name = ret.fileName;
			this.alias = ret.fileName;

			var registry = jsloader.resolve('freequery.lang.registry');
			var currentManager = registry.get("CurrentManager");
			if (currentManager && currentManager.tabControl && currentManager.tabControl.activeTab) {
				currentManager.tabControl.activeTab.setIdAndCaption([ this.queryId, "OPEN" ], this.alias);
				currentManager.tabControl.activeTab.resType = 'WORKFLOW';
			}
			this.onClose.fire(this);
		}
	}
}

// 开始流程
FlowAction.prototype.startInstanceById = function(workflowId, resId) {

	var reportVersion = reportVersion || "1";
	var UserService = imports("bof.usermanager.UserService").getInstance();
	var userId = UserService.getCurrentUser().id;

	var util = jsloader.resolve("freequery.common.util");
	var ret = util.remoteInvoke("RuntimeService", "startInstanceById", [ workflowId, resId, userId ]);
	if (!ret || !ret.succeeded) {
		var modalWindow = jsloader.resolve("freequery.common.modalWindow");
		modalWindow.showServerError(ret);
	}
	// 刷新，更新按钮状态
	// spreadsheetReport.doRefresh();

}