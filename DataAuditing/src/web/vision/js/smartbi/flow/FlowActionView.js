var DialogFactory = imports("freequery.dialog.dialogFactory");
var FlowActionInfo = imports("smartbi.flow.FlowActionInfo");
var FlowActionInfoNext = imports("smartbi.flow.FlowActionInfoNext");
var util = imports("freequery.common.util").getInstance();
var CustomPortletListView = null;
var compatutil = jsloader.resolve("freequery.lang.compatutil");

var FlowActionView = function(parentContainer) {
	FlowActionView.superclass.constructor.call(this, parentContainer);

	this.parentContainer = parentContainer;

	this.onSave = new CustomEvent();
};

lang.extend(FlowActionView, "freequery.common.FrameView");

FlowActionView.prototype.destroy = function() {
	if (this.pageView)
		delete this.pageView.menuLevel;

	if (this.pageView)
		this.pageView.destroy();
	if (this.pageViewNext)
		this.pageViewNext.destroy();
	FlowActionView.superclass.destroy.call(this);
}

FlowActionView.prototype.show = function() {
	this.init(this.frameViewContainer, __url);

	this.viewSlot = this.elemWizardSlot;
	this.currentStep = 0;
	this.changeButtonState();
	this.openPageInfo();

	if (!util.checkFunctionValid("MANAGE_CREATEREPORT_WORKFLOW"))
		this.elemSave.style.display = "none";

}

FlowActionView.prototype.showWithInfo = function(obj) {
	this.pageBean = {};
	if (obj[0]) {
		var defineStr = obj[0].workflowDefine;
		var defineObj = lang.parseJSON(defineStr);
		this.pageBean.processVerifySheet = defineObj.processVerifySheet;
		this.pageBean.life = lang.parseJSON(obj[0].workflowLifeCycle);
		this.pageNextBean = defineObj;
	}
	
	if (obj[1] && obj[1].length > 0) {
		this.pageBean.singleSheet = obj[1][0].bySheet;
	}
	if (obj[1] && obj[2]) {
		var rdList = obj[1];
		var reportMap = obj[2];
		var spreadsheetId = "";
		var spreadsheetAlias = "";
		var allSpreadSheet = [];
		for ( var i = 0; i < rdList.length; i++) {
			var id = rdList[i].spreadsheetId;
			var fakeNode = {};
			fakeNode.id = id;
			fakeNode.name = id;
			fakeNode.alias = id;
			fakeNode.type = "SPREADSHEET_REPORT";

			var report = reportMap[id];
			if (report) {
				fakeNode.name = report.name;
				fakeNode.alias = report.alias;
				spreadsheetId = spreadsheetId + (i == 0 ? "" : ",") + id;
				spreadsheetAlias = spreadsheetAlias + (i == 0 ? "" : ",") + report.alias;
			} else {
				spreadsheetId = spreadsheetId + (i == 0 ? "" : ",") + id;
				spreadsheetAlias = spreadsheetAlias + (i == 0 ? "" : ",") + id;
			}
			allSpreadSheet.push(fakeNode);
		}
		this.pageBean.spreadsheetId = spreadsheetId;
		this.pageBean.spreadsheetAlias = spreadsheetAlias;
		this.pageBean.allSpreadSheet = allSpreadSheet;
	}

	if (obj[1] && obj[3] && obj[1].length > 0) {
		var rdList = obj[1];
		var paramMap = obj[3];

		this.pageBean.mechanismParameters = rdList[0].mechanismParameter;
		if (this.pageBean.mechanismParameters) {
			var fakeNodeArray = [];
			var fakeNode = {};
			fakeNode.id = "";
			fakeNode.name = this.pageBean.mechanismParameters;
			fakeNode.alias = this.pageBean.mechanismParameters;
			fakeNode.type = "PARAM";
			fakeNodeArray.push(fakeNode);

			var param = paramMap[this.pageBean.mechanismParameters];
			if (param) {
				this.pageBean.mechanismParametersAlias = param.alias;
				fakeNode.id = param.id;
				fakeNode.alias = param.alias;
			} else {
				this.pageBean.mechanismParametersAlias = this.pageBean.mechanismParameters;
			}

			this.pageBean.allMechanismParameters = fakeNodeArray;
		}

		var processParametersAlias = "";
		var otherParameters = rdList[0].otherParameters;
		var fakeNodeArray = [];
		if (otherParameters) {
			var nameList = otherParameters.split(",");
			for ( var i = 0; i < nameList.length; i++) {
				var name = nameList[i];

				var fakeNode = {};
				fakeNode.id = "";
				fakeNode.name = name;
				fakeNode.alias = name;
				fakeNode.type = "PARAM";
				fakeNodeArray.push(fakeNode);

				var param = paramMap[name];
				if (param) {
					processParametersAlias = processParametersAlias + (i == 0 ? "" : ",") + param.alias;
					fakeNode.id = param.id;
					fakeNode.alias = param.alias;
				} else {
					processParametersAlias = processParametersAlias + (i == 0 ? "" : ",") + name;
				}
			}
		}
		this.pageBean.processParameters = otherParameters;
		this.pageBean.processParametersAlias = processParametersAlias;
		this.pageBean.allProcessParameters = fakeNodeArray;
	}

	this.show();
}

