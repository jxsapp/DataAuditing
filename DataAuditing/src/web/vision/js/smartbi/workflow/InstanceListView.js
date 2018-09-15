var eventutil = jsloader.resolve("freequery.lang.eventutil");
var domutils = jsloader.resolve("freequery.lang.domutils");
var compatutil = jsloader.resolve("freequery.lang.compatutil");
var ParameterPanel = jsloader.resolve("freequery.query.ParameterPanel");
if (typeof jQuery == 'undefined' || !jQuery) {
	jsloader.resolve('thirdparty.jquery.jquery', true);
}
if (jQuery && jQuery.fn && !jQuery.fn.select2) {
	jsloader.resolve('thirdparty.select2.select2', true);
	jsloader.resolve('thirdparty.select2.i18n.zh-CN', true);
	jsloader.resolve('thirdparty.select2.i18n.zh-TW', true);
}
var InstanceListView = function(container, parent, onlyMine) {
	this.onlyMine = onlyMine;
	this.parent = parent;
	this.contanier = container;
	this.init(container, __url);
	compatutil.fixFirefoxScroll(this.elemTdiv);
	this.whiteRowClassName = 'listView-talbe-row-white';
	this.grayRowClassName = 'listView-talbe-row-gray';
		this.headHtml = this.elemTdiv.innerHTML.replace(/<\/tbody>\s*<\/table>/gi, function(txt) {
		return '';
	});	
	this.tailHtml = '</tbody></table>';
	
	this.initCombox();
	
	
};

lang.extend(InstanceListView, "bof.usermanager.AbstractListView");


InstanceListView.prototype.buildParamPanel = function(sid){
	rets = util.remoteInvokeEx('SpreadsheetReportModule', 'openQueryInPage', [sid, null]);
	if(!rets||!rets.result[0])
		return;
	var	ret = rets.result[0];
	this.paramLayout = ret.settings.paramsLayout;
	this.params = ret.outputParameters;
	this.paramPanelId = ret.parameterPanelClientId;
	
	if(this.paramPanelObj){
		this.paramPanelObj.destroy();
	}
	this.paramPanelObj = new ParameterPanel(this.elemParamPanal, this.parent, lang.parseJSON(this.paramLayout));
	this.paramPanelObj.clientId = this.paramPanelId;
	for ( var i = 0, len = this.params.length; i < len; i++) {
		var param = this.params[i];
			param.manual = true;
		this.paramPanelObj.addParam(param);
	}
	this.adjustParamPanelObj();
}

InstanceListView.prototype.adjustParamPanelObj = function() {
	if (!this.paramPanelObj) {
		return;
	}
	var paramObjList = this.paramPanelObj.paramObjList;
	for ( var i = 0; i < paramObjList.length; i++) {
		var paramTag = paramObjList[i].paramTag;
		var param = paramTag.param;
		var paramId = param.id;
	
		for ( var j = 0; j < this.params.length; j++) {
			if (paramId == this.params[j].id) {					
					if (typeof this.params[j].currentValue != "undefined") {
						paramObjList[i].setSelectedItem("", "");
					} else {
						this.paramPanelObj.setParamCtrlValue(paramTag, "", "");
					}				
			}
		}
	}
};

InstanceListView.prototype.elemLogQuery_click_handler = function(e) {
	if(!this.paramPanelObj)
		return;
	var parmas = this.getWorkflowParams(this.spreadsheetId );
	this.fillList(parmas);
	
}


InstanceListView.prototype.initCombox = function() {
	//this.workflowSelect = new ComboBox(this.elemScheduleTypeDiv, document.body, 230);
	
	var data = [];
	var ret;
	if (this.onlyMine) {
		ret = util.remoteInvoke("RepositoryService", "getAllWorkflowInfoWidthAuthority", []);
	} else {
		ret = util.remoteInvokeEx('RepositoryService', 'getAllWorkflowInfo', []);
	}
	
	var list = ret.result;
	for(var i=0;i<list.length;i++){
		data.push({id:list[i].id,text:list[i].name});
	}	
	this.workflowSelect = jQuery(this.elemScheduleTypeDiv);	
	this.workflowSelect.select2({
		data:data,
		placeholder : '${PleaseSelect}',
		allowClear : true
	});
	this.workflowSelect.val(null).trigger("change");
	this.elemScheduleTypeDiv.value = "";
	var that = this;
	this.workflowSelect.on("select2:close",function(evt){
		that.doParamChange(evt);
	});	
}

