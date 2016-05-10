sap.ui.jsview("view.handwriting", {

    drawings: [],

    nav: null,
    footer: null,

    getControllerName: function() {
        return "view.handwriting";
    },

    createContent: function(oController) {
        this.drawings = [
            {name: 'house', type: 'shape', image: 'images/canvas/house.png'},
            {name: 'smiley', type: 'shape', image: 'images/canvas/smiley.png'},
            {name: 'helloJohn', type: 'text', text: 'Hello John, how are you?'},
        ];
        var pages = [];

        for (var i in this.drawings) {
            var drawing = this.drawings[i];

            var headerLayout = new sap.ui.layout.HorizontalLayout(drawing.name + 'HeaderLayout', {
                content: [
                    new sap.m.Label({
                        text: drawing.type === 'shape' ? 'Follow the shape using your finger or stylus' :
                            'Write down the following sentence:'
                    }).addStyleClass("question_title"),
                    new sap.m.Label({
                        text: parseInt(i) + 1 + "/" + this.drawings.length
                    }).addStyleClass("page_num")
                ]
            }).addStyleClass('questions_header');

            var subHeaderLayout = null;
            if (drawing.type === 'text') {
                subHeaderLayout = new sap.ui.layout.HorizontalLayout(drawing.name + 'SubHeaderLayout', {
                    content: [
                        new sap.m.Label("", {
                            text: '\'' + drawing.text + '\''
                        }).addStyleClass("question_sub_title"),
                    ]
                }).addStyleClass('questions_sub_header');
            }

            var canvasLayout = new sap.ui.layout.VerticalLayout(drawing.name + 'CanvasLayout', {
                content: [new sap.ui.core.HTML({
                    content: '<canvas id="' + drawing.name + 'Canvas" width="400" height="400"></canvas>'
                })]
            }).addStyleClass("hwCanvas");

            var page = new sap.ui.layout.VerticalLayout(drawing.name + 'Page', {
                content: [headerLayout, subHeaderLayout, canvasLayout]
            }).addStyleClass("viz");

            pages.push(page);
        }

        this.nav = new sap.m.NavContainer({
            height: '100%',
            pages: pages,
            initialPage: this.drawings[0].name + 'Page'
        });

        var footer = sap.ui.jsfragment(this.getId(), 'FooterToolbar', {
            nav: this.nav,
            nextCallback: oController.toNextPage.bind(oController),
            finishCallback: oController.onFinish.bind(oController),
            actionCallback: oController.onClear.bind(oController),
            actionName: 'Clear'
        });

        var header = sap.ui.jsfragment(this.getId(), 'HeaderToolbar', {
            title: 'Handwriting',
            showHomeButton: true
        });

        return new sap.ui.layout.VerticalLayout("handwriting_layout", {
            content: [header, this.nav, footer],
            width: '100%'
        });
    },

    onAfterRendering: function() {
        var drawing = this.drawings[0];
        drawing.recordableDrawing = new RecordableDrawing(drawing.name + 'Canvas', drawing.image);
        drawing.recordableDrawing.startRecording();
    }
});