// 修改当前状态下的按钮可见状态
FlowActionView.prototype.changeButtonState = function() {
	if (this.currentStep == 0) {
		this.elemPrevStep.disabled = "disabled";
		this.elemNextStep.disabled = "";
		this.elemSave.disabled = "disabled";
	} else if (this.currentStep == 1) {
		this.elemPrevStep.disabled = "";
		this.elemNextStep.disabled = "disabled";
		this.elemSave.disabled = "";
	} else {
		this.elemPrevStep.disabled = "";
		this.elemNextStep.disabled = "";
		this.elemSave.disabled = "";
	}
}

FlowActionView.prototype.selectStep = function(toStep) {
	var flag = true;
	switch (this.currentStep) {
	case 0:
		flag = this.afterPageInfo();
		break;
	case 1:
		flag = this.afterPageInfoNext();
		break;
	}
	if (!flag)
		return;

	this.currentStep = toStep;
	switch (toStep) {
	case 0:
		this.openPageInfo();
		break;
	case 1:
		this.openPageInfoNext();
		break;
	}

	this.changeButtonState();
}

FlowActionView.prototype.elemPrevStep_click_handler = function() {
	if (this.currentStep > 0 && this.currentStep < 2)
		this.selectStep(this.currentStep - 1);
}

FlowActionView.prototype.elemNextStep_click_handler = function() {
	if (this.currentStep >= 0 && this.currentStep < 1)
		this.selectStep(this.currentStep + 1);
}

FlowActionView.prototype.elemSave_click_handler = function() {
	if (this.collectAllInfo()) {
		this.onSave.fire();
	}
}

FlowActionView.prototype.elemCancle_click_handler = function() {
	this.onClose.fire();
}

FlowActionView.prototype.collectAllInfo = function() {
	var flag = true;
	if (this.currentStep == 0) {
		flag = this.collectPageInfo();
	} else if (this.currentStep == 1) {
		flag = this.collectPageInfoNext();
	}
	return flag;
}

// 页面信息
FlowActionView.prototype.openPageInfo = function() {
	this.pageView = new FlowActionInfo(this.viewSlot);

	if (!this.pageBean)
		this.pageBean = {};

	this.pageView.fill(this.pageBean);
}

// 收集页面布局
FlowActionView.prototype.afterPageInfo = function() {
	if (this.pageView) {
		this.pageBean = this.pageView.getValue();
		this.pageDirty = this.pageView.isDirty();
		return this.pageView.checkValid();
	}
	return true;
}
FlowActionView.prototype.collectPageInfo = function() {
	if (this.pageView) {
		this.pageBean = this.pageView.getValue();
		return this.pageView.checkValid();
	}
	return true;
}

// ------------------------选择下一页--------------------

// 初始化下一页选择界面
FlowActionView.prototype.openPageInfoNext = function() {
	if (this.pageViewNext)
		this.pageViewNext.destroy();
	this.pageViewNext = new FlowActionInfoNext(this.viewSlot, this.pageBean);

	if (!this.pageNextBean)
		this.pageNextBean = {};

	this.pageViewNext.fill(this.pageNextBean);

	return true;
};

// 获取用户选择的下一页
FlowActionView.prototype.afterPageInfoNext = function() {
	if (this.pageViewNext) {
		this.pageNextBean = this.pageViewNext.getValue();
		this.pageNextDirty = this.pageViewNext.isDirty();
		return true;
	}
	return true;
}

FlowActionView.prototype.collectPageInfoNext = function() {
	if (this.pageViewNext) {
		this.pageNextBean = this.pageViewNext.getValue();
		return this.pageViewNext.checkValid();
	}
}

FlowActionView.prototype.setDisabled = function() {
	this.elemSave.disabled = true;
}

FlowActionView.prototype.isDirty = function() {
	if (!util.checkFunctionValid("MANAGE_CREATEREPORT_WORKFLOW")) {
		return false;
	}
	if (this.pageView && this.pageView.isDirty()) {
		return true;
	}
	if (this.pageViewNext && this.pageViewNext.isDirty()) {
		return true;
	}
	return false;
}
