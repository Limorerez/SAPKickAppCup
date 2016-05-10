sap.ui.controller("view.speechMp3", {

    recordings : {},
    index: null,
    replaying: false,

    onInit: function() {
        this.recordings = {};
        this.canRecord = true;
        this.recordAllowed = false;
        var questionsModel = new sap.ui.model.json.JSONModel("data/speechTests.json");
        this.getView().setModel(questionsModel);

        window.AudioContext = window.AudioContext || window.webkitAudioContext;
        window.URL = window.URL || window.webkitURL;

        var audioContext = new AudioContext;
        var audioRecorder;
        var _realAudioInput;
        var that = this;

        this.startUserMedia = function(stream) {
            that.canRecord = true;
            that.recordAllowed = true;

            _realAudioInput = audioContext.createMediaStreamSource(stream);

            audioRecorder = new Recorder(_realAudioInput);
        };

        this.startRecording = function() {
            if(!audioRecorder)
                return;
            audioRecorder.clear();
            audioRecorder.record();
        };

        this.stopRecording = function(testId) {
            if(!audioRecorder)
                return;
            audioRecorder.stop(function() {
                that.saveRecording(testId);
            });

        };

        this.playStarted = function() {
        	var replayLabel = sap.ui.getCore().byId('speech_replayLabel' + that.index);
            replayLabel.setText('Playing...');
            that.canRecord = false;
            that.replaying = true;
        };

        this.playEnded = function() {
        	var replayLabel = sap.ui.getCore().byId('speech_replayLabel' + that.index);
            replayLabel.setText('Replay');
            that.canRecord = true;
            that.replaying = false;
        };

        this.toggleRecording = function(e, testId){
            if (!this.canRecord) return;
            this.index = testId;
            var mic = sap.ui.getCore().byId("speech_micImg" + testId);
            var recordLbl = sap.ui.getCore().byId("speech_recordLabel" + testId);
            var replayLabel = sap.ui.getCore().byId("speech_replayLabel" + testId);

            var footer = this.getView().footer;
            var prevBtn = footer.getContent()[0];
            var nextBtn = footer.getContent()[footer.getContent().length - 1];
            if (e.hasStyleClass("recording")) {
                // stop recording
                nextBtn.setEnabled(true);
                prevBtn.setEnabled(true);
                this.stopRecording(testId);
                mic.setSrc("images/recordAgain.png");
                recordLbl.setText("Record again?");
                e.removeStyleClass("recording");
            } else {
            	if(!this.recordAllowed) {
            		sap.m.MessageBox.alert('Please allow the browser to access your microphone', {title: 'Unable to record'});
            		return;
            	}
                // start recording
                mic.setSrc("images/Stop.png");
                recordLbl.setText("Recording...");
                nextBtn.setEnabled(false);
                prevBtn.setEnabled(false);
                replayLabel.setVisible(false);
                e.addStyleClass("recording");
                this.startRecording();
            }
        };

        this.saveRecording = function(id){
            if( null == Recorder.blob) return;
            this.recordings[id] = Recorder.blob;
            audioRecorder.clear();
            var replayLabel = sap.ui.getCore().byId("speech_replayLabel" + id);
            replayLabel.setVisible(true);
        }
    },

    onFinish: function(evt) {
        var atLeastOne = false;
        $.each(this.recordings, function(key, value){
            if(value != null) {
                atLeastOne = true;
            }
        });

        if (!atLeastOne) {
            sap.m.MessageBox.alert('No audio was recorded', {title: 'Submission failed'});
            console.error("Error saving audio data on server");
            return;
        }
        
        if (alsApp.config.isDemoMode) {
            alsApp.goToPage("completedPage");
            alsApp.resetPage('speechPage');
            return;
        }

        var that = this;
        var form = new FormData();

        $.each(that.recordings, function(key, value){
            form.append("testIds", key);
            form.append("files", value)
        });

        $.ajax({
            type: 'POST',
            url: alsApp.config.SERVER_URL + "/tasks/speechTests",
            data: form,
            cache: false,
            processData: false,
            contentType: false,
            success: function() {
                alsApp.goToPage("completedPage");
                alsApp.resetPage('speechPage');
            },
            error: function() {
                sap.m.MessageBox.alert('Unable to submit speech test. Try again later.', {title: 'Submission failed'});
            }
        });

    },

    onAfterRendering : function(){
        navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia;
        navigator.cancelAnimationFrame = navigator.cancelAnimationFrame || navigator.webkitCancelAnimationFrame || navigator.mozCancelAnimationFrame || navigator.msCancelAnimationFrame;
        navigator.requestAnimationFrame = navigator.requestAnimationFrame || navigator.webkitRequestAnimationFrame || navigator.mozRequestAnimationFrame || navigator.msRequestAnimationFrame;

        navigator.getUserMedia(
            {
                "audio": {
                    "mandatory": {
                        "googEchoCancellation": "false",
                        "googAutoGainControl": "false",
                        "googNoiseSuppression": "false",
                        "googHighpassFilter": "false"
                    },
                    "optional": []
                }
            }, this.startUserMedia, function(e) {
                console.log(e);
            }
        );
    },
    
    onReplay: function() {
    	if (this.replaying) {
    		return;
    	}
        var blob = this.recordings[this.index];
        var audio = new window.Audio();
        audio.src = window.URL.createObjectURL(blob);
        audio.onplaying = this.playStarted;
        audio.onended = this.playEnded; 
        audio.play();
    }
    
});
