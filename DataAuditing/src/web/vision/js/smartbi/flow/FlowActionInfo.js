var modalWindow = imports("freequery.common.modalWindow").getInstance();
var util = jsloader.resolve("freequery.common.util");
var domutil = imports("freequery.lang.domutils").getInstance();
var GroupTreeView = imports("bof.usermanager.GroupTreeView");
var dialogFactory = jsloader.resolve("freequery.dialog.dialogFactory");
var ComboBox = jsloader.resolve("freequery.control.ComboBox");
var compatutil = jsloader.resolve("freequery.lang.compatutil");

var FlowActionInfo = function(container) {
	this.init(container, __url, true);
	this._tid = parseInt(Math.random() * 1000);
	compatutil.fixFirefoxScroll(this.elemContentTable.parentNode);
	this.onChange = new CustomEvent();
	this.bean = null;

	this.allSpreadSheet = [];
	this.allMechanismParameters = [];
	this.allProcessParameters = [];

	var dayList = [];
	for ( var i = 0; i < 31; i++) {
		dayList[i] = [];
		dayList[i][0] = i + 1;
		dayList[i][1] = i + 1;
	}
	dayList.push(['L','L']);

	this.monthDay = new ComboBox(this.elemMonthDay, this.body, 60);
	this.monthDay.insertItems(dayList);
	this.monthDay.setReadOnly(true);
	this.monthDay.setEnabled(false);

	this.quarterMonth = new ComboBox(this.elemQuarterMonth, this.body, 100);
	var monthList = [];
	for ( var i = 0; i < 3; i++) {
		monthList[i] = [];
		monthList[i][0] = i + 1;
		monthList[i][1] = "${mc.the}" + (i + 1) + "${Month1}";
	}
	this.quarterMonth.insertItems(monthList);
	this.quarterMonth.setReadOnly(true);
	this.quarterMonth.setEnabled(false);

	var textNode = document.createTextNode(" ");
	this.elemQuarterMonth.appendChild(textNode);
	this.quarteDay = new ComboBox(this.elemQuarterMonth, this.body, 60);
	this.quarteDay.insertItems(dayList);
	this.quarteDay.setReadOnly(true);
	this.quarteDay.setEnabled(false);

	this.elemLifeNone.checked = true;
	this.updateRaidoName("life" + this._tid, "life");
};

lang.extend(FlowActionInfo, "bof.baseajax.control.FormView");

FlowActionInfo.prototype.getValue = function() {
	var life = {};
	if (this.elemLifeNone.checked) {
		life.type = "nothing";
	} else if (this.elemLifeDay.checked) {
		life.type = "day";
	} else if (this.elemLifeMonth.checked) {
		life.type = "month";
		life.monthDay = this.monthDay.getSelectedId();
	} else if (this.elemLifeQuarter.checked) {
		life.type = "quarter";
		life.quarterMonth = this.quarterMonth.getSelectedId();
		life.quarterDay = this.quarteDay.getSelectedId();
	}

	return {
		allSpreadSheet : this.allSpreadSheet,
		spreadsheetId : this.elemSpreadSheetId.value,
		spreadsheetAlias : this.elemSpreadSheet.value,
		allMechanismParameters : this.allMechanismParameters,
		mechanismParameters : this.elemMechanismParametersId.value,
		mechanismParametersAlias : this.elemMechanismParameters.value,
		allProcessParameters : this.allProcessParameters,
		processParameters : this.elemProcessParametersId.value,
		processParametersAlias : this.elemProcessParameters.value,
		processVerifySheet : this.elemProcessVerifySheet.value,
		singleSheet : this.elemSingleSheet.checked ? true : false,
		life : life
	}
};

