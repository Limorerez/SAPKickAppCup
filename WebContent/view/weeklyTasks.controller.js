sap.ui.controller("view.weeklyTasks", {

    that: this,

    onInit: function() {
    	this.updateTasksList();
    },

    updateTasksList: function() {
        if (alsApp.config.isDemoMode) {
        	this.successCallback(JSON.stringify([
        		{taskId: "QUESTIONNAIRE", lastSubmittedDate: Date.now()},
        		{taskId: "WRITING_TEST", lastSubmittedDate: Date.now()},
        		{taskId: "SPEECH_TEST", lastSubmittedDate: Date.now()},
        	]));
        	return;
        }
        $(function(){
        	alsApp.busyDialog.open()
        });
        $.ajax({
            type: 'GET',
            url: alsApp.config.SERVER_URL + '/tasks/lastSubmitted',
            contentType: 'application/json',
            success: this.successCallback.bind(this),
            error: this.errorCallback,
            complete: function() {
                alsApp.busyDialog.close();
            }
        });
    },

    successCallback: function(data) {
        var tasks = [
            {id: 'QUESTIONNAIRE', 'image': 'images/Questionnaire.png', name: 'Questionnaire'},
            {id: 'WRITING_TEST', 'image': 'images/Handwriting.png', name: 'Handwriting'},
            {id: 'SPEECH_TEST', 'image': 'images/Speech.png', name: 'Speech'}
	        /*** Currently disable unsupported tasks
	         {id: 'WALKING_TEST', 'image': 'images/Walking.png', name: 'Walking'},
	         {id: 'STAIRS_TEST', 'image': 'images/ClimbingStairs.png', name: 'Climbing Stairs'}*/
        ];

        var now = new Date();
        var millisInDay = 24 * 60 * 60 * 1000;
        var lastMidnight = Date.UTC(now.getUTCFullYear(), now.getUTCMonth(), now.getUTCDate());

        var modelData = [];
        var task;
        var lastSubmittedDate;

        for (var i in tasks) {
            var daysAgo = undefined;
            task = tasks[i];
            lastSubmittedDate =this.getlastSubmittedDateById(JSON.parse(data), task.id);
            if (lastSubmittedDate) daysAgo = Math.ceil((lastMidnight - lastSubmittedDate.getTime()) / millisInDay);
            modelData.push({
                iconTaskSource: task.image,
                taskName: task.name,
                dateStatus: lastSubmittedDate === null ? 'Never' : daysAgo === 0 ? 'Today' : daysAgo === 1 ? 'Yesterday' : daysAgo + ' days ago',
                iconStatusSource: (undefined === daysAgo || daysAgo >= 7 )? 'images/openTask.png' : 'images/completedTask.png'
            });
        }

        var oModel = new sap.ui.model.json.JSONModel({modelData: modelData});
        this.getView().setModel(oModel);
    },
    
    errorCallback: function(data) {
    	if (data.status === 401) {
    		alsApp.goToPage('loginPage');
    	}
    },

    onPress: function(event){
        var src = event.getSource();
        var path = src.getBindingContextPath();
        switch (path) {
            case "/modelData/0":
                alsApp.goToPage("questionsPage");
                break;
            case "/modelData/1":
                alsApp.goToPage("writingPage");
                break;
            case "/modelData/2":
                var navigated = alsApp.goToPage('speechPage');
                if (!navigated) {
                    sap.m.MessageBox.alert('This browser does not support speech recording', {
                        title: 'Feature not supported'
                    });
                }
                break;
            case "/modelData/3":
            case "/modelData/4":
                sap.m.MessageBox.alert('This feature will be available soon', {
                    title: 'Feature unavailable'
                });
                break;
        }
    },

    getlastSubmittedDateById: function(lastSubmittedTasks, id) {
        for (var i = 0; i < lastSubmittedTasks.length; i++) {
            if (lastSubmittedTasks[i].taskId === id) {
                return new Date(lastSubmittedTasks[i].lastSubmittedDate);
            }
        }
        return null;
    }

});
