var TreeNode = jsloader.resolve("freequery.tree.TreeNode");
var util = jsloader.resolve("freequery.common.util");
var TransferDataNode = jsloader.resolve("freequery.common.TransferDataNode");
var modalWindow = null;

var FunctionTreeNode = function(text, level, mayHasChild, parent, id) {
	FunctionTreeNode.superclass.constructor.call(this, text, level, mayHasChild, parent, id);
	this.fakeSpan = document.createElement("SPAN");
	this.fakeSpan.style.visibility = "hidden";
	this.textSpan.appendChild(this.fakeSpan);
	this.fakeSpan.appendChild(document.createTextNode(' '));	
};

lang.extend(FunctionTreeNode, TreeNode);

FunctionTreeNode.prototype.addFunctionTreeNode = function(nodeInfo, type) {
	if(nodeInfo.alias == null || nodeInfo.alias.length == 0){
		nodeInfo.alias = nodeInfo.name;
	}
	var node = this.addChild(nodeInfo.alias, type == "FUNCTIONS" || type == "SYSTEM_FUNCTIONS", nodeInfo.id);
	node.setNodeInfo(nodeInfo, type);
	return node;
};

FunctionTreeNode.prototype.setNodeInfo = function(nodeInfo, type) {
	this.catalog = nodeInfo;
	this._id = nodeInfo.id;
	this._name = nodeInfo.name;
	this._alias = nodeInfo.alias;
	this._desc = nodeInfo.desc;
	this._type = nodeInfo.type;
	this._hiddenInBrowse = !!nodeInfo.hiddenInBrowse;
	this._customImage = nodeInfo.customImage;
	this._customImageId = nodeInfo.customImageId;
	this._customImageName = nodeInfo.customImageName;
	this._customMobileImage = nodeInfo.customMobileImage;
	this._hasChild = nodeInfo.hasChild;
	this._extended = nodeInfo.extended;
	this._detectChild = nodeInfo.detectChild;
	this._showOnPC = nodeInfo.showOnPC;
	this._showOnPad = nodeInfo.showOnPad;
	this._showOnPhone = nodeInfo.showOnPhone;
	this._hidetools = nodeInfo.hidetools;
	this.id = nodeInfo.id;
	this.alias = nodeInfo.alias;
	this.type = type;
	this.desc = nodeInfo.desc;
	if(this.desc && this.desc != this.alias)
		this.textSpan.title = this.desc;
	var suffix = ".png";
	this.setIcon("img/catalogtree/" + this.type + suffix);
		
	var title = "${Name}${Colon}" + (this.longText || this.text);
	if (this._desc && (this._desc != this.text))
		title += "\n${Description}${Colon}" + this._desc;
	this.textSpan.title = title;	
};

FunctionTreeNode.prototype.remoteCallback = function(result) {
	if (result.succeeded) {
		for (var i = 0; i < result.result.length; i++) {
			if (this.tree.searchCondition) {
				if (this.tree.searchNodeIdArray[result.result[i].id]) {
					this.addFunctionTreeNode(result.result[i], result.result[i].type);
				}
			} else if (!this.tree.procedureFilter
					|| !this.tree.procedureFilter(result.result[i])) {
				this.addFunctionTreeNode(result.result[i], result.result[i].type);
			}
		}
	} else {
		if(!modalWindow)
			modalWindow = jsloader.resolve("freequery.common.modalWindow");
		modalWindow.showServerError(result);
	}
	this.isLoading = false;
	this.initExpanderImg();
	this.tree.onNodeInitComplete.fire(this.tree, this);
};

FunctionTreeNode.prototype.initChildren = function() {
	this.isLoading = true;
	this.removeAllChildren();
	if(this.tree.dynamicLoad) {
		util.remoteInvoke("CatalogService", "getChildElements", [this._id], this.remoteCallback, this);
	} else {
		var result = util.remoteInvoke("CatalogService", "getChildElements", [this._id]);
		this.remoteCallback(result);
		this.isLoading = false;
		this.initExpanderImg();
	}
	this._isInitChild = true;
};

FunctionTreeNode.prototype.refreshChildren = function(increment) {
	if(increment) {
		
	} else
		this.initChildren();
};

FunctionTreeNode.prototype.createTransferData = function() {
	var transferNode = new TransferDataNode();
	
	transferNode.id = this._id;
	transferNode.type = this._type;
	transferNode.name = this._name;
	transferNode.label = (this._alias == null || this._alias == "") ? this._name : this._alias;
	transferNode.alias = this._alias;
	transferNode.desc = this._desc;
	
	if(this._type == "BUSINESSATTRIBUTE"){
		transferNode.expressionText = this._expression;
		transferNode.dataType = this._datatype;
		transferNode.dataFormat = this._dataformat;
		transferNode.condition = this._condition;
	}
	
	util.transferData().srcElement = this.tree;
	util.transferData().srcType = "themenode";
	util.transferData().dataNodes = [transferNode];

	return true;
};