var modalWindow = imports("freequery.common.modalWindow").getInstance();
var util = jsloader.resolve("freequery.common.util");
var domutil = imports("freequery.lang.domutils").getInstance();
var GroupTreeView = imports("bof.usermanager.GroupTreeView");
var ComboBox = jsloader.resolve("freequery.control.ComboBox");
var compatutil = jsloader.resolve("freequery.lang.compatutil");
var Accordion = jsloader.resolve("smartbi.flow.Accordion");
if (typeof jQuery == 'undefined') {
	jsloader.resolve('thirdparty.jquery.jquery', true);
}
var FlowActionInfoNext = function(container, pageBean) {
	this._parent = container;
	this.init(container, __url, true);
	this.pageBean = pageBean;
	this.accordions = [];
	this.onChange = new CustomEvent();
	this.bean = null;
	this._startId = parseInt(Math.random() * 1000);
	this._endId = parseInt(Math.random() * 1000);
	this.__tempId = 0;
};

lang.extend(FlowActionInfoNext, "bof.baseajax.control.FormView");

FlowActionInfoNext.prototype.getValue = function() {
	this.activitys = {};
	this.paths = {};
	var startActivity = {
		id :this._startId,
		type : "start",
		name : "${Start1}"
	};
	var endActivity = {
		id : this._endId,
		type : "end",
		name : "${End}"
	};
	var startName = "activity_" + startActivity.id;
	this.activitys[startName] = startActivity;
	var endName = "activity_" + endActivity.id;
	this.activitys[endName] = endActivity;

	var firstActivity = null;
	for ( var a = 0; a < this.accordions.length; a++) {
		var activity1 = this.accordions[a].getValue();
		var name = "activity_" + activity1.id;
		this.activitys[name] = activity1;
		if (a == 0) {
			firstActivity = activity1;
			var path = {
				from : startActivity.id,
				to : activity1.id
			}
			var pName = "path" + parseInt(Math.random() * 1000);
			this.paths[pName] = path;
		}
		if (a + 1 < this.accordions.length) {
			var activity2 = this.accordions[a + 1].getValue();
			var path = {
				from : activity1.id,
				to : activity2.id
			}
			var pName = "path" + parseInt(Math.random() * 1000);
			this.paths[pName] = path;
		}
		if (a + 1 == this.accordions.length) {
			var path = {
				from : activity1.id,
				to : endActivity.id
			}
			var pName = "path" + parseInt(Math.random() * 1000);
			this.paths[pName] = path;
		}
	}
	return {
		activitys : this.activitys,
		paths : this.paths,
		firstActivity : firstActivity
	}
};

FlowActionInfoNext.prototype.fill = function(bean) {
	if (bean && bean.activitys) {
		this.oldBean = bean;
		for ( var id in bean.activitys) {
			var act = bean.activitys[id];
			if(act.type == "start" ){
				this._startId = act.id
			}
			if(act.type == "end"){
				this._endId = act.id
			}
			if (act.type != "start" && act.type != "end") {
				this.elemAddAct_click_handler();
				this.accordions[this.accordions.length - 1].fill(act);
			}
		}
	} else {
		this.elemAddAct_click_handler();
	}
};

FlowActionInfoNext.prototype.isDirty = function() {
	return true;
};

