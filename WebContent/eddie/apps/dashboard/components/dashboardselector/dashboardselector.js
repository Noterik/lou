function Dashboardselector(options){
	var self = {};
	var settings = {};
	var activetab = "opentools";
	$.extend(settings, options);

	self.putMsg = function(msg){
		try{
			var command = [msg.target[0].class];
		}catch(e){
			command = $(msg.currentTarget).attr('class').split(" ");
		}
		var content = msg.content;
		for(i=0;i<command.length;i++){
			switch(command[i]) { 
				case 'asdasddas':
						break;
				default:
					alert('unhandled msg in openapps.html : '+msg); 
			}
		}
	}

	self.animDoneUserManager = function() {
	 $('#opentools').css('visibility','hidden');
	 $('#availableapps').css('visibility','hidden');
	 $('#usermanager').css('visibility','visible');
	 $('#usermanager').animate({left:'100px'},600,function() {})
	 activetab = "usermanager";
	}

	self.animDoneOpenApps = function() {
	 $('#opentools').css('visibility','visible');
	 $('#usermanager').css('visibility','hidden');
	 $('#availableapps').css('visibility','hidden');
	 $('#opentools').animate({left:'100px'},600,function() {})
	 activetab = "opentools";
	}

	self.animDoneAvailableApps = function() {
	 $('#opentools').css('visibility','hidden');
	 $('#usermanager').css('visibility','hidden');
	 $('#availableapps').css('visibility','visible');
	 $('#availableapps').animate({left:'100px'},600,function() {})
	 activetab = "availableapps";
	}


	$('#dashboardselector_usermanager').mouseup(function(){
	 eddie.putLou('usermanager','showusers()');
	 $('#'+activetab).animate({left:'-1000px'},600,function() { self.animDoneUserManager(); })
	 $('#dashboardselector_openapps').css('background-color','#eee');
	 $('#dashboardselector_availableapps').css('background-color','#eee');
	 $('#dashboardselector_usermanager').css('background-color','#336');
	 $('#dashboardselector_openapps').css('color','#666');
	 $('#dashboardselector_availableapps').css('color','#666');
	 $('#dashboardselector_usermanager').css('color','#fff');
	});

	$('#dashboardselector_availableapps').mouseup(function(){
	 eddie.putLou('applicationmanager','showallapps()');
	 $('#'+activetab).animate({left:'-1000px'},600,function() { self.animDoneAvailableApps(); })
	 $('#dashboardselector_openapps').css('background-color','#eee');
	 $('#dashboardselector_availableapps').css('background-color','#336');
	 $('#dashboardselector_usermanager').css('background-color','#eee');
	 $('#dashboardselector_openapps').css('color','#666');
	 $('#dashboardselector_availableapps').css('color','#fff');
	 $('#dashboardselector_usermanager').css('color','#666');
	});

	$('#dashboardselector_openapps').mouseup(function(){
	 $('#opentools').css('visibility','visible');
	 $('#'+activetab).animate({left:'-1000px'},600,function() { self.animDoneOpenApps(); })
	 $('#dashboardselector_openapps').css('background-color','#336');
	 $('#dashboardselector_usermanager').css('background-color','#eee');
	 $('#dashboardselector_availableapps').css('background-color','#eee');
	 $('#dashboardselector_openapps').css('color','#fff');
	 $('#dashboardselector_availableapps').css('color','#666');
	 $('#dashboardselector_usermanager').css('color','#666');
	});

	return self;
}