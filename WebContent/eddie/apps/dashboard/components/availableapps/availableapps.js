function Availableapps(options){
	var self = {};
	var settings = {};
	$.extend(settings, options);

	eddie.putLou('availableapps','update()');

	self.putMsg = function(msg){
		try{
			var command = [msg.target[0].class];
		}catch(e){
			command = $(msg.currentTarget).attr('class').split(" ");
		}
		var content = msg.content;
		for(i=0;i<command.length;i++){
			switch(command[i]) { 
				case 'update':
					break;
				default:
					alert('unhandled msg in availableapps.html : '+msg+" command="+command[i]); 
			}
		}
	}
	
	self.getdetails = function(restid) {
		$('#appdetails').css('visibility','visible');
		$('#appdetails').animate({opacity:'1'},400,function() { })
		eddie.putLou('availableapps','selectapp('+restid+')');
	}
	
	
	self.makeproduction = function(version) {
		eddie.putLou('availableapps','makeproduction('+version+')');
	}
	
	self.deleteversion = function(version) {
		eddie.putLou('availableapps','deleteversion('+version+')');
	}
	
	self.makedevelopment = function(version) {
		eddie.putLou('availableapps','makedevelopment('+version+')');
	}

	self.uploadnew = function(version) {
		eddie.putLou('availableapps','uploadnew('+version+')');
	}
	
	self.upload = function(version) {
		eddie.putLou('availableapps','upload('+version+')');
	}
	
	return self;
}