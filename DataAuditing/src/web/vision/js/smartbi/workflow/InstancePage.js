var UserEditAction = imports("bof.usermanager.actions.UserEditAction");
var GroupListAction = imports("bof.usermanager.actions.GroupListAction");
var UserService = imports("bof.usermanager.UserService");
var PopupMenu = imports("freequery.menu.PopupMenu");
var Configuration = imports("Configuration").getInstance();
var PagePanel = jsloader.resolve("freequery.control.PagePanel");
var InstanceList = jsloader.resolve('smartbi.workflow.InstanceListView');

var UserManagerUtil = jsloader.resolve("bof.usermanager.UserManagerUtil");
var eventutil = jsloader.resolve("freequery.lang.eventutil");
var domutils = jsloader.resolve("freequery.lang.domutils");
var compatutil = jsloader.resolve("freequery.lang.compatutil");

var InstancePage = function(container, action) {
	this.init(container, __url, false);

	this.elemPagePanel = domutils.getElementByClassName("_pagePanel", container);
	this.pagecontrol = new PagePanel(this.elemPagePanel);
	this.instanceListViewTab = this.pagecontrol.appendTab();
	this.instanceListViewTab.setCaption("运行中流程");
	this.instanceListViewContainerDiv = document.createElement("div");
	this.instanceListViewContainerDiv.style.height = "100%";
	this.instanceListViewContainerDiv.style.overflow = "hidden";
	this.instanceListViewTab.appendItem(this.instanceListViewContainerDiv);
	this.instanceList = new InstanceList(this.instanceListViewContainerDiv, this);

	this.pagecontrol.tabs[0].setActive();

};

lang.extend(InstancePage, "freequery.widget.Module2");

InstancePage.prototype.destroy = function() {
	InstancePage.superclass.destroy.call(this);
};
