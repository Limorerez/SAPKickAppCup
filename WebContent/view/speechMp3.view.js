sap.ui.jsview("view.speechMp3", {

    footer: null,

    getControllerName : function() {
        return "view.speechMp3";
    },

    createContent : function(oController) {
        var that = this;

        this.nav = new sap.m.NavContainer({
            height : '100%'
        });

        this.nav.bindAggregation("pages", {
            path : "/tests",
            factory : function(sId, oContext) {
                var tests = oContext.getModel().getData().tests;
                var numOfTests = tests.length;
                var index = parseInt(oContext.getPath().replace("/tests/", ""), 10);
                var pageId = oContext.getProperty("id");
                var page = new sap.m.Page({
                    showHeader : false
                });

                var headerLayout = new sap.ui.layout.HorizontalLayout({
                    content : [
                        new sap.m.Label({text: "Tap record and say the following sentence:"}).addStyleClass("question_title"),
                        new sap.m.Label({text: (index + 1) + "/" + numOfTests}).addStyleClass("page_num")
                    ]
                }).addStyleClass('questions_header');

                var headerSentLayout = new sap.ui.layout.HorizontalLayout({
                    content : [new sap.m.Label({text : "{title}"}).addStyleClass("question_sub_title")]
                }).addStyleClass('questions_sub_header');

                var micImg = new sap.ui.commons.Image("speech_micImg" + index)
                    .addStyleClass("centeredLayout speech_micImg")
                    .setSrc("images/Record.png")
                    .attachPress(function() {
                        that.getController().toggleRecording(this, index);
                    }
                );

                var recordLabel = new sap.m.Label("speech_recordLabel" + index).addStyleClass('recordLabel');

                var replayLabel = new sap.m.Label("speech_replayLabel" + index, {
                        text : "Replay",
                        visible: false
                    }
                ).attachBrowserEvent('click', oController.onReplay.bind(oController)).addStyleClass('speechReplay');

                var vbox = new sap.m.VBox('sh' + index, {
                    items : [ headerLayout, headerSentLayout,
                        recordLabel, micImg, replayLabel ]
                });
                page.addContent(vbox);
                return page;
            }

        });

        this.footer = sap.ui.jsfragment(this.getId(), 'FooterToolbar', {
            nav : this.nav,
            finishCallback : oController.onFinish.bind(oController)
        });

        var header = sap.ui.jsfragment(this.getId(), 'HeaderToolbar', {
            title : 'Speech',
            showHomeButton : true
        });

        var contentLayout = new sap.ui.layout.VerticalLayout('speech_layout', {
            content : [ header, this.nav, this.footer ],
            width : '100%'
        });
        return contentLayout;
    }

});
