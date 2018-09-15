var modalWindow = imports("freequery.common.modalWindow").getInstance();
var util = jsloader.resolve("freequery.common.util");
var domutil = imports("freequery.lang.domutils").getInstance();
var GroupTreeView = imports("bof.usermanager.GroupTreeView");
var ComboBox = jsloader.resolve("freequery.control.ComboBox");
var compatutil = jsloader.resolve("freequery.lang.compatutil");
var dialogFactory = jsloader.resolve("freequery.dialog.dialogFactory");

var Accordion = function(container) {
	this.init(container, __url, false);
	this.titleSpan = domutils.getElementsByClassName("_titletext", container.previousSibling)[0];
	this._tid = parseInt(Math.random() * 1000);
	this.assignee = [];

	this.monthDay = new ComboBox(this.elemMonthDay, this.body, 60);
	var dayList = [];
	for ( var i = 0; i < 31; i++) {
		dayList[i] = [];
		dayList[i][0] = i + 1;
		dayList[i][1] = i + 1;
	}
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
	var dayList = [];
	for ( var i = 0; i < 31; i++) {
		dayList[i] = [];
		dayList[i][0] = i + 1;
		dayList[i][1] = i + 1;
	}
	this.quarteDay.insertItems(dayList);
	this.quarteDay.setReadOnly(true);
	this.quarteDay.setEnabled(false);

	this.elemLifeNone.checked = true;
	this.updateRaidoName("life" + this._tid, "life");
	this.addListener(this.elemFlowName, "change", this.setTitle, this);
	this.updateRaidoName("nodeType"+this._tid, "nodeType");
	$(".nodeType input:radio",container).on("click",{that:this},function(e){
		var val = this.value;
		var that = e.data.that;
		that.resetOpt();
		if(val=='fill'){ // 节点类型选择填报
			
			//  勾选 填报 和 上报 
			that.setOpt(['fill','report']);
			//  禁用审核和发布
			that.disableOpt(['audit','release']);
			
		}else if(val=='summary'){// 节点类型选择汇总
			
			//  勾选 汇总 和 上报 
			that.setOpt(['summary','report']);
			//  禁用审核和发布
			that.disableOpt(['audit','release']);
		}else if(val=='audit'){// 节点类型选择审核  
			
			//  勾选审核 和 回退
			that.setOpt(['audit']);
			that.setOpt(['backToPre'],true);
			//禁用上报和发布
			that.disableOpt(['report','release']);
			
		}else if(val=='release'){ // 节点类型选择发布
			//  勾选审核 和 回退
			that.setOpt(['release']);
			that.setOpt(['backToPre'],true);
			//禁用审核和上报
			that.disableOpt(['audit','report']);
		}
	});
};

lang.extend(Accordion, "freequery.widget.Module2");

Accordion.prototype.getValue = function() {
	var cobj = $(this.container).find("input[name='opt']")
	var check_val = [];
	for ( var k = 0; k < cobj.length; k++) {
		if (cobj[k].checked)
			check_val.push(cobj[k].value);
	}
	var nodeType = $(".nodeType  input:radio:checked",this.container).val();
	var life = {};
	if (this.elemLifeNone.checked) {
		life.type = "nothing";
	} else if (this.elemLifeMonth.checked) {
		life.type = "month";
		life.monthDay = this.monthDay.getSelectedId();
	} else if (this.elemLifeQuarter.checked) {
		life.type = "quarter";
		life.quarterMonth = this.quarterMonth.getSelectedId();
		life.quarterDay = this.quarteDay.getSelectedId();
	}

	var assignee = [];
	for ( var i = 0; i < this.assignee.length; i++) {
		assignee[i] = {};
		assignee[i].type = this.assignee[i].type;
		assignee[i].value = this.assignee[i].id;
		assignee[i].alias = this.assignee[i].alias;
	}

	var result = {
		id : this._tid,
		name : this.elemFlowName.value,
		assignee : assignee,
		opt : check_val,
		nodeType : nodeType,
		life : life,
		
	};
	var orgVisiable = $(this.element).find("input[name='visiableType']");
	for ( var i = 0; i < orgVisiable.length; i++) {
		if (orgVisiable[i].disabled = true && orgVisiable[i].checked) {
			result.orgVisiable = orgVisiable[i].value;
			break;
		}
	}
	return result;
};

