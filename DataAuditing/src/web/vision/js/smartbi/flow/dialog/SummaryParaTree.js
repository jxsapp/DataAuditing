var CatalogTree = jsloader.resolve("freequery.tree.CatalogTree");
var util = jsloader.resolve("freequery.common.util");

var SummaryParaTree = function(parent) {
	SummaryParaTree.superclass.constructor.call(this, parent);
	this.rootNodeIsInit = false; // 初始化树形节点根节点
	this.denyPopupMenu = true;
	this.spreadSheetId = new Array();
}

lang.extend(SummaryParaTree, CatalogTree);

SummaryParaTree.prototype.render = function(id,filterParams) {
	var sheetId = id || "";
	if (sheetId != "") {
		this.spreadSheetId = sheetId.split(",");
		if (this.spreadSheetId.length > 0 && this.spreadSheetId.length == 1) {
			var ret = util.remoteInvokeEx("RepositoryService", "getParametersBySpreadsheetId",
					[ this.spreadSheetId[0] ]);
			for ( var i = 0; i < ret.result.length; i++) {
				var node = ret.result[i];
				node.type = "PARAM";
				if(this.nodeInFilterParams(node,filterParams)){
					continue;
				}
				this.rootNode.addCatalogTreeNode(node);
			}
		} else {
			var params = new Array();
			var flag = true;
			for ( var i = 0; i < this.spreadSheetId.length; i++) {
				var ret = util.remoteInvokeEx("RepositoryService", "getParametersBySpreadsheetId",
						[ this.spreadSheetId[i] ]);
				var ret_result = ret.result || "";
				if (ret_result == "") {
					flag = false;
					break;
				}
				if(this.nodeInFilterParams(node,filterParams)){
					continue;
				}
				params.push(ret.result);
			}
			if (flag) {
				var results = new Array();
				for ( var i = 0; i < params.length; i++) {
					for ( var j = 0; j < params[i].length; j++) {
						results.push(params[i][j].name);
					}
				}
				var newRes = this.arrCheck(results);
				for ( var i = 0; i < newRes.length; i++) {
					if (newRes[i].count == params.length) {
						for ( var j = 0; j < params[0].length; j++) {
							if (newRes[i].name == params[0][j].name) {
								var node = params[0][j];
								node.type = "PARAM";
								this.rootNode.addCatalogTreeNode(node);
							}
						}
					}
				}
			}
		}
	}
}

/*
 * 找出数组元素出现的次数
 */
SummaryParaTree.prototype.arrCheck = function(arr) {
	var temp = "";
	var count = 0;
	var arrNew = new Array();
	for ( var i = 0; i < arr.length; i++) {
		if (arr[i] != -1) {
			temp = arr[i];
			for ( var j = 0; j < arr.length; j++) {
				if (temp == arr[j]) {
					count++;
					arr[j] = -1;
				}
			}
			var rest = {
				'name' : temp,
				'count' : count
			};
			arrNew.push(rest);
			count = 0;
		}
	}
	return arrNew;
}

SummaryParaTree.prototype.nodeInFilterParams = function(node,filterParams){
	if(!filterParams || !node){
		return false;
	}
	for(var i = 0;i<filterParams.length;i++){
		if(filterParams[i].name == node.name){
			return true;
		}
	}
	return false;
}
