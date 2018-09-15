var TaskList = jsloader.resolve('smartbi.task.TaskListView');

var MyInstanceCommand = function() {
	this.parentObj = document.getElementById('rightContent');
	this.onClose = new CustomEvent('OnClose', this);
};
lang.extend(MyInstanceCommand, 'freequery.widget.Module');

MyInstanceCommand.prototype.destroy = function() {
	this.close();
	this.onClose = null;
	this.parentObj = null;
	MyInstanceCommand.superclass.destroy.call(this);
};

MyInstanceCommand.prototype.execute = function(action, args) {
	if (action == 'OPEN') {
		if (!this.instancePage) {
			var InstancePage = jsloader.resolve('smartbi.workflow.InstancePage');
			this.instancePage = new InstancePage(this.parentObj,this);
			//this.taskList = new TaskList(this.parentObj,this);
			//this.taskList.onClose.subscribe(this.doOnClose, this);
		}
	}
};

MyInstanceCommand.prototype.close = function() {
	if (this.taskList) {
		this.taskList.destroy();
		this.taskList = null;
	}
};

MyInstanceCommand.prototype.doOnClose = function(e) {
	this.onClose.fire(this);
};