Accordion.prototype.setTitle = function() {
	var name = this.elemFlowName.value;
	var title = this.titleSpan.innerHTML;
	title = title.substring(0, title.indexOf(":")) + ": " + name;
	this.titleSpan.innerHTML = title;
}

Accordion.prototype.fill = function(bean) {
	if (bean) {
		if (bean.id) {
			this._tid = bean.id;
		}
		if (bean.name) {
			this.elemFlowName.value = bean.name;
			this.setTitle();
		}
		if(bean.nodeType){
			var that = this;
			$("input:radio",this.container).each(function(){
				if(this.value==bean.nodeType){
					this.checked=true;
					$(this).click();
				}
			});
		}
		if (bean.opt) {
			var cobj = $(this.container).find("input[name='opt']");
			for ( var k = 0; k < cobj.length; k++) {
				for ( var l = 0; l < bean.opt.length; l++) {
					if (cobj[k].value == bean.opt[l] && !cobj[k].disabled) {
						cobj[k].checked = true;
					}
				}
			}
		}
		
		if (bean.assignee) {
			var alias = "";
			for ( var i = 0; i < bean.assignee.length; i++) {
				var assignee = {};
				assignee.type = bean.assignee[i].type;
				assignee.id = bean.assignee[i].value;
				assignee.alias = bean.assignee[i].alias;
				alias = alias + (i == 0 ? "" : ",") + bean.assignee[i].alias;
				this.assignee.push(assignee);
			}
			this.elemAssigneeAlias.value = alias;
		}

		if (bean.life) {
			if (bean.life.type == "nothing") {
				this.elemLifeNone.checked = true;
			} else if (bean.life.type == "month") {
				this.elemLifeMonth.checked = true;
				this.monthDay.setSelectedItem(bean.life.monthDay);
			} else if (bean.life.type == "quarter") {
				this.elemLifeQuarter.checked = true;
				this.quarterMonth.setSelectedItem(bean.life.quarterMonth);
				this.quarteDay.setSelectedItem(bean.life.quarterDay);
			}
		}
		var radio = $(this.element).find("input[name='visiableType']");
		if (bean.orgVisiable) {
			for (var k = 0; k < radio.length; k++) {
				if (radio[k].value == bean.orgVisiable
						&& !radio[k].disabled) {
					radio[k].checked = true;
				}
			}
		} 
	}
};

Accordion.prototype.disableOpt = function(opt) {
	var cobj = $(this.container).find("input[name='opt']")
	for ( var k = 0; k < cobj.length; k++) {
		for ( var j = 0; j < opt.length; j++) {
			if (cobj[k].value == opt[j]) {
				cobj[k].checked = false;
				cobj[k].disabled = true;
				cobj[k].parentNode.style.color="#999999";
			}
		}
	}
}
Accordion.prototype.must = function(nodeType) {
	var cobj = $(this.container).find(".nodeType input:radio")
	for ( var k = 0; k < cobj.length; k++) {
		if (cobj[k].value == nodeType) {
			cobj[k].checked=true;
		}
		cobj[k].disabled = true;
		cobj[k].parentNode.style.color="#999999";
		
	}
}
Accordion.prototype.disableNodeType = function(nodeTypes) {
	var cobj = $(this.container).find(".nodeType input:radio")
	for ( var k = 0; k < cobj.length; k++) {
		for ( var j = 0; j < nodeTypes.length; j++) {
			if (cobj[k].value == nodeTypes[j]) {
				cobj[k].checked = false;
				cobj[k].disabled = true;
				cobj[k].parentNode.style.color="#999999";
			}
		}
	}
}

Accordion.prototype.resetOpt = function() {
	var cobj = $(this.container).find("input[name='opt']")
	for ( var k = 0; k < cobj.length; k++) {
		if (cobj[k].value != 'summary' && cobj[k].value != 'verify') {
				cobj[k].checked = false;
				cobj[k].disabled = false;
				cobj[k].parentNode.style.color="";
		}else if(cobj[k].value == 'summary'){
			var disabled = $(this.container).find(".nodeType input:radio[value=summary]").attr("disabled");
			if(!disabled){
				cobj[k].checked = false;
				cobj[k].disabled = false;
				cobj[k].parentNode.style.color="";
			}else{
				cobj[k].checked = false;
				cobj[k].disabled = true;
				cobj[k].parentNode.style.color="#999999";
			}
		}
	}
}

