var BaseDialogEx = jsloader.resolve("freequery.dialog.BaseDialogEx");
var util = jsloader.resolve("freequery.common.util");
var CustomEvent = jsloader.resolve("freequery.lang.CustomEvent");
var domutils = jsloader.resolve("freequery.lang.domutils");
var modalWindow = jsloader.resolve("freequery.common.modalWindow");
var TabPanel = jsloader.resolve("freequery.control.TabPanel");
var PagePanel = jsloader.resolve("freequery.control.PagePanel");
var CatalogTree = jsloader.resolve("freequery.tree.CatalogTree");
var SummaryParaTree = jsloader.resolve("smartbi.flow.dialog.SummaryParaTree");
var SummaryParaResourceNodeOrder = jsloader.resolve("smartbi.flow.dialog.SummaryParaResourceNodeOrder");
var compatutil = jsloader.resolve("freequery.lang.compatutil");

SelectSummaryParaDialog = function() {
	SelectSummaryParaDialog.superclass.constructor.call(this);
	this.parentEl = null;
	this.listView = null;
	this.selectedNode = null;
	this.selectedTrs = null;
};
lang.extend(SelectSummaryParaDialog, BaseDialogEx);

SelectSummaryParaDialog.prototype.initSummaryParaTree = function(container, id) {

	this.SummaryParaTree = new SummaryParaTree(container);
	this.SummaryParaTree.dragEnabled = false;
	this.SummaryParaTree.denyPopupMenu = true;
	this.SummaryParaTree.onDblClickNode.subscribe(this.doTreeDblClick, this, true);
	this.SummaryParaTree.onSelectNode.subscribe(this.doOnSelectNode, this, true);
	this.SummaryParaTree.setCheckBoxType(0);
	this.SummaryParaTree.render(id,this.filterParams);
	if (this.SummaryParaTree.rootNode.firstChild)
		this.SummaryParaTree.rootNode.firstChild.setExpanded(true);
	if (this.SummaryParaTree.popupMenu) {
		this.SummaryParaTree.popupMenu.destroy();
		this.SummaryParaTree.popupMenu = null;
	}

};

SelectSummaryParaDialog.prototype.doOnSelectNode = function(tree, newNode, oldNode) {
	if (newNode)
		this.selectedNode = newNode;
};

SelectSummaryParaDialog.prototype.doOnRowDblClick = function(tr) {
	this.orderPanel.grid.removeTr(tr);
};

SelectSummaryParaDialog.prototype.doOnSelectRow = function(trList) {
	if (trList)
		this.selectedTrs = trList;
};

SelectSummaryParaDialog.prototype.doTreeDblClick = function(tree, node) {
	this.doAdd();
};

SelectSummaryParaDialog.prototype.init = function(parent, data, fn, obj) {
	SelectSummaryParaDialog.superclass.init.call(this, parent, data, fn, obj);
	this.summaryParaNodes = data.currentSelect;
	this.multiSelect = data.multiSelect;
	this.filterParams = data.filterParams;
	this.flowActionInfo = obj;
	this.spreadSheetId = this.flowActionInfo.elemSpreadSheetId.value;

	this.parent = parent;
	this.element = document.createElement("div");
	this.element.style.height = "100%";
	this.dialogBody.appendChild(this.element);
	this.dialogBody.style.border = 'none';
	this.dialogBody.style.backgroundColor = 'transparent';
	var template = domutils.doGet("js/smartbi/flow/dialog/SelectSummaryParaDialog.template");
	this.element.innerHTML = template;

	this.summaryParaContainer = domutils.findElementByClassName(this.element, "_summaryPara_div");
	this.selectedUserEl = domutils.findElementByClassName(this.element, "_selected_div");
	compatutil.fixFirefoxScroll(this.summaryParaContainer);

	this.initSummaryParaTree(this.summaryParaContainer, this.spreadSheetId);
	this.orderPanel = new SummaryParaResourceNodeOrder(this.selectedUserEl);

	if (this.summaryParaNodes && this.summaryParaNodes.length != 0) {
		for ( var i = 0; i < this.summaryParaNodes.length; i++) {
			this.summaryParaNodes[i].type = "PARAM";
		}
	}
	this.orderPanel.init(this.summaryParaNodes);
	this.orderPanel.grid.onRowDblClick.subscribe(this.doOnRowDblClick, this, true);
	this.orderPanel.grid.onSelectedRowChanged.subscribe(this.doOnSelectRow, this, true);

	this.btnAdd = domutils.findElementByClassName(this.element, "_btnAdd");
	this.btnRemove = domutils.findElementByClassName(this.element, "_btnRemove");

	this.addListener(this.btnAdd, "click", this.doAdd, this);
	this.addListener(this.btnRemove, "click", this.doRemove, this);

	var tabEl = domutils.findElementByClassName(this.element, "_tabPanel");
	this.pagecontrol = new PagePanel(tabEl);
	this.summaryParatab = this.pagecontrol.appendTab();
	switch (data.dialogType) {
		case "SummaryPara":
			this.summaryParatab.setCaption("${SummaryPara}");
			break;
		case "OtherPara":
			this.summaryParatab.setCaption("${OtherPara}");
			break;
		default:
			this.summaryParatab.setCaption("${SummaryPara}");
	}
	this.summaryParatab.appendItem(this.summaryParaContainer.parentNode.parentNode);
	this.pagecontrol.tabs[0].setActive();
};

SelectSummaryParaDialog.prototype.doAdd = function() {
	if (!this.multiSelect){
		this.orderPanel.clear();
	}
	var node = this.selectedNode;
	if (node && node._type == "PARAM") {
		var nodes = this.orderPanel.getOrderNodes();
		for ( var i = 0; i < nodes.length; i++) {
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

SelectSummaryParaDialog.prototype.doRemove = function() {
	if (this.selectedTrs && this.selectedTrs.length > 0) {
		for ( var i = 0; i < this.selectedTrs.length; i++)
			this.orderPanel.grid.removeTr(this.selectedTrs[i]);
		this.selectedTrs = null;
	}
};

SelectSummaryParaDialog.prototype.doClose = function() {
	var ret = this.summaryParaNodes;
	this.close(false, lang.toJSONString(ret));
};

SelectSummaryParaDialog.prototype.doOK = function() {
	var ret = this.orderPanel.getOrderNodes();
	this.close(true, lang.toJSONString(ret));
};