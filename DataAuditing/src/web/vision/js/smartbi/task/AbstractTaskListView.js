var eventutil = jsloader.resolve("freequery.lang.eventutil");
var domutils = jsloader.resolve("freequery.lang.domutils");
var compatutil = jsloader.resolve("freequery.lang.compatutil");

var AbstractTaskListView = function(container, action) {

	this.contanier = container;
	this.action = action;
	
};

lang.extend(AbstractTaskListView, "bof.usermanager.AbstractListView");

AbstractTaskListView.prototype.render = function(){
	
	//当前所在页
	this.currentPage = 1;
	//根据页面高度设置页默认行数
	if (document.body.clientHeight>700)
		this.defaultPageRows = 25;
	else
		this.defaultPageRows = 15;
		
	
	this.orangeRowClassName = 'listView-talbe-row-orange';
	this.whiteRowClassName = 'listView-talbe-row-white';
	this.grayRowClassName = 'listView-talbe-row-gray';
	this.headHtml = this.elemTdiv.innerHTML.replace(/<\/tbody>\s*<\/table>/gi, function(txt) {
		return '';
	});
	this.tailHtml = '</tbody></table>';	
	this.fillList();
	this.setButtonStyle();
	
	this.addListener(this.elemSearchName, "keypress", this.onSearchNameKeyPress, this);
	
	this.addListener(this.elemFirstPage,"click",this.doFirstPage,this);
	this.addListener(this.elemPrevPage,"click",this.doPrevPage,this);
	this.addListener(this.elemNextPage,"click",this.doNextPage,this);
	this.addListener(this.elemLastPage,"click",this.doLastPage,this);
	
	this.addListener(this.elemCurPage,"keydown",this.doCurPageKeyPress,this);
	this.addListener(this.elemCurPage,"keyup",this.doCurPageKeyUp,this);
	
	this.addListener(this.elemRowCount,"keydown",this.doCurPageKeyPress,this);
	this.addListener(this.elemRowCount,"keyup",this.doCurPageChange,this);
}

AbstractTaskListView.prototype.doFirstPage = function() {
	if ( !this.elemFirstPage.disabled ){
       this.setCurPageNum(1);
       this.fillList(true);
       this.setButtonStyle();
    }
};

AbstractTaskListView.prototype.doLastPage = function(ev) {
	if ( !this.elemLastPage.disabled ){
	   this.setCurPageNum(this.getPages());		
       this.fillList(true);
       this.setButtonStyle();
    }
};

AbstractTaskListView.prototype.doPrevPage = function(ev) {
	if ( !this.elemPrevPage.disabled ){
	   this.setCurPageNum(parseInt(this.currentPage) - 1);
       this.fillList(true);
       this.setButtonStyle();   
	}
};

AbstractTaskListView.prototype.doNextPage = function(ev) {
	if ( !this.elemNextPage.disabled ){
	   this.setCurPageNum(parseInt(this.currentPage) + 1);
       this.fillList(true);
       this.setButtonStyle(); 
    }
};

AbstractTaskListView.prototype.doCurPageKeyPress = function() {
	var ev = eventutil.getEvent();
	var code = ev.charCode || ev.keyCode;
		
	var BACKSPACE = 8;
	var DELETE = 46;	
	var LEFT = 37;
	var RIGHT = 39;
	var HOME = 36;
	var END = 35;
	var NUM_0 = 47;
	var NUM_9 = 58;
	var NUMLOCK_0 = 95;
	var NUMLOCK_9 = 106;
	if(
		NUM_0 < code && code < NUM_9 ||
		NUMLOCK_0 < code && code < NUMLOCK_9 ||
		BACKSPACE == code ||
		LEFT == code ||
		RIGHT == code ||
		HOME == code ||
		END == code ||
		DELETE == code
		) {
	} else {
		domutils.stopEvent(ev);
    } 
};

AbstractTaskListView.prototype.doCurPageKeyUp = function() {
	var ev = eventutil.getEvent();
	var code = ev.charCode;
	var pageNum = this.elemCurPage.value;
	 
	if ( this.getPages() == 0 )
		 pageNum = 1; 
	else if ( pageNum > this.getPages())
	     pageNum = this.getPages();

	if (pageNum == "")
		pageNum = 1; 	
	this.setCurPageNum(pageNum);
	//if ( code == 13 ) {
		this.fillList(true);
		this.setButtonStyle();
	//}
};

AbstractTaskListView.prototype.setCurPageNum = function(pageNum) {
	this.currentPage = pageNum;
	this.elemCurPage.value = pageNum;
};

AbstractTaskListView.prototype.getPages = function() {
	return this.pageCount;
};

AbstractTaskListView.prototype.setPages = function() {
	if (parseInt(this.elemAllRowCount.innerText)%this.getPageRows() == 0 ) 
	    this.pageCount = Math.floor(parseInt(this.elemAllRowCount.innerText)/this.getPageRows());
	
	else this.pageCount = Math.ceil(parseInt(this.elemAllRowCount.innerText)/this.getPageRows());
	
	this.elemPageCount.innerText = this.pageCount;
};

AbstractTaskListView.prototype.setPageRows = function(rows) {
	if ( rows > 0 )
		this.elemRowCount.value = rows;
	else
		this.elemRowCount.value = this.defaultPageRows;
};

