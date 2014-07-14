var Requester = function(){
	var self = {};
	var running = true;
	
	self.init = function(args){
		while(running){
			request(args)
		}
	}
	
	var request = function(args){
		var putData = "<fsxml><screen><properties><screenId>" + args.screenId + "</screenId></properties></screen></fsxml>"
		var xhr = new XMLHttpRequest();
		var appId = args.screenId.substring(0, args.screenId.indexOf("/1/screen"));
    	xhr.open("PUT","http://" + args.lou_ip + ":" + args.lou_port + "/lou/LouServlet" + appId, false);
  		xhr.send(putData);
  		result = xhr.responseText;
		if(result != ""){
			if (result.indexOf("<screenid>appreset</screenid>")!=-1) {
				running = false;
			}
			postMessage(result);
		}
	}
	
	return self;
};

onmessage = function(e){
	var message = JSON.parse(e.data);
	var r = Requester();
	if(message.fn){
		r[message.fn](message.args);
	}
};
