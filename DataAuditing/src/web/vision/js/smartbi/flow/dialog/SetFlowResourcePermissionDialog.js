/*
 * FileName  : SetFlowResourcePermissionDialog.js
 * Copyright : Copyright (C) 2002-2007 
 * Creator   : QianJiJin
 * History   : 
*/
var SetResourcePermissionDialog = jsloader.resolve("freequery.permission.SetResourcePermissionDialog");
var util = jsloader.resolve("freequery.common.util");
var CustomEvent = jsloader.resolve("freequery.lang.CustomEvent");
var domutils = jsloader.resolve("freequery.lang.domutils");
var modalWindow = jsloader.resolve("freequery.common.modalWindow");
var UserTree = jsloader.resolve("freequery.permission.UserTree");
var TabPanel = jsloader.resolve("freequery.control.TabPanel");
var SimpleListView = jsloader.resolve("freequery.control.SimpleListView");
var PagePanel = jsloader.resolve("freequery.control.PagePanel");
var UserTreeSearchBar = jsloader.resolve("bof.usermanager.treesearch.UserTreeSearchBar");
var compatutil = jsloader.resolve("freequery.lang.compatutil");
var FunctionTree = jsloader.resolve("smartbi.flow.dialog.FunctionTree");
var ExpressionEditor = jsloader.resolve("freequery.control.ExpressionEditor");

SetFlowResourcePermissionDialog = function(){
	SetFlowResourcePermissionDialog.superclass.constructor.call(this);
};
lang.extend(SetFlowResourcePermissionDialog, SetResourcePermissionDialog);

SetFlowResourcePermissionDialog.prototype.initFunctionTree = function(container) {
	container.style.height = "100%";
	container.style.overflow = "hidden";
	var table = document.createElement("TABLE");
	table.style.height = "100%";
	table.style.width = "100%";
	table.style.tableLayout = "fixed";
	table.setAttribute("cellspacing", 0);
	table.setAttribute("cellpadding", 0);
	container.appendChild(table);	

	var viewTr = table.insertRow(-1);
	viewTr.style.height = "100%";
	var viewTd = viewTr.insertCell(-1);
	var treeDiv = document.createElement("div");
	viewTd.appendChild(treeDiv);
	compatutil.fixFirefoxScroll(treeDiv);
	this.functionTree = new FunctionTree(treeDiv);
	this.functionTree.render(true);
	this.functionTree.selectDefaultNode();
}

SetFlowResourcePermissionDialog.prototype.initSelectedObject = function(){
	var selectedObject = (window.dialogArguments || window.args || 0).selectedObject;
	selectedObject = selectedObject || this.selectedObject;
	if(selectedObject){
		var len = selectedObject.length;
		if (len > 0 && selectedObject[0].type != "CUSTOM") {
			for( var index = 0; index < len ; index++){
				var obj = selectedObject[index];
			    var rowData = {};
			    rowData.id = obj.id;
			    rowData.type = obj.type;
			    rowData.cols = [];
			    var data = {};
			    data.alias = obj.name || obj.alias;
			    rowData.cols.push(data);
				this.selectedListView.insertRow(rowData);
				this.btnRemove.disabled = false;
				this.btnOK.disabled = false;
			};
		}
	}
	this.selectedListView.onRowDblClick.subscribe(this.doSelRowDblClick,this,true);
};

