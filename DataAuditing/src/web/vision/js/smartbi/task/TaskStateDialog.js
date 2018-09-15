var BaseDialogEx = jsloader.resolve("freequery.dialog.BaseDialogEx");
var domutils = jsloader.resolve("freequery.lang.domutils");
var util = jsloader.resolve("freequery.common.util");
var compatutil = jsloader.resolve("freequery.lang.compatutil");

var TaskStateDialog = function() {

};

lang.extend(TaskStateDialog, BaseDialogEx);

TaskStateDialog.prototype.init = function(parent, data, fn, obj) {
	TaskStateDialog.superclass.init.call(this, parent, data, fn, obj);
	this.ins = data[1];
	BaseDialogEx.superclass.init.call(this, this.dialogBody, __url, true);
	compatutil.fixFirefoxScroll(this.element);

	this.fillList();
}

TaskStateDialog.prototype.getItemList = function() {
	var itemList = [];

	var taskList = util.remoteInvoke("TaskService", "getInstanceCompleteTasks", [ this.ins.instanceId ]);
	if (taskList.result) {
		for ( var i = 0; i < taskList.result.length; i++) {
			var task = new Object();
			task = taskList.result[i];
			itemList.push(task);
		}
	}
	this.itemList = itemList;
	return itemList;
};

TaskStateDialog.prototype.fillList = function() {
	var itemList = this.getItemList();
	var len = itemList && itemList.length || 0;

	var buff = [];
	buff.push('<tr><td width="40%">&nbsp;</td><td align="center" width="20%"><img src="img/flow/icon06.gif"></td>'
			+ '<td width="40%">&nbsp;</td></tr>');

	for ( var i = 0; i < len; i++) {
		var timeText = itemList[i].time;
		var textList = timeText.split(" ");
		buff.push('<tr valign="middle">');

		buff.push('<td align="right" style="color: #63CD52;">');
		buff.push('<font size=4>' + textList[0] + '</font>');
		buff.push('<br>');
		buff.push(textList[1]);
		buff.push('</td>');

		buff.push('<td align="center"><img src="img/flow/icon07.gif"></td>');

		buff.push('<td>');
		buff.push('<font size=4>' + itemList[i].taskName + '</font>');
		buff.push('<br>');
		if (itemList[i].userName != "" && itemList[i].userName != "已有流程进入汇总" ) {
			buff.push('操作用户：' + itemList[i].useralias);
			buff.push('<br>');
		}
		if (itemList[i].detail != "") {
			buff.push('操作及备注：' + itemList[i].detail);
		}
		buff.push('</td>');

		buff.push('</tr>');
	}

	this.elemTbody.innerHTML = buff.join('');

};

TaskStateDialog.prototype.destroy = function() {
	TaskStateDialog.superclass.destroy.call(this);
}
