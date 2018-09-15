var TaskList = jsloader.resolve('smartbi.task.TaskListView');
var TaskPage = jsloader.resolve('smartbi.task.TaskPage');

var MyTaskCommand = function() {
	this.parentObj = document.getElementById('rightContent');
	this.onClose = new CustomEvent('OnClose', this);
};
lang.extend(MyTaskCommand, 'freequery.widget.Module');

MyTaskCommand.prototype.destroy = function() {
	this.close();
	this.onClose = null;
	this.parentObj = null;
	MyTaskCommand.superclass.destroy.call(this);
};

MyTaskCommand.prototype.execute = function(action, args) {
	if (action == 'OPEN') {
		if (!this.taskList) {
			this.taskList = new TaskPage(this.parentObj,this);
			//this.taskList = new TaskList(this.parentObj,this);
			//this.taskList.onClose.subscribe(this.doOnClose, this);
		}
	}
};

MyTaskCommand.prototype.close = function() {
	if (this.taskList) {
		this.taskList.destroy();
		this.taskList = null;
	}
};

MyTaskCommand.prototype.doOnClose = function(e) {
	this.onClose.fire(this);
};