SetFlowResourcePermissionDialog.prototype.init = function(parent,data,fn,obj){
	SetResourcePermissionDialog.superclass.init.call(this,parent,data,fn,obj);
	if (data && data.multiple === false)
		this.multiple = false;
	if (data && data.selectedObject)
		this.selectedObject = data.selectedObject;
	this.parent = parent;
	this.element = document.createElement("div");
	this.element.style.height = "100%";
	this.dialogBody.appendChild(this.element);
	this.dialogBody.style.border = 'none';
	this.dialogBody.style.backgroundColor = 'transparent';
	var template = domutils.doGet("js/smartbi/flow/dialog/SetFlowResourcePermissionDialog.template");
	this.element.innerHTML = template;
	this.roleContainer = domutils.findElementByClassName(this.element,"_list_role_div");
	this.userContainer = domutils.findElementByClassName(this.element,"_list_user_div");
	this.initRolesListView(this.roleContainer);
	this.selectedUserEl = domutils.findElementByClassName(this.element,"_selected_user_div");

	var cfg2 = {};
	cfg2.width = "100%";
	cfg2.height = 150;
	cfg2.cols = [ {
		colName : "${Theselectedroles}/${Users}/${Group}",
		width : "60%"
	}, {
		colName : "${Whetherornottoapplytosubgroups}",
		width : "40%"
	} ];

	cfg2.cols = [ {
		colName : "${Theselectedroles}/${Users}/${Group}",
		width : "58%"
	}, {
		colName : "${Whetherornottoapplytosubgroups}",
		width : "40%"
	} ];
    this.selectedListView =  new SimpleListView(cfg2);
	this.selectedListView.init(this.selectedUserEl);
	this.selectedListView.showSearchBar();
	this.selectedListView.onRemoveRow.subscribe(this.SetRemoveBtnState,this,true);
    this.selectedListView.setMultSelect(true);
    this.selectedListView.autoSetRowWidth(true);
	
	var btnAdd = domutils.findElementByClassName(this.element,"_btnAdd");
	this.btnRemove = domutils.findElementByClassName(this.element,"_btnRemove");
	this.btnRemove.disabled = true;
	this.btnOK = this.getButton("BTNOK");
	this.btnOK.disabled = true;
	this.addListener(btnAdd, "click", this.doAdd, this);
	this.addListener(this.btnRemove, "click", this.doRemove, this);
	this.initSelectedObject();
	this.initUserTree(this.userContainer);
	var tabEl = domutils.findElementByClassName(this.element,"_tabPanel");
	this.pagecontrol = new  PagePanel(tabEl);
	this.roletab = this.pagecontrol.appendTab();
	this.roletab.setCaption("${Role}");
	this.grouptab = this.pagecontrol.appendTab();
	this.grouptab.setCaption("${User}/${Usergroup}");	
	this.roletab.appendItem(this.roleContainer);
	this.grouptab.appendItem(this.userContainer);
	this.pagecontrol.onTabActive.subscribe(this.doActive, this);
	this.expressionContainer = domutils.findElementByClassName(this.element,"_list_express_div");
	this.initFunctionTree(this.expressionContainer);
	this.expressiontab = this.pagecontrol.appendTab();
	this.expressiontab.setCaption("${Custom2}");
	this.expressiontab.appendItem(this.expressionContainer);
	if (data && data.showExpress == false) {
		var children = this.expressiontab.button.children;
		for (var i = 0; i < children.length; i++) {
			children[i].style.display = "none";
		}
	} else {
		var expressDiv = document.createElement("div");
		expressDiv.style.height = "100%";
		expressDiv.style.width = "100%";
		expressDiv.style.display = "none";
		expressDiv.id = "expressDiv";
		this.expressionEditor = new ExpressionEditor(expressDiv, "DS.SYSTEM知识库", '<${Workflow.common.s}>');
		this.expressionEditor.render();
		this.selectedUserEl.appendChild(expressDiv);
	}
	if (obj && obj.assignee && obj.assignee.length > 0&& obj.assignee[0].type == "CUSTOM") {
		this.pagecontrol.tabs[2].setActive();
		this.expressionEditor.setTextValue(obj.assignee[0].id);
	} else {
		this.pagecontrol.tabs[0].setActive();
	}
};

SetFlowResourcePermissionDialog.prototype.doActive = function(dialog) {
	var activeTab = this.pagecontrol.getActiveTab();
	if (activeTab && activeTab.items.length > 0) {
		var activeId = activeTab.items[0].id;
		var btnAdd = domutils.findElementByClassName(this.element,"_btnAdd");
		var isExpressActive = activeId == "list_express_div";
		if (isExpressActive) {
			this.btnRemove.style.display = "none";
			btnAdd.style.display = "none";
			this.btnOK.disabled = false;
		} else {
			this.btnRemove.style.display = "";
			btnAdd.style.display = "";
			var items = this.selectedListView.getDataItems();
			this.btnOK.disabled = items.length > 0 ? false : true;
		}
		var e1Children = this.selectedUserEl.children;
		for (var i = 0; i < e1Children.length; i++) {
			if (e1Children[i].id && e1Children[i].id == "expressDiv") {
				e1Children[i].style.display = isExpressActive ? "" : "none";
			} else {
				e1Children[i].style.display = isExpressActive ? "none" : "";
			}
		}
	}
}

SetFlowResourcePermissionDialog.prototype.doOK = function(){
	var activeTab = this.pagecontrol.getActiveTab();
	var items = [];
	if (activeTab.items[0].id == "list_express_div") {
		var value = this.expressionEditor.getTextValue();
		if (!value || value == "" || value.toLocaleLowerCase().indexOf("select") < 0) {
			alert("${expressError}");   
			return;
		}
		var expressObject = {
			alias : "${Custom2}",
			type : "CUSTOM",
			id : value,
		};
		items.push(expressObject);
	} else {
		items = this.selectedListView.getDataItems();
	}

	this.close(items);	
};