Accordion.prototype.setOpt = function(opt,isEnabled) {
	var cobj = $(this.container).find("input[name='opt']")
	for ( var k = 0; k < cobj.length; k++) {
		for ( var j = 0; j < opt.length; j++) {
			if (cobj[k].value == opt[j]) {
				cobj[k].checked = true;
				if(isEnabled){
					cobj[k].disabled = false;
					cobj[k].parentNode.style.color="";
				}else{
					cobj[k].disabled = true;
					cobj[k].parentNode.style.color="#999999";
				}
			}
		}
	}
}
Accordion.prototype.checkValid = function() {
	var value = this.getValue();
	if (util.trim(value.name) == "") {
		alert("${ProcessNodeNameEmpty}");
		return false;
	}
	if (value.assignee.length == 0) {
		alert("${OperatorEmptyError}");
		return false;
	}
	if (value.opt.length == 0) {
		alert("${OperationEmptyError}");
		return false;
	}
	if (!value.nodeType) {
		alert(" ${Youmusthavea} ${NodeType}");
		return false;
	}
	return true;
}

Accordion.prototype.isDirty = function() {
	var bean = this.getValue();
	return false;
};

Accordion.prototype.elemBtnSelectNextId_click_handler = function() {
	var data = {
		createName : "flowCreate",
		selectedObject : this.assignee,
		showExpress : this.__tempId && this.__tempId != 1,
	};
	var dialogConfig = {};
	dialogConfig.title = "${Selectuser}${DunHao}${Usergroup}${DunHao}${Role}";
	dialogConfig.fullName = "smartbi.flow.dialog.SetFlowResourcePermissionDialog";
	dialogConfig.size = dialogFactory.size.LARGE;
	dialogFactory.showDialog(dialogConfig, data, this.afterSelect, this);
}

Accordion.prototype.afterSelect = function(items) {
	if (items.length > 0){
		var assignees = [];	
		var alias = "";
		var len = items.length;
		for ( var i = 0; i < len; i++) {		
			if(items[i].cols && items[i].cols[0].groupDescend){
				assignees.push(items[i]);
				alias = alias + (i == 0 ? "" : ",") + items[i].alias;
				var ret = util.remoteInvokeEx("UserService", "getSubGroups", [items[i].id]);
				if(ret){
					var result = ret.result;
					for(var j = 0;j<result.length;j++){
						result[j].type = "GROUP";
						assignees.push(result[j]);
						alias +=","+ result[j].alias;
					}
				}
			}else{
				assignees.push(items[i]);
				alias = alias + (i == 0 ? "" : ",") + items[i].alias;
			}
		}
		
		this.elemAssigneeAlias.value = alias;
		this.assignee = assignees;
	}
}


Accordion.prototype.elemLifeNone_change_handler = function() {
	this.doLifeChange();
}

Accordion.prototype.elemLifeMonth_change_handler = function() {
	this.doLifeChange();
}

Accordion.prototype.elemLifeQuarter_change_handler = function() {
	this.doLifeChange();
}

Accordion.prototype.doLifeChange = function() {
	if (this.elemLifeNone.checked) {
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

Accordion.prototype.destroy = function() {
	if (this.monthDay)
		this.monthDay.destroy();
	if (this.quarterMonth)
		this.quarterMonth.destroy();
	if (this.quarteDay)
		this.quarteDay.destroy();

	Accordion.superclass.destroy.call(this);
};

Accordion.prototype.updateRaidoName = function(uniqueName, oldRadioName) {
	var radio = null;
	var inputList = this.container.getElementsByTagName('INPUT');
	for ( var i = 0; i < inputList.length; i++) {
		if (inputList[i].name == oldRadioName) {
			inputList[i].name = uniqueName;
		}
	}
}

Accordion.prototype.setVisiableTypeEnable = function() {
	var trs = domutil.getElementsByClassName("_content editblock_blank", this.element); 
	for (var i = 0; i < trs.length; i++) {
		if (trs[i].getAttribute("_itemClass") == "onlyOriginatorVisiable") {
			trs[i].style.display = "";
		}
	}
	var radio = $(this.element).find("input[name='visiableType']")
	for (var i = 0; i < radio.length; i++) {
		radio[i].disabled = false;
		radio[i].parentNode.style.color = "";
		if (radio[i].value == "no") {
			radio[i].checked = true;
		}
	}
}