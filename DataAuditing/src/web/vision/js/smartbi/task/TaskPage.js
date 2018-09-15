var UserEditAction = imports("bof.usermanager.actions.UserEditAction");
var GroupListAction = imports("bof.usermanager.actions.GroupListAction");
var UserService = imports("bof.usermanager.UserService");
var PopupMenu = imports("freequery.menu.PopupMenu");
var Configuration = imports("Configuration").getInstance();
var PagePanel = jsloader.resolve("freequery.control.PagePanel");
var TaskListView = jsloader.resolve('smartbi.task.TaskListView');
var CompleteTaskListView = jsloader.resolve('smartbi.task.CompleteTaskListView');
var TaskList = jsloader.resolve('smartbi.task.TaskListView');
var WorkFlowList = jsloader.resolve('smartbi.workflow.WorkFlowListView');
var InstanceListView = jsloader.resolve('smartbi.workflow.InstanceListView');

var UserManagerUtil = jsloader.resolve("bof.usermanager.UserManagerUtil");
var eventutil = jsloader.resolve("freequery.lang.eventutil");
var domutils = jsloader.resolve("freequery.lang.domutils");
var compatutil = jsloader.resolve("freequery.lang.compatutil");

var TaskPage = function(container, action) {
	
	this.init(container, __url, false);
	
	this.elemPagePanel = domutils.getElementByClassName("_pagePanel",container);
	this.pagecontrol = new PagePanel(this.elemPagePanel);
	this.taskListViewTab = this.pagecontrol.appendTab();
	this.taskListViewTab.setCaption("${Backlog}");
	this.taskListViewContainerDiv = document.createElement("div");
	this.taskListViewContainerDiv.style.height = "100%";
	this.taskListViewContainerDiv.style.overflow = "hidden";
	this.taskListViewTab.appendItem(this.taskListViewContainerDiv);
	this.taskListView = new TaskListView(this.taskListViewContainerDiv,this);
	
	this.completeTaskTab = this.pagecontrol.appendTab();
	this.completeTaskTab.setCaption("${CompleteTask}");
	this.completeTaskContainerDiv = document.createElement("div");
	this.completeTaskContainerDiv.style.height = "100%";
	this.completeTaskContainerDiv.style.overflow = "hidden";
	this.completeTaskTab.appendItem(this.completeTaskContainerDiv);
	this.completeTaskListView = new CompleteTaskListView(this.completeTaskContainerDiv,this);
	
	this.workFlowListTab = this.pagecontrol.appendTab();
	this.workFlowListTab.setCaption("${BacklogWorkflow}");
	this.workFlowListContainerDiv = document.createElement("div");
	this.workFlowListContainerDiv.style.height = "100%";
	this.workFlowListContainerDiv.style.overflow = "hidden";
	this.workFlowListTab.appendItem(this.workFlowListContainerDiv);
	this.workFlowList = new WorkFlowList(this.workFlowListContainerDiv,this);
	
	this.InstanceListViewTab = this.pagecontrol.appendTab();
	this.InstanceListViewTab.setCaption("${MyMonitorWorkflow}");
	this.InstanceListViewContainerDiv = document.createElement("div");
	this.InstanceListViewContainerDiv.style.height = "100%";
	this.InstanceListViewContainerDiv.style.overflow = "hidden";
	this.InstanceListViewTab.appendItem(this.InstanceListViewContainerDiv);
	this.InstanceListView = new InstanceListView(this.InstanceListViewContainerDiv,this, true);
	this.pagecontrol.tabs[0].setActive();
};

lang.extend(TaskPage, "freequery.widget.Module2");

TaskPage.prototype.destroy = function() {
	TaskPage.superclass.destroy.call(this);
};
