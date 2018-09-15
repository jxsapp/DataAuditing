var BaseDialogEx = jsloader.resolve("freequery.dialog.BaseDialogEx");
var domutils = jsloader.resolve("freequery.lang.domutils");
var util = jsloader.resolve("freequery.common.util");
var PagePanel = jsloader.resolve("freequery.control.PagePanel");
var Configuration = jsloader.resolve("Configuration");

var TaskRemarkDialog = function() {
	
};

lang.extend(TaskRemarkDialog, BaseDialogEx);

TaskRemarkDialog.prototype.init = function(parent, data, fn, obj) {
	TaskRemarkDialog.superclass.init.call(this, parent, data, fn, obj);
	this.task = data[1];
	BaseDialogEx.superclass.init.call(this, this.dialogBody, __url, true);
	var template = domutils.doGet("js/smartbi/task/TaskRemarkDialog.html");
	this.element.innerHTML = template;
	this.remark = domutils.findElementByClassName(this.element, "_remark");
	

}



TaskRemarkDialog.prototype.destroy = function() {
	TaskRemarkDialog.superclass.destroy.call(this);
}

TaskRemarkDialog.prototype.doOK  = function(){
	if(this.task&&this.task.taskId){
		var ret2 = util.remoteInvoke("TaskService", "addOperationLog", [this.task.taskId,this.remark.value]);	
	}else{
		alert("${InstanceIsGone}！");
	}
	this.close(true,null);
}
