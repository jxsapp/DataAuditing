var BaseDialogEx = jsloader.resolve("freequery.dialog.BaseDialogEx");
var SummaryStateView = jsloader.resolve("smartbi.task.SummaryStateView");
var compatutil = jsloader.resolve("freequery.lang.compatutil");

var SummaryStateDialog = function() {

};

lang.extend(SummaryStateDialog, BaseDialogEx);

SummaryStateDialog.prototype.init = function(parent, data, fn, obj) {
	SummaryStateDialog.superclass.init.call(this, parent, data, fn, obj);
	this.list = data[1];
	BaseDialogEx.superclass.init.call(this, this.dialogBody, __url, true);
	compatutil.fixFirefoxScroll(this.element);
	var summaryStateView = new SummaryStateView(this.elemStateDiv, this);

	if (this.list) {
		var summary = true;
		for ( var i = 0; i < this.list.length; i++) {
			if (this.list[i].flg == "unfinished")
				summary = false;
		}
		if (!summary) {
			this.setButtonVisible("BTNOK", false);
		}
	}
}

SummaryStateDialog.prototype.doOK = function() {
	if (this.list) {
		var summary = true;
		for ( var i = 0; i < this.list.length; i++) {
			if (this.list[i].flg == "unfinished")
				summary = false;
		}
	}
	if (summary)
		this.close(true, null);
	else
		this.close(false, null);
}

SummaryStateDialog.prototype.destroy = function() {
	SummaryStateDialog.superclass.destroy.call(this);
}