InstanceListView.prototype.doParamChange = function(evt){
	var dbSelectedId =  this.elemScheduleTypeDiv.value;
	if(dbSelectedId == ""){
		if(this.paramPanelObj){
			this.paramPanelObj.destroy();
		}
		return;
	}
		
	var ret = util.remoteInvokeEx('RepositoryService', 'getResourceDefineByWorkflowId', [dbSelectedId]);	
	if(!ret.result&&!ret.result.length>0)
		return;	
	this.spreadsheetId = ret.result[0].spreadsheetId;
	this.buildParamPanel(ret.result[0].spreadsheetId);
}

InstanceListView.prototype.doParamComboEditChange = function(obj, selectedId, dbSelectedId,selectedValue, dropdownBoxSelectedValue){
	//alert(dropdownBoxSelectedValue);
	var ret = util.remoteInvokeEx('RepositoryService', 'getResourceDefineByWorkflowId', [dbSelectedId]);	
	if(!ret.result&&!ret.result.length>0)
		return;	
	this.spreadsheetId = ret.result[0].spreadsheetId;
	this.buildParamPanel(ret.result[0].spreadsheetId);
}

InstanceListView.prototype.getItemList = function(parmas) {
	var itemList = [];
	var list = util.remoteInvoke("RuntimeService", "findInsByWorkflowInfoAndParam", [parmas.res.workflowId, 
	               parmas.mechanismParmas, parmas.otherParmas ]);
	if(list.result){
		 for ( var i = 0; i < list.result.length; i++){
 	           var item = new Object();
 	           item = list.result[i];
 	  	       itemList.push(item);
 	     }
	}	
	this.itemList = itemList;
	return itemList;
};

InstanceListView.prototype.fillList = function(parmas) {
	var itemList = this.getItemList(parmas);
	var len = itemList && itemList.length || 0;
	var buff = [ this.headHtml ];
	for ( var i = 0; i < len; i++) {
		buff.push('<tr class="');
		var clz = (i % 2 == 0) ? this.grayRowClassName : this.whiteRowClassName;
		buff.push(clz);
		buff.push('">');

		buff.push('<td style="height:26px;">');
		buff.push(itemList[i].instanceName);
		buff.push('</td>');


		buff.push('<td>');
		buff.push(itemList[i].spreadsheetName);
		buff.push('</td>');
		
		buff.push('<td style="height:26px;">');
		if(itemList[i].activity == "childcomplete"){
			buff.push("子流程结束");
		}else{
			buff.push(itemList[i].activity);
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
		
	
		
		buff.push('<td style="height:26px;">');
		if(itemList[i].instanceState == "running"){
			buff.push("${Running}");
		}else{
			buff.push("${InstanceIsEnd}");
		}	
		buff.push('</td>');
		
		buff.push('<td style="height:26px;">');	
		buff.push(itemList[i].instanceCreateDate);		
		buff.push('</td>');
		buff.push('</tr>');
	}
	buff.push(this.tailHtml);

	this.elemTdiv.innerHTML = buff.join('');

};

InstanceListView.prototype.getWorkflowParams = function(reportResId) {
	var ret = util.remoteInvoke("RepositoryService", "getResourceDefineBySpreadsheetId", [ reportResId ]);
	var resDefine = ret.result;
	var sparmas = this.params;
	var mechanismParamNames = resDefine.mechanismParameter.split(","); // 字符分割
	var otherParamNames = resDefine.otherParameters.split(","); // 字符分割
	var mechanismParmas = new Array();
	var otherParmas = new Array();
	for ( var j = 0; j < sparmas.length; j++) {
		for ( var i = 0; i < mechanismParamNames.length; i++) {
			if (sparmas[j].name == mechanismParamNames[i]) {
				if(sparmas[j].value){
					mechanismParmas.push({
						name : sparmas[j].name,
						value : sparmas[j].value,
						displayValue : sparmas[j].displayValue
					});
				}		
			}
		}
		for ( var i = 0; i < otherParamNames.length; i++) {
			if (sparmas[j].name == otherParamNames[i]) {
				if(sparmas[j].value){
					otherParmas.push({
						name : sparmas[j].name,
						value : sparmas[j].value,
						displayValue : sparmas[j].displayValue
					});
				}
				
			}
		}
	}

	var mechanismParmasStr = JSON.stringify(mechanismParmas);
	var otherParmasStr = JSON.stringify(otherParmas);
	return {
		res : resDefine,
		mechanismParmas : mechanismParmasStr,
		otherParmas : otherParmasStr
	};
}

InstanceListView.prototype.destroy = function() {
	InstanceListView.superclass.destroy.call(this);
};

InstanceListView.prototype.refresh = function(ev) {
	this.fillList();
};