FlowActionInfo.prototype.fill = function(bean) {
	this.oldBean = bean;
	if (bean.allSpreadSheet)
		this.allSpreadSheet = bean.allSpreadSheet;
	if (bean.spreadsheetId)
		this.elemSpreadSheetId.value = bean.spreadsheetId;
	if (bean.spreadsheetAlias)
		this.elemSpreadSheet.value = bean.spreadsheetAlias;
	if (bean.allMechanismParameters)
		this.allMechanismParameters = bean.allMechanismParameters;
	if (bean.mechanismParameters)
		this.elemMechanismParametersId.value = bean.mechanismParameters;
	if (bean.mechanismParametersAlias)
		this.elemMechanismParameters.value = bean.mechanismParametersAlias;
	if (bean.allProcessParameters)
		this.allProcessParameters = bean.allProcessParameters;
	if (bean.processParameters)
		this.elemProcessParametersId.value = bean.processParameters;
	if (bean.processParametersAlias)
		this.elemProcessParameters.value = bean.processParametersAlias;
	if (bean.processVerifySheet)
		this.elemProcessVerifySheet.value = bean.processVerifySheet;

	if (bean.singleSheet)
		this.elemSingleSheet.checked = true;
	if (bean.life) {
		if (bean.life.type == "nothing") {
			this.elemLifeNone.checked = true;
		} else if (bean.life.type == "day") {
			this.elemLifeDay.checked = true;
		} else if (bean.life.type == "month") {
			this.elemLifeMonth.checked = true;
			this.monthDay.setSelectedItem(bean.life.monthDay);
			this.monthDay.setEnabled(true);
		} else if (bean.life.type == "quarter") {
			this.elemLifeQuarter.checked = true;
			this.quarterMonth.setSelectedItem(bean.life.quarterMonth);
			this.quarteDay.setSelectedItem(bean.life.quarterDay);
			this.quarterMonth.setEnabled(true);
			this.quarteDay.setEnabled(true);
		}
	}
};

/*
 * 电子表格ID选择按钮
 */
FlowActionInfo.prototype.elemBnSelectId_click_handler = function(e) {
	var dialogConfig = {};
	dialogConfig.title = "${SelectSpreadSheet}";
	dialogConfig.fullName = "smartbi.flow.dialog.SelectSpreadSheetDialog";
	dialogConfig.size = dialogFactory.size.LARGE;
	var data = this.allSpreadSheet;
	dialogFactory.showDialog(dialogConfig, data, this.callBackSpreadSheetSelected, this);
}

FlowActionInfo.prototype.callBackSpreadSheetSelected = function(isOk, listStr) {
	if (!isOk)
		return;
	var list = lang.parseJSON(listStr);
	if (!list || list.length == 0) {
		this.allSpreadSheet = [];
		this.elemSpreadSheetId.value = "";
		this.elemSpreadSheet.value = "";

	} else {
		this.allSpreadSheet = list;
		var id = "", alias = "";
		for ( var index = 0; index < list.length; index++) {
			id += list[index].id;
			alias += list[index].alias;
			if (index < list.length - 1) {
				id += ",";
				alias += ",";
			}
		}
		this.elemSpreadSheetId.value = id;
		this.elemSpreadSheet.value = alias;
	}
}

/*
 * 汇总相关的机构参数
 */
FlowActionInfo.prototype.elemBtnMechanismParameters_click_handler = function(e) {
	var dialogConfig = {};
	dialogConfig.title = "${SelectSummaryPara}";
	dialogConfig.fullName = "smartbi.flow.dialog.SelectSummaryParaDialog";
	dialogConfig.size = dialogFactory.size.MIDDLE;
	var data = {};
	data.currentSelect = this.allMechanismParameters;
	data.multiSelect = false;
	data.filterParams = this.allProcessParameters;
	data.dialogType = "SummaryPara";
	dialogFactory.showDialog(dialogConfig, data, this.callBackMechanismParameters, this);
}

FlowActionInfo.prototype.callBackMechanismParameters = function(isOk, listStr) {
	if (!isOk)
		return;
	var list = lang.parseJSON(listStr);
	if (!list || list.length == 0) {
		this.allMechanismParameters = [];
		this.elemMechanismParametersId.value = "";
		this.elemMechanismParameters.value = "";

	} else {
		this.allMechanismParameters = list;
		var name = "", alias = "";
		if (list.length > 0) {
			name = list[0].name;
			alias = list[0].alias;
		}
		this.elemMechanismParametersId.value = name;
		this.elemMechanismParameters.value = alias;
	}
}

/*
 * 其它参数
 */
