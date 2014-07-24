function Appdetails(options){
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
	
	$('#appdetails').mouseup(function(){
	// $('#appdetails').animate({opacity:'0'},400,function() { appdetails_animDone(); })
	});
	
	self.done = function() {
		$('#appdetails').animate({opacity:'0'},400,function() { appdetails_animDone(); })
	}
	
	self.setAutoDeploy = function(version,value) {
		eddie.putLou('availableapps','setautodeploy('+version+','+value+')');
	}

	function appdetails_animDone() {
	 $('#appdetails').css('visibility','hidden');
	}

	return self;
}