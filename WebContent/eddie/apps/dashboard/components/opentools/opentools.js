function Opentools(options){
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

	eddie.putLou('','load(openapps)');
	eddie.putLou('','load(availableapps)');
	eddie.putLou('','load(usermanager)');
	eddie.putLou('','load(dashboardselector)');
	$('#opentools').animate({opacity:'1'},1000,function() { });

	return self;
}