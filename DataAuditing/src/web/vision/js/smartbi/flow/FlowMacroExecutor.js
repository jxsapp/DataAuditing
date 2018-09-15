var CommonMacroExecutor = jsloader.resolve("bof.macro.CommonMacroExecutor");

var FlowMacroExecutor = function(resource, targetName, eventName, args, script) {
	this.resource = resource;
	this.context = resource.queryNavigator;
	this.targetName = targetName;
	this.evenetType = eventName;
	this.args = args;
	this.script = script;
};
lang.extend(FlowMacroExecutor, CommonMacroExecutor);
