var eventutil = jsloader.resolve("freequery.lang.eventutil");
var domutils = jsloader.resolve("freequery.lang.domutils");
var compatutil = jsloader.resolve("freequery.lang.compatutil");

var InstanceStateView = function(container, parent) {
	this.list = parent.list;
	this.contanier = container;
	this.init(container, __url);
	compatutil.fixFirefoxScroll(this.elemTdiv);
	
	this.drawActivitys();	
};

lang.extend(InstanceStateView, "bof.usermanager.AbstractListView");

InstanceStateView.prototype.drawActivitys = function() {
	var buff = [];
	if( this.list){
		var list  = this.list;
		for(var i=0;i < list.length; i++){
			if(buff.length > 0){			
				buff.push("<span style=' font-size: 0;line-height:0;border-width:10px;border-color:#6fa8dc;border-bottom-width: 0;border-style: dashed;border-top-style: solid;border-left-color: transparent;border-right-color: transparent;'></span>");
			}else{
				buff.push("<div  style='width:200px;height:10px;border-radius:10px;'></div>");
			}
			if(list[i].desc == "current"){
				buff.push("<div  style='width:200px;height:30px;border-radius:10px;background-color:#e06666;color:#FFFFFF;'>"+list[i].name+"</div>");
			}else{
				buff.push("<div  style='width:200px;height:30px;border-radius:10px;background-color:#6fa8dc;color:#FFFFFF;'>"+list[i].name+"</div>");
			}
		}		
	}
	this.element.style.backgroundColor = "#FFFFFF";
	this.element.innerHTML = buff.join('');
	
	
};

InstanceStateView.prototype.destroy = function() {
	InstanceStateView.superclass.destroy.call(this);
};

InstanceStateView.prototype.refresh = function(ev) {
	this.fillList();
};


