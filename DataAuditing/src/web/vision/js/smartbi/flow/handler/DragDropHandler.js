var util = jsloader.resolve('freequery.common.util');

var DragDropHandler = function(catalogTree) {
	this.catalogTree = catalogTree;
};

DragDropHandler.prototype.destroy = function() {
};

DragDropHandler.prototype.canDrag = function(node) {
	var dataNodes = util.transferData().dataNodes;
	if (!dataNodes || dataNodes.length < 1) {
		return;
	}
	var firstSrcNode = dataNodes[0];
	var nodeType = firstSrcNode.type;
	if ('WORKFLOW' != nodeType) {
		return;
	}
	var oldParentId = firstSrcNode.parentID || firstSrcNode.parentId;
	var oldParentType = firstSrcNode.type;
	if (oldParentId) {
		var ret = util.remoteInvoke('CatalogService', 'getCatalogElementById', [ oldParentId ]);
		if (ret && ret.succeeded) {
			oldParentType = ret.result.type;
		}
	}
	var newParentType = node._type;
	if (oldParentType != newParentType) { // “公有目录”与“私有目录”之间不能相互拖动
		return;
	}
	return (newParentType == 'DEFAULT_TREENODE' || newParentType == 'SELF_TREENODE');
};