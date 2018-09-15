var BaseDialogEx = jsloader.resolve("freequery.dialog.BaseDialogEx");
var util = jsloader.resolve("freequery.common.util");
var domutils = jsloader.resolve("freequery.lang.domutils");
var CatalogTree = jsloader.resolve("freequery.tree.CatalogTree");
var compatutil = jsloader.resolve("freequery.lang.compatutil");
var CatalogTreeSearchBar = jsloader.resolve("freequery.tree.CatalogTreeSearchBar");

var SelectFlowDialog = function() {
	SelectFlowDialog.superclass.constructor.call(this);
};
lang.extend(SelectFlowDialog, BaseDialogEx);

SelectFlowDialog.prototype.destroy = function() {
	if (this.resTree) {
		this.resTree.destroy();
	}
	SelectFlowDialog.superclass.destroy.call(this);
}

SelectFlowDialog.prototype.init = function(parent, data, fn, obj) {
	SelectFlowDialog.superclass.init.call(this, parent, data, fn, obj);
	this.data = data;

	this.parent = parent;
	this.element = document.createElement("div");
	this.element.style.height = "100%";
	this.dialogBody.appendChild(this.element);
	this.dialogBody.style.border = 'none';
	this.dialogBody.style.backgroundColor = 'transparent';
	var template = domutils.doGet("js/smartbi/flow/dialog/SelectFlowDialog.template");
	this.element.innerHTML = template;

	var treeDiv = domutils.findElementByClassName(this.element, "_treeDiv");
	compatutil.fixFirefoxScroll(treeDiv);
	this.initTree(treeDiv);
};

SelectFlowDialog.prototype.initTree = function(container) {
	this.resTree = new CatalogTree(container);
	this.resTree.dragEnabled = false;
	this.resTree.denyPopupMenu = true;
	this.resTree.filterTypes = [ "DEFAULT_TREENODE", "WORKFLOW", "SELF_TREENODE" ];
	this.resTree.onDblClickNode.subscribe(this.doOnSelectNode, this, true);
	this.resTree.onSelectNode.subscribe(this.doOnSelectNode, this, true);
	this.resTree.setCheckBoxType(0);
	this.resTree.render("DEFAULT_TREENODE");
	if (this.resTree.rootNode.firstChild)
		this.resTree.rootNode.firstChild.setExpanded(true);
	if (this.resTree.popupMenu) {
		this.resTree.popupMenu.destroy();
		this.resTree.popupMenu = null;
	}
	// 搜索条
	var searchBanner = domutils.findElementByClassName(this.element, "_searchTd");
	this.resTree.needSearch = true;
	this.searchbar = new CatalogTreeSearchBar(this.resTree, searchBanner);
	this.searchbar.onSearch.subscribe(this.setOKButtonDisabled, this);
	this.searchbar.show();
};

SelectFlowDialog.prototype.doOnSelectNode = function(tree, newNode, oldNode) {
	if (newNode && newNode._type == "WORKFLOW") {
		this.selectedNode = newNode;
		this.setButtonEnable("BTNOK", true);
	} else {
		this.selectedNode = null;
		this.setButtonEnable("BTNOK", false);
	}
};

SelectFlowDialog.prototype.doClose = function() {
	this.close(false);
};

SelectFlowDialog.prototype.doOK = function() {
	this.close(true, this.selectedNode);
};