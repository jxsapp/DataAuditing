/*
 * FileName  : SetResourcePermissionDialog.js
 * Copyright : Copyright (C) 2002-2007 
 * Creator   : Lvliangzhong (lvliangzhong)
 * History   : 
 * 1. Created this Unit. Lv Liangzhong 2007-02-28 
*/
var BaseDialogEx = jsloader.resolve("freequery.dialog.BaseDialogEx");
var util = jsloader.resolve("freequery.common.util");
var CustomEvent = jsloader.resolve("freequery.lang.CustomEvent");
var domutils = jsloader.resolve("freequery.lang.domutils");
var modalWindow = jsloader.resolve("freequery.common.modalWindow");
var TabPanel = jsloader.resolve("freequery.control.TabPanel");
var PagePanel = jsloader.resolve("freequery.control.PagePanel");
var CatalogTree = jsloader.resolve("freequery.tree.CatalogTree");
var SpreadSheetTree = jsloader.resolve("smartbi.flow.dialog.SpreadSheetTree");
var SpreadSheetResourceNodeOrder = jsloader.resolve("smartbi.flow.dialog.SpreadSheetResourceNodeOrder");
var compatutil = jsloader.resolve("freequery.lang.compatutil");
var CatalogTreeSearchBar;

SelectSpreadSheetDialog = function(){
	SelectSpreadSheetDialog.superclass.constructor.call(this);	
	this.parentEl = null;
	this.listView = null;
	this.selectedNode = null;
	this.selectedTrs = null;
};
lang.extend(SelectSpreadSheetDialog,BaseDialogEx);



SelectSpreadSheetDialog.prototype.initSpreadSheetTree = function(container){
	
	this.SpreadSheetTree = new SpreadSheetTree(container);
	this.SpreadSheetTree.dragEnabled = false;
	this.SpreadSheetTree.denyPopupMenu = true;
	this.SpreadSheetTree.onDblClickNode.subscribe(this.doTreeDblClick,this,true);
	this.SpreadSheetTree.onSelectNode.subscribe(this.doOnSelectNode,this,true);
	this.SpreadSheetTree.setCheckBoxType(0);
	this.SpreadSheetTree.render("DEFAULT_TREENODE");
	if (this.SpreadSheetTree.rootNode.firstChild)
		this.SpreadSheetTree.rootNode.firstChild.setExpanded(true);
	if(this.SpreadSheetTree.popupMenu){
		this.SpreadSheetTree.popupMenu.destroy();
		this.SpreadSheetTree.popupMenu = null;
	}
	//搜索条
	var searchBanner = domutils.findElementByClassName(this.element,"_searchTd");
	this.SpreadSheetTree.needSearch = true;
	if(!CatalogTreeSearchBar){
		CatalogTreeSearchBar = jsloader.resolve("freequery.tree.CatalogTreeSearchBar");
	}
	this.searchbar = new CatalogTreeSearchBar(this.SpreadSheetTree,searchBanner);
	//this.searchbar.onSearch.subscribe(this.setOKButtonDisabled, this);
	this.searchbar.show();

};

SelectSpreadSheetDialog.prototype.doOnSelectNode = function(tree,newNode,oldNode){
	if (newNode)
		this.selectedNode = newNode;
};

SelectSpreadSheetDialog.prototype.doOnRowDblClick = function(tr){
	this.orderPanel.grid.removeTr(tr);
};

SelectSpreadSheetDialog.prototype.doOnSelectRow = function(trList){
	if (trList)
		this.selectedTrs =trList;
};

SelectSpreadSheetDialog.prototype.doTreeDblClick = function(tree, node){
	this.doAdd();
};

SelectSpreadSheetDialog.prototype.init = function(parent,data,fn,obj){
	SelectSpreadSheetDialog.superclass.init.call(this,parent,data,fn,obj);
	this.spreadSheetNodes = data;
	
	this.parent = parent;
	this.element = document.createElement("div");
	this.element.style.height = "100%";
	this.dialogBody.appendChild(this.element);
	this.dialogBody.style.border = 'none';
	this.dialogBody.style.backgroundColor = 'transparent';
	var template = domutils.doGet("js/smartbi/flow/dialog/SelectSpreadSheetDialog.template");
	this.element.innerHTML = template;
	
	this.spreadSheetContainer = domutils.findElementByClassName(this.element,"_spreadSheettree_div");
	this.selectedUserEl = domutils.findElementByClassName(this.element,"_selected_div");
	compatutil.fixFirefoxScroll(this.spreadSheetContainer);
    
    this.initSpreadSheetTree(this.spreadSheetContainer);
    this.orderPanel = new SpreadSheetResourceNodeOrder(this.selectedUserEl);
    
    if (this.spreadSheetNodes && this.spreadSheetNodes.length!=0 )
    {
  		for (var i=0;i<this.spreadSheetNodes.length;i++)
		{
			this.spreadSheetNodes[i].type="SPREADSHEET_REPORT";
		}
    }	
    this.orderPanel.init(this.spreadSheetNodes);
    this.orderPanel.grid.onRowDblClick.subscribe(this.doOnRowDblClick,this,true);
	this.orderPanel.grid.onSelectedRowChanged.subscribe(this.doOnSelectRow,this,true);
	
	this.btnAdd = domutils.findElementByClassName(this.element,"_btnAdd");
	this.btnRemove = domutils.findElementByClassName(this.element,"_btnRemove");

	this.addListener(this.btnAdd, "click", this.doAdd, this);
	this.addListener(this.btnRemove, "click", this.doRemove, this);


	var tabEl = domutils.findElementByClassName(this.element,"_tabPanel");
	this.pagecontrol = new  PagePanel(tabEl);
	this.spreadSheettab = this.pagecontrol.appendTab();
	this.spreadSheettab.setCaption("${SpreadSheet}");
	this.spreadSheettab.appendItem(this.spreadSheetContainer.parentNode.parentNode);
	this.pagecontrol.tabs[0].setActive();
};


SelectSpreadSheetDialog.prototype.doAdd = function(){
	var node = this.selectedNode;
	if (node && node._type == "SPREADSHEET_REPORT") {
		var nodes = this.orderPanel.getOrderNodes();
		for (var i=0; i<nodes.length; i++) {
			if (nodes[i].id == node._id)
				return;
		}
		var fakeNode = {};
		fakeNode.id = node._id;
		fakeNode.name = node._name;
		fakeNode.alias = node._alias;
		fakeNode.type = node._type;
		this.orderPanel.addOneNode(fakeNode);
	}
};


SelectSpreadSheetDialog.prototype.doRemove = function(){
	if (this.selectedTrs && this.selectedTrs.length>0)
	{
	
		for (var i=0 ;i<this.selectedTrs.length;i++)
			this.orderPanel.grid.removeTr(this.selectedTrs[i]);
			
		this.selectedTrs = null;	
	}
};

SelectSpreadSheetDialog.prototype.doClose = function(){
	 var ret = this.spreadSheetNodes;
	 this.close(false, lang.toJSONString(ret));
};


SelectSpreadSheetDialog.prototype.doOK = function(){
	 var ret = this.orderPanel.getOrderNodes();
	 this.close(true, lang.toJSONString(ret));	
};