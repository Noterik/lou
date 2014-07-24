var eddie = null;
$(document).ready(function(){
	eddie = Eddie({
		lou_ip: LouSettings.lou_ip,
		lou_port: LouSettings.lou_port,
		app: LouSettings.app,
		fullapp: LouSettings.fullapp,
		appparams: LouSettings.appparams
	});
	eddie.init();
	
	$('body').attr('onunload', 'eddie.destroy()');
})
