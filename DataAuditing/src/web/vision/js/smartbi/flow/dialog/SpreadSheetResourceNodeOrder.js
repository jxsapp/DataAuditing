var ResourceNodeOrder = jsloader.resolve("freequery.tree.ResourceNodeOrder");
var Module = jsloader.resolve("freequery.widget.Module");
var util = jsloader.resolve("freequery.common.util");
var CustomEvent = jsloader.resolve("freequery.lang.CustomEvent");
var eventutil = jsloader.resolve("freequery.lang.eventutil");

var SpreadSheetResourceNodeOrder = function(parent) {
	//SpreadSheetResourceNodeOrder.superclass.constructor.call(parent);
	parent.innerHTML = "<TABLE width='100%' height='100%' border='0' cellSpacing='0' cellPadding='0'style='table-layout:fixed'>" +
			"<colgroup><col width='2px' /><col /><col width='16px' /></colgroup>" +
			"<tr><td colspan='3'>${Resourcename}${Colon}</td></tr>" + 
			"<TR height='100%'>" +
				"<TD style='width:2px'>&nbsp;" +
				"</TD>" +
				"<TD>" +
					"<div class='wrapper-outer'><DIV class='_containerDiv wrapper-inner' " +
						"style='border:1px solid #D8D8D8;height:94%;overflow:auto;background-color:#fff;'>" +
					"</DIV></div></TD>" +
			"</TR>" +
		"</TABLE>";
	this.container = domutils.findElementByClassName(parent, "_containerDiv");
	this.grid = new TreeNodeOrderGrid(this.container);
}

lang.extend(SpreadSheetResourceNodeOrder, ResourceNodeOrder);

SpreadSheetResourceNodeOrder.prototype.destroy = function() {
	this.grid.destroy();
}

//初始化
//节点信息最少应该包含 名称 别名 ID 类型
SpreadSheetResourceNodeOrder.prototype.init = function(nodes) {
	this.grid.init(nodes);
}

SpreadSheetResourceNodeOrder.prototype.getOrderNodes = function() {
	return this.grid.getOrder();
}


SpreadSheetResourceNodeOrder.prototype.isDirty = function() {
	return this.grid.isDirty;
}

//插入节点
SpreadSheetResourceNodeOrder.prototype.addOneNode = function(node) {
	this.grid.addOneNode(node);
}


//---------------------------------------------------
var TreeNodeOrderGrid = function(parent) {
	TreeNodeOrderGrid.superclass.constructor.call(this);
	this.mainTable = document.createElement("TABLE");
	this.mainTable.width = "100%";
	parent.appendChild(this.mainTable);
	this.tBody = document.createElement("TBODY");
	this.mainTable.appendChild(this.tBody);
	
	this.addListener(this.mainTable, "selectstart", this.doOnSelectStart, this);
	this.addListener(this.mainTable, "keydown", this.doOnKeyDown, this);
	this.addListener(this.mainTable,"click",this.doRowSelect,this); 
	this.addListener(this.mainTable,"dblclick",this.doRowDblClick,this); 
	this.onSelectedRowChanged = new CustomEvent("selectedRowChanged");
	this.onRowDblClick = new CustomEvent("rowDblClick");
};
lang.extend(TreeNodeOrderGrid, Module);

TreeNodeOrderGrid.prototype.doOnSelectStart = function(e) {
	domutils.preventDefault(e);
};

TreeNodeOrderGrid.prototype.doOnKeyDown = function(e) {
	domutils.preventDefault(e);
}

TreeNodeOrderGrid.prototype.init = function(nodes) {
	this.selectedRowList = new Array();	

	if(nodes) {
		for(var i = 0; i < nodes.length; i++) {
			var n = nodes[i];
			this.addOneNode(n);
		}
	}
	this.isDirty = false;
}

