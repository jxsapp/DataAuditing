var eventutil = jsloader.resolve("freequery.lang.eventutil");
var domutils = jsloader.resolve("freequery.lang.domutils");
var compatutil = jsloader.resolve("freequery.lang.compatutil");

var CompleteTaskListView = function(container, action) {
	this.init(container, __url);
	compatutil.fixFirefoxScroll(this.elemTdiv);
	this.render();
	
};

lang.extend(CompleteTaskListView, "smartbi.task.AbstractTaskListView");


//双击列表事件
CompleteTaskListView.prototype.elemTbody_dblclick_handler = function() {
	if ( this.currentItem ){	
		var res = this.currentItem;	
		var sid = res.spreadsheetId.split("_")[0];
		var sheetName = res.spreadsheetId.split("_")[1];
		var ret2 = util.remoteInvoke("CatalogService", "getCatalogElementById", [sid]);
		this.openRes(ret2.result,res,sheetName);	
	}
};

CompleteTaskListView.prototype.openRes = function(node,res,sName) {
	//debugger;
	var action = 'OPEN';
	var manager = registry.get('CurrentManager');
	var tabId = [ node.id, action ];
	if(!node||!node.id){
		alert("${SpreadSheetNotExisit}!");
		return;
	}
	var tab = manager.createTab(tabId, node.alias);
	if (!tab) {
		return;
	}
	var commandFactory = jsloader.resolve('freequery.tree.superviseCommandFactory');
	var command = commandFactory.getCommand('SpreadsheetReportCommand');
	command.onClose.subscribe(function() {
		tab.doClose();
	}, this);
	var options = {
			queryId : node && node.id,
			sheetName:sName ,
			pageId : node && node.pageId
		};
	this.spreadsheetReport = command.openSpreadsheetReport(options,false);
	var mechanismParam = eval("("+res.mechanismParam+")");  
	var otherParams = eval("("+res.otherParams+")");
	var _that = this;
	setTimeout(function() {
			for(var i=0;i<mechanismParam.length;i++){
				if(_that.spreadsheetReport.paramPanelObj){
					_that.spreadsheetReport.setParamValueByName(mechanismParam[i].name, mechanismParam[i].value,  mechanismParam[i].displayValue,"");
				}
			}
			for(var i=0;i<otherParams.length;i++){
				if(_that.spreadsheetReport.paramPanelObj){
					_that.spreadsheetReport.setParamValueByName(otherParams[i].name, otherParams[i].value,  otherParams[i].displayValue,"");
				}
			}
			_that.spreadsheetReport.doRefresh();
		}, 500);
	
	//
	manager.setCommand(tab, command);
};

CompleteTaskListView.prototype.destroy = function() {

	CompleteTaskListView.superclass.destroy.call(this);
};

CompleteTaskListView.prototype.sortByOperateTime = function(itemList){
	var tItem;
	var len = itemList.length;
	for(var i=0;i<len;i++){
		for(var j= i + 1;j<len;j++){
			if(itemList[i].taskOperateTime<itemList[j].taskOperateTime){
				tItem = itemList[i];
				itemList[i] = itemList[j];
				itemList[j] = tItem;
			}
		}
	}
}
CompleteTaskListView.prototype.getItemList = function() {
	var itemList = [];
	var taskList = util.remoteInvoke("TaskService", "taskByAssignee", [ "complete"]);
	var taskList2 = util.remoteInvoke("TaskService", "taskByAssignee", [ "backToPre"]);
	var taskList3 = util.remoteInvoke("TaskService", "taskByAssignee", [ "backToStart"]);
	if(taskList.result){
		 for ( var i = 0; i < taskList.result.length; i++){
 	           var task = new Object();
 	           task = taskList.result[i];
 	  	       itemList.push(task);
 	     }
	}
	if(taskList2.result){
		 for ( var i = 0; i < taskList2.result.length; i++){
	           var task = new Object();
	           task = taskList2.result[i];
	  	       itemList.push(task);
	     }
	}
	if(taskList3.result){
		 for ( var i = 0; i < taskList3.result.length; i++){
	           var task = new Object();
	           task = taskList3.result[i];
	  	       itemList.push(task);
	     }
	}
	this.sortByOperateTime(itemList);
	this.pageCount = 0;
	
	this.elemPage.style.display = "";
	if(this.elemRowCount.value == "") {
		this.elemRowCount.value = this.defaultPageRows;
	}
	
	var itemLen = itemList.length;

	if (itemLen == 0)
		this.elemAllRowCount.innerText = 0;
	else
    	this.elemAllRowCount.innerText = itemLen;
	this.setPages();
	this.setButtonStyle();
	this.itemList = itemList;
	return itemList;
};

