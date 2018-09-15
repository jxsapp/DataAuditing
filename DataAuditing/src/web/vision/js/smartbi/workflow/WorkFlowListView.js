var eventutil = jsloader.resolve("freequery.lang.eventutil");
var domutils = jsloader.resolve("freequery.lang.domutils");
var compatutil = jsloader.resolve("freequery.lang.compatutil");

var WorkFlowListView = function(container, action) {
	this.init(container, __url);
	compatutil.fixFirefoxScroll(this.elemTdiv);
	this.render();
	
};
lang.extend(WorkFlowListView, "smartbi.task.AbstractTaskListView");



//双击列表事件
WorkFlowListView.prototype.elemTbody_dblclick_handler = function() {
	if ( this.currentItem ){
		var ret2 = util.remoteInvoke("CatalogService", "getCatalogElementById", [this.currentItem.spreadsheetId]);
	
		this.openRes(ret2.result);	
	}
};

WorkFlowListView.prototype.openRes = function(node) {
	var action = 'OPEN';
	var manager = registry.get('CurrentManager');
	if(!node||!node.id){
		alert("${SpreadSheetNotExisit}!");
		return;
	}
	var tabId = [ node.id, action ];
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
			pageId : node && node.pageId
		};
	command.openSpreadsheetReport(options);
	manager.setCommand(tab, command);
};

WorkFlowListView.prototype.destroy = function() {
	WorkFlowListView.superclass.destroy.call(this);
};

WorkFlowListView.prototype.getItemList = function() {
	var itemList = [];
	var workflowList = util.remoteInvoke("RepositoryService", "getAllInfoByStartRoal", []);
	if(workflowList.result){
		 for ( var i = 0; i < workflowList.result.length; i++){
 	           var workflow = new Object();
 	           workflow = workflowList.result[i];
 	  	       itemList.push(workflow);
 	     }
	}
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

WorkFlowListView.prototype.fillList = function() {
	var itemList = this.getItemList();
	var len = itemList && itemList.length || 0;
	var buff = [ this.headHtml ];
	for (  var i = (this.currentPage - 1) * this.getPageRows(), j = 0;
	i < (this.currentPage) * this.getPageRows() && i < itemList.length; i++) {
		buff.push('<tr class="');
		var clz = (i % 2 == 0) ? this.grayRowClassName : this.whiteRowClassName;
		buff.push(clz);
		buff.push('">');

		buff.push('<td style="height:26px; text-align: center;">&nbsp;</td>');

		buff.push('<td>');
		buff.push(itemList[i].workflowName);
		buff.push('</td>');
		
		buff.push('<td>');
		buff.push(itemList[i].spreadsheetName);
		buff.push('</td>');

		buff.push('<td>');
		if(itemList[i].workflowType == "manual"){
			buff.push("${InstanceManual}");
		}else if(itemList[i].workflowType == "auto"){
			buff.push("${InstanceAuto}");
		}	
		buff.push('</td>');

		buff.push('<td>');
		buff.push(itemList[i].workflowDesc || '&nbsp;');
		buff.push('</td>');

		buff.push('<td>&nbsp;</td>');

		buff.push('</tr>');
	}
	buff.push(this.tailHtml);

	this.unbindListeners();
	this.elemTdiv.innerHTML = buff.join('');
	this.elemTbody = this.elemTdiv.getElementsByTagName('table')[0].tBodies[0];
	domutils.removeWhiteSpace(this.elemTbody);
	this.elemCode = this.elemTbody.childNodes[0].childNodes[1];
	this.bindListeners();

	var rows = this.elemTbody.childNodes;
	for ( var i = (this.currentPage - 1) * this.getPageRows(), j = 1;
	i < (this.currentPage) * this.getPageRows() && i < itemList.length; i++) {
		var item = itemList[i];
		var tr = rows[j++];
		var itemStr = lang.toJSONString(item);
		if (tr.setAttribute) tr.setAttribute("item", itemStr);
	}
	if (this.lastActiveRow) {
		this.lastActiveRow = null;
	}
};

WorkFlowListView.prototype.unbindListeners = function() {
	WorkFlowListView.superclass.unbindListeners.call(this);
	this.removeListener(this.elemTbody, 'dblclick', this.elemTbody_dblclick_handler);
};
