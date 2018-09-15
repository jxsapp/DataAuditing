var CatalogTree = jsloader.resolve("freequery.tree.CatalogTree");
var util = jsloader.resolve("freequery.common.util");

var SpreadSheetTree = function(parent){
	SpreadSheetTree.superclass.constructor.call(this, parent);
	this.rootNodeIsInit = false; //初始化树形节点根节点
	this.denyPopupMenu = true;
}

lang.extend(SpreadSheetTree, CatalogTree);

SpreadSheetTree.prototype.render = function(id) {
	this.filterTypes = [ "DEFAULT_TREENODE", "SPREADSHEET_REPORT" ];
	this.rootNode.removeAllChildren();
	var ret = util.remoteInvokeEx("CatalogService", "getChildElements", [""]);
	if(ret) {
		for(var i = 0; i < ret.result.length; i++) {
			if (this.filterTypes.indexOf(ret.result[i].type) > -1) {
				this.rootNode.addCatalogTreeNode(ret.result[i]);
			}
		}
	}
}



