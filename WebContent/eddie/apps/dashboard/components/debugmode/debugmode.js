var Debugmode = function(options){
	var self = {};
	var settings = {}
	
	$.extend(settings, options);
	

	$('#debugmode_form').submit(function(e) {
		e.preventDefault();
		e.stopPropagation();
		eddie.putLou($('#debugmode_formtarget').val(), $('#debugmode_formvalue').val());
		return false;
	})

	$('#debugmode_star').click(function(e) {
		var pos = $('#debug').css("left");
		if (pos=="-402px") {
			$('#debug').animate({left:'0'},400,function() { })
		} else {
			$('#debug').animate({left:'-402'},400,function() { })
		}
		eddie.putLou('notification','sound(shoof)');
		return false;
	})


	return self;
}
