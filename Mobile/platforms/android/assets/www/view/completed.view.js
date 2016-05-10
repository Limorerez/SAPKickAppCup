sap.ui.jsview("view.completed", {

    getControllerName: function() {
        return "view.completed";
    },

    createContent: function(controller){
        var completedLabel = new sap.m.Label('completedLabel', {
            text: "Back to menu",
            width: "100%",
            textAlign: 'Center'
        }).attachBrowserEvent('click',function(){
                alsApp.backToPage('weeklyTasksPage');
            });

        var completedImage = new sap.m.Image('ribbonIcon', {
            src: 'images/Completed.png',
            densityAware: false
        });

        var layout = new sap.ui.layout.VerticalLayout('completedPageLayout', {
            content: [completedImage, completedLabel],
            width: '100%'
        });

        return layout;
    }
});
