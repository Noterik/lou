function Openapps(options){
	var self = {};
	var settings = {};
	$.extend(settings, options);

	eddie.putLou('openapps','update()');

	self.putMsg = function(msg){
		try{
			var command = [msg.target[0].class];
		}catch(e){
			command = $(msg.currentTarget).attr('class').split(" ");
		}
		var content = msg.content;
		for(i=0;i<command.length;i++){
			switch(command[i]) { 
				case 'closelog':
					$('#logger').css('visibility','hidden');
                			$('#logger').animate({opacity:'01'},800,function() { })
					break;
				default:
					alert('unhandled msg in openapps.html : '+msg); 
			}
		}
	}

	self.openlogger = function(restid) {
		$('#logger').css('visibility','visible');
        $('#logger').animate({opacity:'1'},800,function() { })
		eddie.putLou('openapps','startlogger('+restid+')');
	}

	self.getdetails = function(restid) {
		$('#appdetails').css('visibility','visible');
		$('#appdetails').animate({opacity:'1'},400,function() { })
		eddie.putLou('openapps','selectapp('+restid+')');
	}

	self.locate = function(restid) {
		eddie.putLou('openapps','locate('+restid+')');
	}

	return self;
}