FlowActionInfoNext.prototype.elemAddAct_click_handler = function(prev) {
	var that = this;
	this.__tempId++;
	var text = this.__tempId + ": ${ProcessNode}";
	$(this._parent).find(".toggle dl").append(
			"<dt><dt2><dimage/><titletext class='_titletext'>" + text
					+ "</titletext></dt2><btnadd>+</btnadd><btndel>-</btndel></dt>");
	$(this._parent).find(".toggle dl").append("<dd class='_accordion'></dd>");

	this._accordion = domutils.getElementByClassName("_accordion", this._parent);
	this._accordion.setAttribute("__tempId", this.__tempId);
	var accordion = new Accordion(this._accordion);
	accordion.__tempId = this.__tempId;
	if (util.trim(this.pageBean.mechanismParameters) == "") {
		accordion.disableNodeType(['summary']);
		accordion.disableOpt(['summary']);
	}
	if (util.trim(this.pageBean.processVerifySheet) == "") {
		accordion.disableOpt([ 'verify' ]);
	}
	if (prev) {
		if (prev.length) {
			prev = prev[0];
		}
		var dtList = $(this._parent).find(".toggle dl dt");
		var ddList = $(this._parent).find(".toggle dl dd");
		var dt = dtList[dtList.length - 1];
		var dd = ddList[ddList.length - 1];
		$(dd).insertAfter(prev);
		$(dt).insertAfter(prev);

		var flag = false;
		var __tempId = $(prev).attr("__tempId");
		for ( var i = 0; i < that.accordions.length; i++) {
			if (that.accordions[i].__tempId == __tempId) {
				that.accordions.splice(i + 1, 0, accordion);
				flag = true;
				break;
			}
		}
		if (!flag) {
			this.accordions.push(accordion);
		}
	} else {
		this.accordions.push(accordion);
	}
	//第一个填报必填，并且不能选汇总按钮
	if(this.accordions[0]){
		this.accordions[0].must('fill');//节点类型必须是填报
		//操作按钮禁用汇总，审核，发布
		this.accordions[0].disableOpt([ 'summary','audit','release' ]);
		//  操作按钮勾选 填报 和 上报 
		this.accordions[0].setOpt(['fill','report']);
		if (this.__tempId == 1) {
			this.accordions[0].setVisiableTypeEnable();
			$(this._parent).find(".toggle dl dt btndel").css("visibility", "hidden");
		}
	}
	$(this._parent).find("._accordion").removeClass("_accordion");
	$(this._parent).find(".toggle dl dd").not($(this._accordion)).hide();
	$(this._parent).find(".toggle dl dt dt2").unbind();
	$(this._parent).find(".toggle dl dt dt2").click(function() {
		$(that._parent).find(".toggle dl dd").not($(this).parent().next()).hide();
		$(that._parent).find(".toggle dl dt dt2").removeClass("current");
		$(this).parent().next().slideToggle();
		$(this).toggleClass("current");
	});

	$(this._parent).find(".toggle dl dt btnadd").unbind();
	$(this._parent).find(".toggle dl dt btnadd").click(function() {
		var prev = $(this).parent().next();
		that.elemAddAct_click_handler(prev);
	});
	$(this._parent).find(".toggle dl dt btndel").unbind();
	$(this._parent).find(".toggle dl dt btndel").click(function() {
		var __tempId = $(this).parent().next().attr("__tempId");
		for (var i = 0; i < that.accordions.length; i++) {
			if (that.accordions[i].__tempId == __tempId) {
				that.accordions[i].destroy();
				that.accordions.splice(i, 1);
				break;
			}
		}
		$(this).parent().next().remove();
		$(this).parent().remove();
		if (that.accordions.length == 0) {
			that.elemAddAct_click_handler();
		}
	});
}

FlowActionInfoNext.prototype.checkValid = function() {
	for ( var a = 0; a < this.accordions.length; a++) {
		var flag = this.accordions[a].checkValid();
		if (!flag) {
			if (this.accordions[a].container.style.display == "none") {
				$(this.accordions[a].container.previousSibling.firstChild).trigger("click");
			}
			return flag;
		}
	}
	return true;
}

FlowActionInfoNext.prototype.refreshButtonStatus = function(ev) {
	return;
};

FlowActionInfoNext.prototype.destroy = function() {
	for ( var i = 0; i < this.accordions.length; i++) {
		this.accordions[i].destroy();
	}

	FlowActionInfoNext.superclass.destroy.call(this);
};
