var Eddie = function(options){

	var self = {};

	var settings = {
		lou_ip: "",
		lou_port: "",
		app: "",
		fullapp: "",
		postData: "<fsxml><screen><properties><screenId>-1</screenId></properties><capabilities id=\"1\"><properties>"+getCapabilities()+"</properties></capabilities></screen></fsxml>",
		screenId: "",
		active: true,
		appparams: null,
		worker_location: '/eddie/js/eddie_worker.js',
		worker: null
	}
	$.extend(settings, options);

	settings.lou_port = (window.location.port == "") ? '80' : window.location.port;

	self.init = function(){
		if(typeof(Worker) != "undefined"){
			settings.worker = new Worker(settings.worker_location);
  		}else{
  			console.log("Worker not supported");
  		}

		$(self).on('register-success', self.listen);
		register();
		addGestureEvents();

	}

	self.destroy = function() {
		$.each(components, function(key, comp){
			if(typeof comp.destroy == "function"){
				comp.destroy();
			}
		});
		var splits = settings.screenId.split('/');

		self.putLou('notification','show(user ' + splits[splits.length - 1] + ' left session!)');
		var postData = "stop(" + settings.screenId + ")";
		var args =
		self.doRequest({
			'type': 'POST',
			'url': "http://" + settings.lou_ip + ":" + settings.lou_port + "/lou/LouServlet" + settings.fullapp,
			'data': postData,
			'dataType': 'text',
			'async': false
		});
	}

	self.doRequest = function(args){
		$.ajax(args);
	}

	self.getComponent = function(comp){
		return components[comp];
	}

	self.listen = function(){
		if(!settings.worker){
			$(self).on('request-success', function(event, response){
				if(settings.active){
					try{
						parseResponse(response);
					}catch(error){
						console.log(error);
					}
					request();
				}
			})
			request();
		}else{
			settings.worker.postMessage(JSON.stringify({
				'fn': 'init',
				'args': {
					'lou_ip': settings.lou_ip,
					'lou_port': settings.lou_port,
					'screenId': settings.screenId
				}
			}))
			settings.worker.onmessage = function(m){
				parseResponse(m.data);
			}
		}
	}

	self.getScreenId = function(){
		return settings.screenId;
	}

	self.log = function(msg) {
		self.putLou("","log("+msg+",info)");
		return false;
	}

        self.log = function(msg,level) {
                self.putLou("","log("+msg+","+level+")");
                return false;
        }

	self.putLou = function(targetid, content, sync) {
		var postData = "put(" + settings.screenId + "," + targetid + ")=" + content;
		self.doRequest({
			'type': 'POST',
			'url': 'http://' + settings.lou_ip + ":" + settings.lou_port + "/lou/LouServlet" + settings.fullapp,
			'contentType': 'text/plain',
			'data': postData,
			'dataType': 'text',
			'async': !sync
		});

		return false;
	}

	var register = function(){
		var parseRegisterResponse = function(response){
			settings.screenId = $(response).find('screenid').first().text();
			$(self).trigger('register-success');
		}
		self.doRequest({
			'type': 'POST',
			'url': 'http://' + settings.lou_ip +":"+ settings.lou_port + '/lou/LouServlet' + settings.fullapp+"?"+settings.appparams,
			'data': settings.postData,
			'success': parseRegisterResponse
		})
	}

	var request = function(){
		var putData = "<fsxml><screen><properties><screenId>" + settings.screenId + "</screenId></properties></screen></fsxml>";
		var appId = settings.screenId.substring(0, settings.screenId.indexOf("/1/screen"));

		self.doRequest({
			'type': 'POST',
			'url': 'http://' + settings.lou_ip + ':' + settings.lou_port + '/lou/LouServlet' + appId,
			'data': putData,
			'dataType': 'text',
			'contentType': 'text/plain',
			'success': function(data){
				$(self).trigger('request-success', data)
			}
		});
	}

	var parseResponse = function(response){
		var result = response;
		if (result.indexOf("<screenid>appreset</screenid>")!=-1) {
				alert('server reset');
		}
		var pos = result.indexOf("(");
		while (pos!=-1) {
			var command = result.substring(0,pos);
			result = result.substring(pos+1);
			pos = result.indexOf(")");

            var targetid = result.substring(0,pos);

            switch(command){
            	case "set":
            		var content = result.substring(pos+2);
            		pos = content.indexOf("($end$)");
            		if(pos!=-1)
            			content = content.substring(0,pos);
            			setDiv(targetid,content);
            		break;
             	case "add":
            		var content = result.substring(pos+2);
            		pos = content.indexOf("($end$)");
            		if(pos!=-1)
            			content = content.substring(0,pos);
            			addToDiv(targetid,content);
            		break;
            	case "put":
            		var content = result.substring(pos+2);
            		pos = content.indexOf("($end$)");
            		if(pos!=-1)
            			content = content.substring(0,pos);
						putMsg(targetid,content);
					break;
				case "remove":
					remove(targetid);
					break;
				case "setcss":
					var content = result.substring(pos+2);
					pos = content.indexOf("($end$)");
					if (pos!=-1) content = content.substring(0,pos);
                    	setCSS(content);
                    break;
                case "setstyle":
					var content = result.substring(pos+2);
					pos = content.indexOf("($end$)");
					if (pos!=-1)
						content = content.substring(0,pos);
                    	setStyle(content);
                    break;
            	case "setscript":
            		var content = result.substring(pos+2);
					pos = content.indexOf("($end$)");
					if (pos!=-1)
						content = content.substring(0,pos);
                	setScript(targetid,content);
                	break;
                case "removestyle":
            		var content = result.substring(result.substring(result.indexOf("("))+1, result.indexOf(")"));
					removeStyle(content);
                	break;
            }

            // lets check if there are move messages
			pos = result.indexOf("($end$)");
			if (pos!=-1) {
				result = result.substring(pos+7);
				pos = result.indexOf("(");
			}else {
				pos = -1;
			}
		}
	}

	function remove(args){
		var splits = args.split(",");
		var targetid = splits[0];
		var leaveDiv = splits[1];

		if(leaveDiv == "false"){
			removeDiv(targetid);
		}else{
			emptyDiv(targetid);
		}

		removeScript(targetid);
		removeInstance(targetid);
	}

	function removeDiv(targetid) {
		var div = document.getElementById(targetid);
		if (div!=null) {
			div.parentNode.removeChild(div);
		}
	};

	function emptyDiv(targetid){
		var div = document.getElementById(targetid);
		if(div!=null){
			div.innerHTML = "";
		}
	}

	function removeScript(targetid){
		$('#script_' + targetid).remove();
	}

	function removeInstance(targetid){
		delete components[targetid];
	}

	function putMsg(targetid,content) {
		var new_content = content;
		try{
			var pos = new_content.indexOf("(");
			if (pos!=-1) {
				var command = new_content.substring(0,pos);
				var args = new_content.substring(pos+1,new_content.length-1);
				new_content = {
					"target" : [{
						"id" : command,
						"class" : command
					}],
					"content" : args,
					"originalMessage" : content
				};
			}
		}catch(e){ console.log("escaped: " + content);}

		content = new_content;
		var div = document.getElementById(targetid);
		if(components[targetid]){
			components[targetid].putMsg(content);
		}else{
			window[targetid+"_putMsg"](content);
		}
	};

	function setCSS(filename) {
	  var fileref=document.createElement("link");
	  fileref.setAttribute("rel", "stylesheet");
	  fileref.setAttribute("type", "text/css");
	  fileref.setAttribute("href", filename);
	  document.getElementsByTagName("head")[0].appendChild(fileref);
	}

	function setStyle(content){
		try{
			var css = content;
			var stylename = content.substring(0, content.indexOf(","));
			// if(stylename.indexOf("_")!==-1) stylename = stylename.substring(stylename.indexOf("_"));
			// console.log("**************"+content);
			// console.log('stylename: ' + stylename);
			var head = document.getElementsByTagName('head')[0],
			    style = document.getElementsByTagName('style'),
			    sstyle = $("style#"+stylename);
			    content = content.substring(content.indexOf(",")+1)
			if(sstyle.length==0){
				sstyle = document.createElement("style");
				sstyle.type = 'text/css';
				sstyle.setAttribute("id", stylename);
				if (style.styleSheet){
				  sstyle.styleSheet.cssText = content;
				} else {
				  sstyle.appendChild(document.createTextNode(content));
				}
				head.appendChild(sstyle);
			}
			else{
				sstyle.html(content);
			}
		}catch(err){
			var trace = printStackTrace();
		    console.error("eddie.js: "+err.message + "\n\n" + trace.join('\n\n'));
		}
	}

	function removeStyle(style){
		$('style#'+style).remove();
	}

	function setScript(targetid, scriptbody) {
		var script   = document.createElement("script");
		script.type  = "text/javascript";
		script.text  = scriptbody;
		script.id = 'script_' + targetid;
		document.body.appendChild(script);
	}

	function setDiv(targetid,content) {
		// console.log("setting target: "+targetid);
		var div = document.getElementById(targetid);
		if (div!=null) {
	       	    $('#'+targetid).html(content);
		} else {
	  	    div = document.createElement('div');
	  	    div.setAttribute('id',targetid);
	            div.innerHTML = content;
	            document.getElementById("screen").appendChild(div);
		}

	};

	function addToDiv(targetid,content) {
		var div = document.getElementById(targetid);
		if (div!=null) {

	       	    ne = document.createElement('div');
	            ne.innerHTML = content;
	            div.appendChild(ne);
		}
	};

	function getCapabilities() {
		var body="";
		body +="<platform>"+navigator.platform+"</platform>";
		body +="<appcodename>"+navigator.appCodeName+"</appcodename>";
		body +="<appname>"+navigator.appName+"</appname>";
		body +="<appversion>"+navigator.appVersion+"</appversion>";
		body +="<useragent>"+navigator.userAgent+"</useragent>";
		body +="<cookiesenabled>"+navigator.cookieEnabled+"</cookiesenabled>";
		body +="<screenwidth>"+window.innerWidth+"</screenwidth>";
		body +="<screenheight>"+window.innerHeight+"</screenheight>";
		body +="<orientation>"+window.orientation+"</orientation>";

		var browserid = readCookie("smt_browserid");
		if (browserid==null) {
			var date = new Date();
			createCookie("smt_browserid",date.toGMTString(),365);
		}
		browserid = readCookie("smt_browserid");
		body +="<smt_browserid>"+browserid+"</smt_browserid>";

		var s = document.URL;
		s = s.substring(s.indexOf("/domain/")+8);
		s = s.substring(0,s.indexOf("?"));
		s = s.replace(/\//g,"_");
		s = ""; // ignore sessions per app for now
		var sessionid = readCookie("smt_"+s+"_sessionid");
		if (sessionid===null) {
			date = ""+new Date().getTime();
			createCookie("smt_"+s+"_sessionid",date,365);
		}
		sessionid = readCookie("smt_"+s+"_sessionid");
		body +="<smt_sessionid>"+sessionid+"</smt_sessionid>";

		return body;
	}

	function createCookie(name, value, days) {
    	var expires;
    	if (days) {
       	 	var date = new Date();
       	 	date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
         	expires = "; expires=" + date.toGMTString();
    	} else {
        	expires = "";
    	}
   	 	document.cookie = escape(name) + "=" + escape(value) + expires + "; path=/";
	}

	function readCookie(name) {
    	var nameEQ = escape(name) + "=";
    	var ca = document.cookie.split(';');
    	for (var i = 0; i < ca.length; i++) {
        	var c = ca[i];
        	while (c.charAt(0) === ' ') c = c.substring(1, c.length);
        	if (c.indexOf(nameEQ) === 0) return unescape(c.substring(nameEQ.length, c.length));
    	}
    	return null;
	}

function eraseCookie(name) {
    createCookie(name, "", -1);
}
	function addGestureEvents() {
		window.addEventListener("orientationchange", function() {
  			self.putLou('','orientationchange('+window.orientation+')');
		}, false);
	}

	return self;
}
