sap.ui.controller("view.questions", {

    onInit: function () {
        var questionsModel = new sap.ui.model.json.JSONModel("data/questions.json");
        this.getView().setModel(questionsModel);
    },

    onFinish: function(){
        var view = this.getView();

        var oModel = view.getModel("resModel");
        var data = {
            answers: oModel.getData().questionsRes
        };

        if (data.answers.length === 0) {
            sap.m.MessageBox.alert('You cannot submit an empty questionnaire', {
                title: 'Incomplete Task'
            });
            return;
        }
        
        if (alsApp.config.isDemoMode) {
            alsApp.goToPage("completedPage");
            alsApp.resetPage('questionsPage');
            return;
        }

        alsApp.busyDialog.open();

        $.ajax({
            url: alsApp.config.SERVER_URL + "/tasks/questionnaires",
            data: JSON.stringify(data),
            type: 'POST',
            contentType: 'application/json',
            success : function(){
                alsApp.goToPage("completedPage");
                alsApp.resetPage('questionsPage');
            }.bind(this),
            error: function(){
                sap.m.MessageBox.alert('Unable to submit questionnaire. Try again later.', {title: 'Submission failed'});
            },
            complete: function(){
                alsApp.busyDialog.close();
            }
        });
    }

});
