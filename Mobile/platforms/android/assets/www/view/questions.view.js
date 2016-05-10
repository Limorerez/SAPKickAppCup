sap.ui.jsview("view.questions", {

    nav: null,

    getControllerName: function() {
        return "view.questions";
    },

    createContent: function(controller){
        var that = this;
        this.nav = new sap.m.NavContainer({
            height: '100%'
        });

        this.nav.bindAggregation("pages", {
            path: "/questions",
            factory: function(sId, oContext) {
                var questions = oContext.getModel().getData().questions;

                var answers = oContext.getProperty("answers") || [];
                var index = parseInt(oContext.getPath().replace("/questions/",""), 10);
                var pageId = oContext.getProperty("id");

                var page = new sap.m.Page("page_" + pageId, {
                    showHeader: false
                });

                var box = new sap.m.VBox();
                var headerLayout = new sap.ui.layout.HorizontalLayout({
                    content: [
                        new sap.m.Label("",{"text":"{title}"}).addStyleClass("question_title"),
                        new sap.m.Label({"text": (index + 1) + "/" + questions.length}).addStyleClass("page_num")
                    ]
                }).addStyleClass('questions_header');

                box.addItem(headerLayout);

                for (i = 0; i < answers.length; i++) {
                    box.addItem(new sap.m.RadioButton({
                        groupName: pageId,
                        text: answers[i].title,
                        customData: [
                            new sap.ui.core.CustomData({
                                key: 'answer',
                                value: answers[i].id
                            }),
                            new sap.ui.core.CustomData({
                                key: 'questionId',
                                value: pageId
                            })
                        ],
                        select: function(e){
                            e.getSource().$().closest('.sapMFlexBox').children('.sapMFlexItem.selected').removeClass('selected');
                            e.getSource().$().closest('.sapMFlexItem').addClass('selected');

                            var data = this.getCustomData();
                            var answer = {};
                            var i;

                            for (i = 0; i < data.length; i++) {
                                answer[data[i].getKey()] = data[i].getValue();
                            }

                            var res = this.getModel("resModel").getData();
                            var found = false;
                            for (i = 0; i < res.questionsRes.length; i++) {
                                if (res.questionsRes[i].questionId === answer.questionId) {
                                    found = true;
                                    res.questionsRes[i].answer = answer.answer;
                                    break;
                                }
                            }

                            if (!found) {
                                res.questionsRes.push(answer);
                            }

                            this.getModel("resModel").setData(res);
                        }
                    }));
                }

                box.addItem(new sap.m.Input({
                    placeholder: "Remarks",
                    customData: [
                        new sap.ui.core.CustomData({
                            key: 'questionId',
                            value: pageId
                        })
                    ],
                    change: function(c) {
                        var remarkValue = c.getParameter("newValue");
                        var res = this.getModel("resModel").getData();
                        var pageId = this.getCustomData()[0].getValue();

                        var found = false;
                        for (i = 0; i < res.questionsRes.length; i++) {
                            if (res.questionsRes[i].questionId === pageId) {
                                found = true;
                                res.questionsRes[i].remark = remarkValue;
                                break;
                            }
                        }

                        if (!found) {
                            res.questionsRes.push({
                                questionId: pageId,
                                remark: remarkValue
                            });
                        }

                    }
                })).addStyleClass("remarks");

                page.addContent(box);

                return page;
            }
        });

        var header = sap.ui.jsfragment(this.getId(), 'HeaderToolbar', {
            title: 'Questionnaire',
            showHomeButton: true
        });

        var footer = sap.ui.jsfragment(this.getId(), 'FooterToolbar', {
            nav: this.nav,
            finishCallback: controller.onFinish.bind(controller)
        });

        return new sap.ui.layout.VerticalLayout("question_layout", {
            content: [header, this.nav, footer],
            width: '100%'
        });

    },

    onAfterRendering: function(){
        this.resModel = new sap.ui.model.json.JSONModel({questionsRes: []});
        this.setModel(this.resModel, "resModel");
    }

});
