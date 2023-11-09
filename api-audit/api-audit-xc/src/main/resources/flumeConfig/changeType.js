function parse(event){
	var _type = event.getHeaders().get("_TYPE");
	if (_type.indexOf("probe-netflow") != -1) {
		event.getHeaders().put("_TYPE","probe-netflow");
		return event;
	} else if (_type.indexOf("probe-net") != -1) {
		event.getHeaders().put("_TYPE","probe-net");
		return event;
	} else if (_type.indexOf("app-audit") != -1) {
		event.getHeaders().put("_TYPE","app-audit");
		return event;
	} else if (_type.indexOf("netflow") != -1) {
		event.getHeaders().put("_TYPE","netflow");
		return event;
	} else if (_type.indexOf("ids-event") != -1) {
		event.getHeaders().put("_TYPE","ids-event");
		return event;
	} else if (_type.indexOf("fireware-event") != -1) {
		event.getHeaders().put("_TYPE","fireware-event");
		return event;
	}
}