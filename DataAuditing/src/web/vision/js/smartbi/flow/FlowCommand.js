var FrameView = jsloader.imports("freequery.common.FrameView");
var FlowAction = jsloader.resolve("smartbi.flow.FlowAction");
var dialogFactory = jsloader.resolve("freequery.dialog.dialogFactory");
var modalWindow = imports("freequery.common.modalWindow");
var util = jsloader.resolve('freequery.common.util');

var FlowCommand = function() {
	this.mainPane = document.getElementById('rightContent');
	this.onClose = new CustomEvent('onClose', this);
	this.needRefresh = new CustomEvent('needRefresh', this);
	this.mainView = registry.get('MainView');
};
lang.extend(FlowCommand, 'freequery.widget.Module');

FlowCommand.prototype.destroy = function() {
	this.close();
	this.node = null;
	this.mainView = null;
	this.onClose = null;
	this.needRefresh = null;
	this.mainPane = null;
	FlowCommand.superclass.destroy.call(this);
};

FlowCommand.prototype.isActionEnabled = function(action) {
	// TODO license
	return true;
};

FlowCommand.prototype.assertOpenFunc = function() {
	// TODO 操作权限
	return true;
};

FlowCommand.prototype.execute = function(arg1, arg2) {
	var action, node;
	var combinId = "";
	if ('string' === typeof arguments[1]) {
		action = arguments[0];
		node = {
			_id : arguments[1],
			_type : "WORKFLOW",
			tree : {
				commandFactory : jsloader.resolve('freequery.common.DisplayCustomCommandFactory')
			}
		};
		if (arguments.length>2)
			combinId = arguments[2];
	} else {
		action = arguments[0];
		node = arguments[1];
	}
	this.action = action;
	this.node = node;
	switch (action) {
	case 'CREATE':
		this.createWorkflow(arguments[1]);
		break;
	case 'CREATEWITHID':
		this.createWorkflow(combinId);
		break;
	case 'OPEN':
		var options = {
			_id : node && node._id,
			folderNode : node && node.parentNode
		};
		this.openWorkflow(options);
		break;
	default:
		alert('TODO:' + action);
		break;
	}
};

FlowCommand.prototype.confirmClose = function(func) {
	if (this.flowAction) {
		return this.flowAction.confirmClose(func);
	} else {
		return true;
	}
};

FlowCommand.prototype.close = function() {
	this.closeFlowAction();
};

FlowCommand.prototype.doOnClose = function() {
	this.onClose.fire(this);
};

FlowCommand.prototype.doNeedRefresh = function(that, folderId) {
	this.needRefresh.fire(this, folderId);
};

FlowCommand.prototype.createWorkflow = function(node) {
	this.close();	
	this.flowAction = new FlowAction(this.mainPane);
	this.flowAction.open();
	this.flowAction.onClose.subscribe(this.doOnClose, this);
	this.flowAction.needRefresh.subscribe(this.doNeedRefresh, this);
	if(node){
		this.flowAction.setNode(node);
	}
};

FlowCommand.prototype.openWorkflow = function(options) {
	if (!this.assertOpenFunc()) {
		this.doOnClose();
		return;
	}
	this.close();
	this.flowAction = new FlowAction(this.mainPane);
	//this.flowAction.open();
	this.flowAction.needRefresh.subscribe(this.doNeedRefresh, this);
	this.flowAction.onClose.subscribe(this.doOnClose, this);
	this.flowAction.openWithId(options._id)
};

FlowCommand.prototype.doRefreshTreeNode = function(sender, id, type) {
	var searchHelper = imports('bof.baseajax.common.searchHelper').getInstance();
	searchHelper.locate(id);
};

FlowCommand.prototype.closeFlowAction = function() {
	if (this.flowAction) {
		if (this.flowAction.onClose) {
			this.flowAction.onClose.unsubscribe(this.doClose, this);
		}
		this.flowAction.needRefresh.unsubscribe(this.doNeedRefresh, this);
		this.flowAction.destroy();
		this.flowAction = null;
	}
};

FlowCommand.prototype.setCommandFactoryName = function(commandFactoryName) {
	this.commandFactoryName = commandFactoryName;
	this.isBrowseModule = ('QueryBrowserCommandFactory' == this.commandFactoryName
			|| 'MyFavoriteCommandFactory' == this.commandFactoryName || /^CatalogDisplayCommandFactory/
			.test(this.commandFactoryName));
};

FlowCommand.prototype.loadFrameView = function() {
	this.frame = new FrameView(document.getElementById('rightContent'));
	this.container = this.frame.frameViewContainer;
	this.frame.onClose.subscribe(this.closeFlowAction, this);
};

FlowCommand.prototype.confirmClose = function(callback) {
	return this.confirm(callback);
};

FlowCommand.prototype.confirm = function(callback) {
	if (typeof callback != 'function') {
		return true;
	}
	var t = this, fn = function(ret) {
		callback.call(t, ret);
	};
	
	if(this.flowAction){
		if(this.flowAction.isDirty()){
			var msg = "${Thereareunsavedchanges}${Comma}${Confirmthatyouwanttocontinue}${Questionmark}";
			var modalWin = modalWindow.getInstance();
			var flags = modalWin.MB_YESNO | modalWin.MB_ICONQUESTION;
			alert(msg, "${Confirm}", flags, function(ret) {
				var result = false;
				if (ret == modalWin.ID_YES) {
					result = true;
					this.closeFlowAction();
				}
				fn(result);
			}, this);
			return;
		}
	}
	this.closeFlowAction();
	return fn(true);
}