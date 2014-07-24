function Usermanager(options){
	var self = {};
	var settings = {};
	$.extend(settings, options);

	self.putMsg = function(msg) {
		try{
			var command = [msg.target[0].class];
		}catch(e){
			command = $(msg.currentTarget).attr('class').split(" ");
		}
		var content = msg.content;
		for(i=0;i<command.length;i++){
			switch(command[i]) { 
			}
		}
	}

	self.saveProperty = function(user,name,value) {
		var line = user+','+name+','+value;
		eddie.putLou('usermanager','setusersetting('+line+')');
		return false;
	}
	return self;

}
