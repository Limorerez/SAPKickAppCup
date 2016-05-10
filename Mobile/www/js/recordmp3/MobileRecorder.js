(function(window){


    var MobileRecorder = function() {

        var recordFileFQN = null;
        var fbaseName = "recording";
        var extension  =  ".amr";
        this.theRecorder, this.thePlayer = null;
        var _fileReadyCallback = null;
        var currentFileName = "recording1";
        var mediaStatus = Media.MEDIA_NONE;
        var blob;


        function gotFile(file){
            var reader = new FileReader();
            var callback = _fileReadyCallback;
            reader.onloadend = function(evt) {
                var value = evt.target.result;
                MobileRecorder.blob = new Blob([value]);
                if(callback) callback();
            };

            reader.readAsArrayBuffer(file);
        }


        function onSuccessGetFileEntry(fileEntry){
            fileEntry.file(gotFile,onFail);
        }

        function onSuccessGetFS(fileSystem) {
            fileSystem.root.getFile(currentFileName, {create: false, exclusive: false}, onSuccessGetFileEntry, onFail);
        }

        function onMediaCallSuccess(){
            //TODO: add console log

        };
        function onMediaCallError(){
            //TODO: add console log

        };

        this.onMediaStatusChanged = function(status){
            mediaStatus = status;
        }


        function onFail(){
            //TODO: add console log
        };

        this.loadRecordingFile = function() {
            window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, onSuccessGetFS,  onFail);
        };

        this.getSrcName = function (index){
            return fbaseName + index + extension;

        }
        this.record = function (index) {

            if (this.theRecorder) {
                this.theRecorder.release();
            }
            currentFileName = this.getSrcName(index);
            this.theRecorder = new Media(currentFileName, onMediaCallSuccess, onMediaCallError);
            this.theRecorder.startRecord();
        };

        this.stop = function (callback) {
            if (this.theRecorder) {
                this.theRecorder.stopRecord();
                _fileReadyCallback = callback;
                this.loadRecordingFile()
            }
        };

        this.clear = function () {
            if(this.thePlayer)
                this.thePlayer.release();
            this.thePlayer = null;
            if (this.theRecorder)
                this.theRecorder.release();
            this.theRecorder = null;
            this.thePlayer = null;

        };

        this.play = function (index) {
            if(mediaStatus == Media.MEDIA_RUNNING) return;
            this.thePlayer = new Media(this.getSrcName(index), onMediaCallSuccess, onMediaCallError,this.onMediaStatusChanged);
            if (this.thePlayer) {
                this.thePlayer.play();
            }
        }
    }
    window.MobileRecorder = MobileRecorder;

})(window);
