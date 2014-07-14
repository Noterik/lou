function Debugger(options){
    var self = {};
    var settings = {};
    $.extend(settings, options);

    eddie.putLou('debugger','start');
    $('#debugger').animate({opacity:'1'},800,function() { })

    var prompt = "$";

    self.putMsg = function(msg) {
        try{
            var command = [msg.target[0].class];
        }catch(e){
            command = $(msg.currentTarget).attr('class').split(" ");
        }
        var content = msg.content;
        for(i=0;i<command.length;i++){
            switch(command[i]) { 
                case 'quit':
                        $('#debugger').animate({opacity:'0'},1600,function() { self.animDone(); })
                        break;
                case 'color':
                        $('#debugger').css("background-color","rgba("+content+")");
                        break;
                case 'backgroundcolor':
                        $('body').css("background-color","rgb("+content+")");
                        break;
                case 'textcolor':
                        $('#debugger_output').css("color","rgb("+content+")");
                        $('#debugger_console').css("color","rgb("+content+")");
                        break;
                case 'html':
                        $('#debugger_output').html(content);
                        var textarea = document.getElementById('debugger_output');
                            textarea.scrollTop = textarea.scrollHeight;
                        break;
                case 'prompt':
                        prompt = content;
                        debugger_console.value = content;
                        break;
                case 'newcommand':
                            debugger_console.value = prompt + content;
                        break;
                default:
                        alert('unhandled msg in debugger.html : '+msg);
            }
        }
    }

    self.change = function(event) {
       var keyCode = ('which' in event) ? event.which : event.keyCode;
       if (keyCode==13) {
        var tc = debugger_console.value;
        tc = tc.substring(prompt.length);
        eddie.putLou('debugger',tc);
        debugger_console.value = prompt;
       }
    }

    self.keydown = function(event) {
       var keyCode = ('which' in event) ? event.which : event.keyCode;
       if (keyCode==8 && debugger_console.value.length<prompt.length+1) {
            return false;
       } else if (keyCode==9) {
            var tc = debugger_console.value;
            tc = tc.substring(prompt.length);
            eddie.putLou('debugger',"filecomplete "+tc);
            return false;
       } else if (keyCode==38) {
            eddie.putLou('debugger',"prev");
            return false;
       } else if (keyCode==40) {
            eddie.putLou('debugger',"next");
            return false;
       }
       return true;
    }

    self.animDone = function() {
        eddie.putLou('','remove(debugger)');
    }
    return self;
}
