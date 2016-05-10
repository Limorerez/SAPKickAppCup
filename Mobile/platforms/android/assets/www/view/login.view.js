sap.ui.jsview("view.login", {

    emailInput: null,
    passwordInput: null,

    getControllerName: function() {
        return "view.login";
    },

    createContent: function(controller){
        var appLogoIcon = new sap.m.Image('appLogoIcon', {
            src: 'images/appLogo.png',
            height: '2.5rem',
            densityAware: false
        }).addStyleClass('logoIcon');

        var p4lLogoIcon = new sap.m.Image('p4lLogoIcon', {
            src: 'images/p4lLogo.png',
            height: '2.5rem',
            densityAware: false
        }).addStyleClass('logoIcon');

        var logosLayout = new sap.ui.layout.HorizontalLayout('logosLayout', {
            content: [appLogoIcon, p4lLogoIcon]
        }).addStyleClass('centeredLayout');

        this.emailInput = new sap.m.Input('emailInput', {
            placeholder: 'Email',
            value: localStorage.getItem('alsEmail'),
            width: '17rem',
            liveChange: function(event) {
                alsApp.setEmail(event.getSource().getValue());
            }
        }).attachBrowserEvent('keypress', controller.onKeypress.bind(this.getController()));

        this.passwordInput = new sap.m.Input('passwordInput', {
            placeholder: 'Password',
            width: '17rem',
            type: 'Password'
        });

        var forgotPasswordLabel = new sap.m.Label('forgotPasswordLabel', {
            text: "Forgot your password?"
        }).attachBrowserEvent('click', controller.onForgotPassword);

        var separatorLabel = new sap.m.Label('separatorLabel', {
            text: "|"
        });

        var firstUseLabel = new sap.m.Label('firstUseLabel', {
            text: "Register"
        }).attachBrowserEvent('click', controller.onRegister);

        var registerAndPasswordLayout = new sap.ui.layout.HorizontalLayout('registerAndPasswordLayout', {
            content: [firstUseLabel, separatorLabel, forgotPasswordLabel]
        });

        var registerDetailsLayout = new sap.ui.layout.VerticalLayout('registerDetailsLayout', {
            content: [this.emailInput, this.passwordInput, registerAndPasswordLayout]
        }).addStyleClass('centeredLayout');

        var disclaimerLabel = new sap.m.Label('disclaimerLabel', {
            text: "Legal Disclaimer",
            width: "100%",
            textAlign: 'Center'
        }).attachBrowserEvent('click', controller.onDisclaimer);

        var contentLayout = new sap.ui.layout.VerticalLayout('contentLayout', {
            content: [logosLayout, registerDetailsLayout, disclaimerLabel],
            width: '100%'
        });

        var loginLabel = new sap.m.Label('loginLabel', {
            text: "Login",
            width: "100%",
            textAlign: 'Center'
        }).attachBrowserEvent('click', controller.onLogin.bind(controller));

        var layout = new sap.ui.layout.VerticalLayout('loginPageLayout', {
            content: [contentLayout, loginLabel],
            width: '100%'
        });

        return layout;
    },

    setEmail: function(email) {
        this.emailInput.setValue(email);
    }

});
