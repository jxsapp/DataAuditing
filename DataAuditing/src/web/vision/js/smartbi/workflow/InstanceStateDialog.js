var BaseDialogEx = jsloader.resolve("freequery.dialog.BaseDialogEx");
var domutils = jsloader.resolve("freequery.lang.domutils");
var util = jsloader.resolve("freequery.common.util");
var InstanceStateView = jsloader.resolve("smartbi.workflow.InstanceStateView");
var Configuration = jsloader.resolve("Configuration");
var compatutil = jsloader.resolve("freequery.lang.compatutil");
var InstanceStateDialog = function() {
	
};

lang.extend(InstanceStateDialog, BaseDialogEx);

InstanceStateDialog.prototype.init = function(parent, data, fn, obj) {
	InstanceStateDialog.superclass.init.call(this, parent, data, fn, obj);
	this.list = data[1];
	BaseDialogEx.superclass.init.call(this, this.dialogBody, __url, true);
	compatutil.fixFirefoxScroll(this.element);
	var instanceStateView = new InstanceStateView(this.elemStateDiv,this);
}



InstanceStateDialog.prototype.destroy = function() {
	InstanceStateDialog.superclass.destroy.call(this);
}