CompleteTaskListView.prototype.fillList = function() {
	var itemList = this.getItemList();
	var len = itemList && itemList.length || 0;
	var buff = [ this.headHtml ];
	for (  var i = (this.currentPage - 1) * this.getPageRows(), j = 0;
	i < (this.currentPage) * this.getPageRows() && i < itemList.length; i++) {
		buff.push('<tr class="');
		var clz = (i % 2 == 0) ? this.grayRowClassName : this.whiteRowClassName;
		buff.push(clz);
		buff.push('">');

		buff.push('<td>');
		buff.push(itemList[i].workflowName);
		buff.push('</td>');
		
		buff.push('<td>');
		buff.push(itemList[i].spreadsheetName);
		buff.push('</td>');

		buff.push('<td>');
		buff.push(itemList[i].taskName);
		buff.push('</td>');

		buff.push('<td>');
		buff.push('${DataAuditDone}');
		buff.push('</td>');
		
		if(itemList[i].mechanismParam){
			var mechanismParam = eval("("+itemList[i].mechanismParam+")");
			buff.push('<td>');
			for(var j=0;j<mechanismParam.length;j++){
				buff.push(mechanismParam[j].name+' : '+mechanismParam[j].displayValue+'<br>');
			}		
			buff.push('</td>');
		}else{			
			buff.push('<td>');	
			buff.push('</td>');
		}
		
		
		if(itemList[i].otherParams){
			var otherParams = eval("("+itemList[i].otherParams+")");
			buff.push('<td>');
			for(var j=0;j<otherParams.length;j++){
				buff.push(otherParams[j].name+' : '+otherParams[j].displayValue+'<br>');
			}		
			buff.push('</td>');	
		}else{		
			buff.push('<td>');
			buff.push('</td>');	
		}
		
		buff.push('<td>');
		buff.push(itemList[i].taskOperateTime || '&nbsp;');
		buff.push('</td>');

		buff.push('<td>&nbsp;</td>');

		buff.push('</tr>');
	}
	buff.push(this.tailHtml);

	this.unbindListeners();
	this.elemTdiv.innerHTML = buff.join('');
	this.elemTbody = this.elemTdiv.getElementsByTagName('table')[0].tBodies[0];
	domutils.removeWhiteSpace(this.elemTbody);
	//this.elemCode = this.elemTbody.childNodes[0].childNodes[1];
	this.bindListeners();

	var rows = this.elemTbody.childNodes;
	for ( var i = (this.currentPage - 1) * this.getPageRows(), j = 1;
	i < (this.currentPage) * this.getPageRows() && i < itemList.length; i++) {
		var item = itemList[i];
		var tr = rows[j++];
		var itemStr = lang.toJSONString(item);
		if (tr.setAttribute)
			tr.setAttribute("item", itemStr);
	}
	if (this.lastActiveRow) {
		this.lastActiveRow = null;
	}
};


CompleteTaskListView.prototype.unbindListeners = function() {
	CompleteTaskListView.superclass.unbindListeners.call(this);
	this.removeListener(this.elemTbody, 'dblclick', this.elemTbody_dblclick_handler);
};


