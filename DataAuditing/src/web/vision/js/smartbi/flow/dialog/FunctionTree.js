var TreeView = jsloader.resolve("freequery.tree.TreeView");
var FunctionTreeNode = jsloader.resolve("smartbi.flow.dialog.FunctionTreeNode");
var util = jsloader.resolve("freequery.common.util");
var commandFactory = null;
var CustomEvent = jsloader.resolve("freequery.lang.CustomEvent");
var modalWindow = jsloader.resolve("freequery.common.modalWindow");
var dialogFactory = null;

var FunctionTree = function(parent,parentObj) {
	FunctionTree.superclass.constructor.call(this, parent);
	this.dynamicLoad = true;
	this.onNodeInitComplete = new CustomEvent("nodeInitComplete", this);
	this.onDblClickNode = new CustomEvent("dblClick", this);
	this.parentObj = parentObj;
	this.needSearch = true;
};

lang.extend(FunctionTree, TreeView);

FunctionTree.prototype.createTreeNode = function(text, level, mayHasChild, parent, id) {
	return new FunctionTreeNode(text, level, mayHasChild, parent, id);
};

FunctionTree.prototype.selectDefaultNode = function() {
	//默认展开树
	this.selectNode(this.rootNode.firstChild, true);
	if ( this.rootNode.firstChild ) {
		var me = this;
		var timerId = setTimeout(function() {
			if (me.rootNode){
			   me.rootNode.firstChild.setExpanded(true);
			   clearTimeout(timerId);
			}	
		}, 5);
	}
};

FunctionTree.prototype.render = function(_needIni) {
	var node = {name:"${Functionlist}", id:"SYSTEM_FUNCTIONS"};
	this.rootNode.addFunctionTreeNode(node, "SYSTEM_FUNCTIONS");
};

FunctionTree.prototype.doOnSelectStart = function(e) {
	if(!this._initSelection)
		FunctionTree.superclass.doOnSelectStart.call(this, e);
};

FunctionTree.prototype.doOnSelectNode = function(tree,newNode,oldNode){
	if(this.selectedNode == null || this.selectedNode._id != newNode._id){
		
	}
};

FunctionTree.prototype.doOnNodeDblClick = function(tree, node) {
	this.onDblClickNode.fire(this,node);
};

FunctionTree.prototype.canDrag = function(node) {
	return true;
};
