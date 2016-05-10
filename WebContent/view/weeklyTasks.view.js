sap.ui.jsview("view.weeklyTasks", {

    header: null,

    getControllerName: function() {
        return "view.weeklyTasks";
    },

    createContent: function(oController) {
        var that = this;

        var subTitle = new sap.m.Label({
            text:"Weekly Tasks",
            width: '100%'
        }).addStyleClass("subtitle");

        var list = new sap.m.List({ }).addStyleClass("list");

        var itemTemplate = new sap.m.CustomListItem({

            content: [
                new sap.m.Image({
                    src: "{iconTaskSource}",
                    size: "0.8rem",
                    densityAware: false
                }).addStyleClass("taskIcon"),

                new sap.m.Label({
                    text: "{taskName}"
                }).addStyleClass("task"),

                new sap.ui.layout.VerticalLayout({
                    content:[
                        new sap.m.Image({
                            src: "{iconStatusSource}",
                            densityAware: false
                        }).addStyleClass("statusIcon"),

                        new sap.m.Label({
                            text: "{dateStatus}"
                        }).addStyleClass("dateStatus")
                    ]
                }).addStyleClass("layout")
            ],
            type: "Active"

        }).attachPress(oController.onPress);

        list.bindAggregation("items", {
            path: "/modelData",
            template: itemTemplate
        });

        return new sap.m.Page({
            showHeader: false,
            content: [this.getHeader(), subTitle, list]
        });
    },

    replaceHeader: function() {
        var content = this.getContent()[0]
        var current = content.getContent();
        content.removeAllContent();
        this.header.destroy();
        content.addContent(this.getHeader());
        for (var i = 1; i < current.length; i++) {
            content.addContent(current[i]);
        }
    },

    getHeader: function() {
        this.header = sap.ui.jsfragment('HeaderToolbar', {
            title: alsApp.config.APP_NAME,
            showHomeButton: false,
            showLoginInfo: true
        });
        return this.header;
    }

});