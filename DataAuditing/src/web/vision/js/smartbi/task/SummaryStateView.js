var UserEditAction = imports("bof.usermanager.actions.UserEditAction");
var GroupListAction = imports("bof.usermanager.actions.GroupListAction");
var UserService = imports("bof.usermanager.UserService");
var PopupMenu = imports("freequery.menu.PopupMenu");
var Configuration = imports("Configuration").getInstance();
var UserManagerUtil = jsloader.resolve("bof.usermanager.UserManagerUtil");
var eventutil = jsloader.resolve("freequery.lang.eventutil");
var domutils = jsloader.resolve("freequery.lang.domutils");
var compatutil = jsloader.resolve("freequery.lang.compatutil");

var SummaryStateView = function(container, parent) {
	this.list = parent.list;
	this.contanier = container;
	this.init(container, __url);
	compatutil.fixFirefoxScroll(this.elemTdiv);
	this.whiteRowClassName = 'listView-talbe-row-white';
	this.grayRowClassName = 'listView-talbe-row-gray';
	this.headHtml = this.elemTdiv.innerHTML.replace(/<\/tbody>\s*<\/table>/gi, function(txt) {
		return '';
	});
	this.tailHtml = '</tbody></table>';
	this.fillList();
};

lang.extend(SummaryStateView, "bof.usermanager.AbstractListView");

SummaryStateView.prototype.destroy = function() {
	SummaryStateView.superclass.destroy.call(this);
};

SummaryStateView.prototype.refresh = function(ev) {
	this.fillList();
};

SummaryStateView.prototype.getItemList = function() {
	var itemList = [];
	var list = this.list;
	if (list) {
		for ( var i = 0; i < list.length; i++) {
			var summary = new Object();
			summary = list[i];
			itemList.push(summary);
		}
	}
	this.itemList = itemList;
	return itemList;
};

SummaryStateView.prototype.fillList = function() {
	var itemList = this.getItemList();
	var len = itemList && itemList.length || 0;
	var buff = [ this.headHtml ];
	for ( var i = 0; i < len; i++) {
		buff.push('<tr class="');
		var clz = (i % 2 == 0) ? this.grayRowClassName : this.whiteRowClassName;
		buff.push(clz);
		buff.push('">');

		buff.push('<td style="height:26px; text-align: center;">');
		buff.push(itemList[i].name);
		buff.push('</td>');

		if (itemList[i].flg == "unfinished") {
			buff.push('<td style="height:26px; text-align: center; color:red;">');
			buff.push("${DataAuditUnFinish}");
		} else if (itemList[i].flg == "finish") {
			buff.push('<td style="height:26px; text-align: center;">');
			buff.push("${DataAuditComplete}");
		}

		buff.push('</td>');

		buff.push('</tr>');
	}
	buff.push(this.tailHtml);

	this.element.innerHTML = buff.join('');

};