FlowActionInfo.prototype.elemBtnParameters_click_handler = function(e) {
	var dialogConfig = {};
	dialogConfig.title = "${SelectOtherPara}";
	dialogConfig.fullName = "smartbi.flow.dialog.SelectSummaryParaDialog";
	dialogConfig.size = dialogFactory.size.MIDDLE;
	var data = {};
	data.currentSelect = this.allProcessParameters;
	data.filterParams = this.allMechanismParameters;
	data.multiSelect = true;
	data.dialogType = "OtherPara";
	dialogFactory.showDialog(dialogConfig, data, this.callBackParameters, this);
}

FlowActionInfo.prototype.callBackParameters = function(isOk, listStr) {
	if (!isOk)
		return;
	var list = lang.parseJSON(listStr);
	if (!list || list.length == 0) {
		this.allProcessParameters = [];
		this.elemProcessParametersId.value = "";
		this.elemProcessParameters.value = "";
	} else {
		this.allProcessParameters = list;
		var name = "", alias = "";
		for ( var index = 0; index < list.length; index++) {
			name += list[index].name;
			alias += list[index].alias;
			if (index < list.length - 1) {
				name += ",";
				alias += ",";
			}
		}
		this.elemProcessParametersId.value = name;
		this.elemProcessParameters.value = alias;
	}
}

FlowActionInfo.prototype.elemLifeNone_change_handler = function() {
	this.doLifeChange();
}

FlowActionInfo.prototype.elemLifeDay_change_handler = function() {
	this.doLifeChange();
}

FlowActionInfo.prototype.elemLifeMonth_change_handler = function() {
	this.doLifeChange();
}

FlowActionInfo.prototype.elemLifeQuarter_change_handler = function() {
	this.doLifeChange();
}

FlowActionInfo.prototype.doLifeChange = function() {
	if (this.elemLifeNone.checked || this.elemLifeDay.checked) {
		this.monthDay.setEnabled(false);
		this.quarterMonth.setEnabled(false);
		this.quarteDay.setEnabled(false);
	} else if (this.elemLifeMonth.checked) {
		this.monthDay.setEnabled(true);
		this.quarterMonth.setEnabled(false);
		this.quarteDay.setEnabled(false);
	} else if (this.elemLifeQuarter.checked) {
		this.monthDay.setEnabled(false);
		this.quarterMonth.setEnabled(true);
		this.quarteDay.setEnabled(true);
	}
}

FlowActionInfo.prototype.refreshButtonStatus = function() {
	// overwrite superclass
}

FlowActionInfo.prototype.checkValid = function() {
	if (util.trim(this.elemSpreadSheetId.value) == "") {
		alert("${ReportEmptyError}");
		return false;
	}
	if (this.elemMechanismParametersId.value == "" && this.elemProcessParametersId.value == ""){
		alert("'${MechanismParameters}','${ProcessParameters}' ${BothEmptyError}");
		return false;
	}
	return true;
}

FlowActionInfo.prototype.isDirty = function() {
	var bean = this.getValue();
	if (this.oldBean && this.oldBean.life && bean && bean.life) {
		return this.oldBean.spreadsheetId != bean.spreadsheetId
				|| this.oldBean.mechanismParameters != bean.mechanismParameters
				|| this.oldBean.processParameters != bean.processParameters
				|| this.oldBean.processVerifySheet != bean.processVerifySheet
				|| this.oldBean.singleSheet != bean.singleSheet || this.oldBean.life.type != bean.life.type;
	}
	return true;
};

FlowActionInfo.prototype.destroy = function() {
	if (this.monthDay)
		this.monthDay.destroy();
	if (this.quarterMonth)
		this.quarterMonth.destroy();
	if (this.quarteDay)
		this.quarteDay.destroy();

	FlowActionInfo.superclass.destroy.call(this);
};

FlowActionInfo.prototype.updateRaidoName = function(uniqueName, oldRadioName) {
	var radio = null;
	var inputList = this.container.getElementsByTagName('INPUT');
	for (var i = 0; i < inputList.length; i++) {
		if (inputList[i].name == oldRadioName) {
			inputList[i].name = uniqueName;
		}
	}
}