TreeNodeOrderGrid.prototype.addOneNode = function(n) {
	var tr = document.createElement("TR");
	this.tBody.appendChild(tr);
	var td = document.createElement("TD");
	tr.appendChild(td);
			
	tr._data = n;
	var alias = n.alias;
	if(!alias)
		alias = n.name;
				
	var img = document.createElement("IMG");
	switch(n.type) {
		default:
			img.src = "img/catalogtree/" + n.type + ".png";
	}
	td.appendChild(img);
	td.appendChild(document.createTextNode(alias));		
	this.isDirty = true;
}

TreeNodeOrderGrid.prototype.getOrder = function() {
	var retNodes = new Array();	
	for (var i = 0;i< this.mainTable.rows.length;i++)
	{
		retNodes.push(this.mainTable.rows[i]._data);
	}
	return retNodes;
}

TreeNodeOrderGrid.prototype.doRowDblClick = function() {
	var r = this.getClickRow();
	if ((r)&&(!r.cells[0].isHeader))
	{
		this.onRowDblClick.fire(r);
	}
}

TreeNodeOrderGrid.prototype.doRowSelect = function() {
	var ev = eventutil.getEvent();
	if (this.selectedRow && !ev.ctrlKey && !ev.shiftKey)
	{
		for(var i = 0; i < this.selectedRowList.length; i++)
			this.selectedRowList[i].className = "";
		this.selectedRowList.splice(0, this.selectedRowList.length);
	}
	var r = this.getClickRow();
	if ((r)&&(!r.cells[0].isHeader))
	{
		if(ev.ctrlKey) {

			if(!r.className) {
				r.className = "table-grid-row-select";
				var index = 0;
				for(; index < this.selectedRowList.length; index++) {
					if(this.selectedRowList[index].rowIndex > r.rowIndex) {
						break;
					}
				}
				this.selectedRowList.splice(index, 0, r);
			}
			this.selectedRow = r;
		} else if(ev.shiftKey) {
			for(var i = 0; i < this.selectedRowList.length; i++)
				this.selectedRowList[i].className = "";
			this.selectedRowList.splice(0, this.selectedRowList.length);

			if(this.selectedRow) {
				var from = r.rowIndex < this.selectedRow.rowIndex ? r.rowIndex : this.selectedRow.rowIndex;
				var to = r.rowIndex > this.selectedRow.rowIndex ? r.rowIndex : this.selectedRow.rowIndex;


				for(var i = from; i <= to; i++) {
					if(!this.mainTable.rows[i].className) {
						this.mainTable.rows[i].className = "table-grid-row-select";
						this.selectedRowList.push(this.mainTable.rows[i]);
					}
				}
			} else {
				r.className = "table-grid-row-select";
				this.selectedRowList.push(r);
				this.selectedRow = r;
			}
		} else {
			r.className = "table-grid-row-select";
			this.selectedRowList.splice(0, this.selectedRowList.length);
			this.selectedRowList.push(r);
			this.selectedRow = r;
		}
		
		this.onSelectedRowChanged.fire(this.selectedRowList);
	}
}


TreeNodeOrderGrid.prototype.removeTr = function(tr) {
	for (var i = 0; i < this.selectedRowList.length; i++) {
		if (this.selectedRowList[i] == tr) {
			this.selectedRowList.splice(i, 1);
			break;
		}
	}
	this.tBody.removeChild(tr);
}


TreeNodeOrderGrid.prototype.getClickCell = function () {
	var ev = eventutil.getEvent();
	var el = ev.target;
	while ((el!=null)&&(el.tagName && el.tagName.toLowerCase()!="td")) 
		el = el.parentNode;

	if (el)
	{
		var p = el;
		while ((p!=null)&&(p.tagName && p.tagName.toLowerCase()!="table")) 
			p = p.parentNode;

		if (p!=this.mainTable)
			return null;
	}
	return el;
}

TreeNodeOrderGrid.prototype.getClickRow = function () {
	var el = this.getClickCell();
	if (el)
		return el.parentNode;
	else
		return null;
}
