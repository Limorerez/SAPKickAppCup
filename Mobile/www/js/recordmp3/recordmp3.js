(function(window){


var recordWavWorker = new Worker('js/recordmp3/recorderWorker.js');
var encoderMp3Worker = new Worker('js/recordmp3/mp3Worker.js');

var Recorder = function(source) {

  var bufferLen = 4096;
  var recording = false;
  var _fileReadyCallback = null;
  this.context = source.context;

  /*
    ScriptProcessorNode createScriptProcessor (optional unsigned long bufferSize = 0,
     optional unsigned long numberOfInputChannels = 2, optional unsigned long numberOfOutputChannels = 2 );
  */

  this.node = (this.context.createScriptProcessor || this.context.createJavaScriptNode).call(this.context, bufferLen, 1, 1);
  this.node.connect(this.context.destination); //this should not be necessary

  this.node.onaudioprocess = function(e) {

    if (!recording)
      return;

    var channelLeft = e.inputBuffer.getChannelData(0);

    console.log('onAudioProcess' + channelLeft.length);

    encoderMp3Worker.postMessage({
      command: 'encode',
      buf: channelLeft
    });

    recordWavWorker.postMessage({
      command: 'record',
      buffer: channelLeft
    });

  }

  source.connect(this.node);

  this.record = function() {

    if (recording)
      return false;

    recording = true;

    var sampleRate = this.context.sampleRate;

    console.log("Initializing WAV");
    

    recordWavWorker.postMessage({
      command: 'init',
      config: {
        sampleRate: sampleRate
      }
    });

    console.log("Initializing to Mp3");
    

    encoderMp3Worker.postMessage({
      command: 'init',
      config: {
        channels: 1,
        mode: 3 /* means MONO*/ ,
        samplerate: 22050,
        bitrate: 64,
        insamplerate: sampleRate
      }
    });

  }

  this.stop = function(callback) {

    if (!recording)
      return;

    recordWavWorker.postMessage({
      command: 'finish'
    });

    encoderMp3Worker.postMessage({
      command: 'finish'
    });

    recording = false;
    _fileReadyCallback = callback;
    
  }
  
  this.clear = function(){
	  Recorder.blob = null;
  }
  
  

  encoderMp3Worker.onmessage = function(e) {

    var command = e.data.command;

    console.log('encoderMp3Worker - onmessage: ' + command);

    switch (command) {
      case 'data':
        var buf = e.data.buf;
        console.log('Receiving data from mp3-Encoder');

        //maybe you want to send to websocket channel, as:
        //https://github.com/akrennmair/speech-to-server

        break;
      case 'mp3':
        var buf = e.data.buf;
        endFile(buf, 'mp3');
        // Removed the terminate of the worker - terminate does not allow multiple recordings
        //encoderMp3Worker.terminate();
        //encoderMp3Worker = null;
        break;
    }

  };

  recordWavWorker.onmessage = function(e) {

    var command = e.data.command;

    console.log('recordWavWorker - onmessage: ' + command);

    switch (command) {
      case 'wav':
        //endFile(e.data.buf, 'wav');
        break;
    }

  };

  function endFile(blob, extension) {
    console.log("Done converting to " + extension);
    console.log("the blob " + blob + " " + blob.size + " " + blob.type);
    
    Recorder.blob = blob;
    if(_fileReadyCallback) _fileReadyCallback();
    //////////
    //Temporary just in order to test recorded audio!
    //Can be used in order to play the audio later on.
    //link_1 - to be removed from the UI!!
    //var url = (window.URL || window.webkitURL).createObjectURL(blob);
    //var filename = new Date().toISOString() + '.' + extension;
	//var link = document.getElementById("link_1");
	//link.href = url;
	//link.download = filename;
	//$( "#link_1" ).attr('download', filename);
	/////////
  }

};



	
window.Recorder = Recorder;

})(window);