AbstractTaskListView.prototype.doCurPageChange = function(e) {
	if (!this.elemRowCount.value ){
		return; 
	}   
	else if ( this.elemRowCount.value > 200 ) {
		alert("${Allowamaximumof}200");
		this.setPageRows(200);
	}
	else
		this.setPageRows(this.elemRowCount.value);
	
	//if(e.keyCode == 13){
		this.setPages();
		this.setCurPageNum(1);
       this.fillList();
       this.setButtonStyle(); 
	//}
};

AbstractTaskListView.prototype.getPageRows = function(defaultPageRows) {
	if(this.elemRowCount.value == "")
		this.setPageRows(defaultPageRows);
	return this.elemRowCount.value;
};

AbstractTaskListView.prototype.setButtonStyle = function() {
	if ( this.getPages()==0 ){
		this.elemNextPage.disabled = true;
		this.elemLastPage.disabled = true;
		this.elemFirstPage.disabled = true;
		this.elemPrevPage.disabled = true;
		return;
	}
		
	if (this.getPages()==1){
		this.elemFirstPage.disabled = true;
		this.elemPrevPage.disabled = true;
		this.elemNextPage.disabled = true;
		this.elemLastPage.disabled = true;
	}
		
	if (this.getPages() == this.currentPage){
		this.elemNextPage.disabled = true;
		this.elemLastPage.disabled = true;
	}else {
		this.elemNextPage.disabled = false;
		this.elemLastPage.disabled = false;
	}
	
	if (this.currentPage == 1){
		this.elemFirstPage.disabled = true;
		this.elemPrevPage.disabled = true;
	}else {
		this.elemFirstPage.disabled = false;
		this.elemPrevPage.disabled = false;
	}
};

//点击列表
AbstractTaskListView.prototype.elemTbody_click_handler = function(ev) {
	ev = eventutil.getEvent();
	this.currentItem = null;
	var tr = this.findActiveRow(ev);
	if (tr == null || tr.rowIndex == 0)
		return;
	var itemStr = tr.getAttribute("item");
	var item = lang.parseJSON(itemStr);
	this.currentItem = item;	   
};

//双击列表事件
AbstractTaskListView.prototype.elemTbody_dblclick_handler = function() {
	if ( this.currentItem ){
		var res = this.currentItem;
		var sid = res.spreadsheetId.split("_")[0];
		var sheetName = res.spreadsheetId.split("_")[1];
		var ret2 = util.remoteInvoke("CatalogService", "getCatalogElementById", [sid]);
	
		this.openRes(ret2.result,res,sheetName);	
	}
};

AbstractTaskListView.prototype.openRes = function(node,res,sName) {
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
	boflog.profile('SpreadsheetReportCommand.execute=>OPENWITHNOREFRESH');
	this.spreadsheetReport = command.openSpreadsheetReport(options,false);
	boflog.profile('SpreadsheetReportCommand.execute=>OPENWITHNOREFRESH');
	var mechanismParam = eval("("+res.mechanismParam+")");  
	var otherParams = eval("("+res.otherParams+")");
	
	var report = this.spreadsheetReport;
	if (report) {
		setTimeout(function(){
			var paramsInfo = [];
			for ( var i = 0; i < mechanismParam.length; i++) {
				var paramValue = {
					name : mechanismParam[i].name,
					value : mechanismParam[i].value,
					displayValue : mechanismParam[i].displayValue
				};
				paramsInfo.push(paramValue);
			}
			for ( var i = 0; i < otherParams.length; i++) {
				var paramValue = {
					name : otherParams[i].name,
					value : otherParams[i].value,
					displayValue : otherParams[i].displayValue
				};
				paramsInfo.push(paramValue);
			}
			report.setParamsInfo(paramsInfo);
			
			report.doRefresh();
		},500);
	}
	
	//
	manager.setCommand(tab, command);
};

AbstractTaskListView.prototype.destroy = function() {
	AbstractTaskListView.superclass.destroy.call(this);
};

AbstractTaskListView.prototype.refresh = function(ev) {
	this.fillList();
};

AbstractTaskListView.prototype.sortByCreateTime = function(itemList){
	var tItem;
	var len = itemList.length;
	for(var i=0;i<len;i++){
		for(var j= i + 1;j<len;j++){
			if(itemList[i].createTime<itemList[j].createTime){
				tItem = itemList[i];
				itemList[i] = itemList[j];
				itemList[j] = tItem;
			}
		}
	}
}
AbstractTaskListView.prototype.getItemList = function() {
	//debugger;
	var itemList = [];
	var taskList = util.remoteInvoke("TaskService", "taskByAssignee", [ "unfinished"]);
	if(taskList.result){
		 for ( var i = 0; i < taskList.result.length; i++){
 	           var task = new Object();
 	           task = taskList.result[i];
 	  	       itemList.push(task);
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
	this.sortByCreateTime(itemList);
	this.itemList = itemList;
	
	return itemList;
};

AbstractTaskListView.prototype.fillList = function() {
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
		if(itemList[i].taskState == "undone"){
			buff.push("未处理");
		}		
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
		buff.push(itemList[i].createTime || '&nbsp;');
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
		if (tr.setAttribute) tr.setAttribute("item", itemStr);
	}
	if (this.lastActiveRow) {
		this.lastActiveRow = null;
	}
};

AbstractTaskListView.prototype.elem_btnRefresh_click_handler = function(){
	this.refresh();
}


AbstractTaskListView.prototype.unbindListeners = function() {
	AbstractTaskListView.superclass.unbindListeners.call(this);
	this.removeListener(this.elemTbody, 'dblclick', this.elemTbody_dblclick_handler);
};


