sap.ui.controller("view.handwriting", {

    toNextPage: function() {
        var view = this.getView();
        var toDrawing = view.drawings[view.nav.indexOfPage(view.nav.getCurrentPage()) + 1];
        if (!toDrawing.recordableDrawing) {
            setTimeout(function() {
                toDrawing.recordableDrawing = new RecordableDrawing(toDrawing.name + 'Canvas', toDrawing.image);
                toDrawing.recordableDrawing.startRecording();
            }, 0);
        }
    },

    onClear: function() {
        var view = this.getView();
        var drawing = view.drawings[view.nav.indexOfPage(view.nav.getCurrentPage())].recordableDrawing;
        if (drawing) {
            drawing.stopRecording();
            drawing.clearCanvas();
            drawing.startRecording();
        }
    },

    onFinish: function(){
        var view = this.getView();
        var result = [];

        for (var i in view.drawings) {
            var drawing = view.drawings[i];
            var serialized = serializeDrawing(drawing.recordableDrawing);
            if (serialized !== null) {
                result.push({
                    drawingName: drawing.name,
                    drawingJson: JSON.stringify(serialized)
                });
            }
        }

        if (result.length === 0) {
            sap.m.MessageBox.alert('You did not draw anything', {title: 'Task incomplete'});
            return;
        }
        
        if (alsApp.config.isDemoMode) {
            alsApp.goToPage("completedPage");
            alsApp.resetPage('writingPage');
            return;
        }
        
        var url = alsApp.config.SERVER_URL + "/tasks/writingTests";
        var data = {
            drawings: result
        };

        alsApp.busyDialog.open();

        $.ajax({
            url: url,
            data: JSON.stringify(data),
            type: 'POST',
            contentType: 'application/json',
            success: function() {
                alsApp.goToPage("completedPage");
                alsApp.resetPage('writingPage');
            }.bind(this),
            error: function(e){
                sap.m.MessageBox.alert('Unable to submit handwriting test. Try again later.', {title: 'Submission failed'});
            },
            complete: function(){
                alsApp.busyDialog.close();
            }
        });
    }

});