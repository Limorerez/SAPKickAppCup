sap.ui.jsview("view.register", {

    emailRegisterInput: null,
    passwordRegisterInput: null,
    repeatPasswordRegisterInput: null,
    patientRadioButton:null,
    controlRadioButton:null,

    nameRegisterInput: null,
    genderSelect: null,
    yearOfBirthInput: null,
    diagnosisDateTimeInput: null,

    getControllerName: function() {
        return "view.register";
    },

    createContent: function(controller) {
        var that = this;
        var userTypeRadioButtonGroupID = "userType";
        var appLogoIcon = new sap.m.Image('appLogoIconRegister', {
            src: 'images/appLogo.png',
            height: '2.5rem',
            densityAware: false
        }).addStyleClass('logoIcon');

        var p4lLogoIcon = new sap.m.Image('p4lLogoIconRegister', {
            src: 'images/p4lLogo.png',
            height: '2.5rem',
            densityAware: false
        }).addStyleClass('logoIcon');

        var registerLayout = new sap.ui.layout.HorizontalLayout('registerLayout', {
            content: [appLogoIcon, p4lLogoIcon]
        }).addStyleClass('centeredLayout');

        this.emailRegisterInput = new sap.m.Input('emailRegisterInput', {
            placeholder: 'Email',
            value: localStorage.getItem('alsEmail'),
            width: '17rem',
            liveChange: function(event) {
                alsApp.setEmail(event.getSource().getValue());
            }
        });

        this.passwordRegisterInput = new sap.m.Input('passwordRegisterInput', {
            placeholder: 'Password',
            width: '17rem',
            type: 'Password'
        });

        this.repeatPasswordRegisterInput = new sap.m.Input('repeatPasswordRegisterInput', {
            placeholder: 'Repeat password',
            width: '17rem',
            type: 'Password'
        });
        
        var userTypeLabel = new sap.m.Label('userTypeLabel', {
        	text: 'User type'
        })

        this.patientRadioButton = new sap.m.RadioButton('controlRadioButton', {
        	groupName: userTypeRadioButtonGroupID,
        	selected: true,
        	text: "Patient",
        	select: function (){
        		handleUserTypeSelection(alsApp.config.USER_TYPE.PATIENT);
        	}
        });
        
        this.controlRadioButton = new sap.m.RadioButton('patientRadioButton', {
        	groupName: userTypeRadioButtonGroupID,
        	text: "Control",
        	select: function (){
        		handleUserTypeSelection(alsApp.config.USER_TYPE.CONTROL);
        	}
        });
        
        var userTypeLayout = new sap.ui.layout.HorizontalLayout('userTypeLayout', {
        	content: [userTypeLabel, this.patientRadioButton, this.controlRadioButton]
        }).addStyleClass('centeredLayout');
        
        this.nameRegisterInput = new sap.m.Input('nameRegisterInput', {
            placeholder: 'Name (optional)',
            width: '8.25rem'
        });

        this.yearOfBirthInput = new sap.m.Input('yearOfBirthInput', {
            placeholder: 'Year of birth',
            type: 'Number',
            width: '8.25rem'
        });

        var nameAndBirthLayout = new sap.ui.layout.HorizontalLayout('nameAndBirthLayout', {
            content: [this.nameRegisterInput, this.yearOfBirthInput]
        });

        this.genderSelect = new sap.m.Select('genderSelect', {
            items: [
                new sap.ui.core.Item({key: 'Gender', text: 'Gender'}),
                new sap.ui.core.Item({key: 'Male', text: 'Male'}),
                new sap.ui.core.Item({key: 'Female', text: 'Female'})
            ],
            width: '8.25rem'
        });

        this.diagnosisDateTimeInput = new sap.m.DateTimeInput('diagnosisDateTimeInput', {
            type: 'Date',
            placeholder: 'Date of diagnosis',
            displayFormat: 'MMMM yyyy',
            width: '8.25rem'
        });

        var genderAndDiagnosisLayout = new sap.ui.layout.HorizontalLayout('genderAndDiagnosisLayout', {
            content: [this.genderSelect, this.diagnosisDateTimeInput]
        });

        var alreadyRegisteredLabel = new sap.m.Label('alreadyRegisteredLabel', {
            text: "Already registered? Login",
            width: "15rem",
            textAlign: 'Center'
        }).attachBrowserEvent('click', controller.onAlreadyRegistered);

        var detailsLayout = new sap.ui.layout.VerticalLayout('detailsLayout', {
            content: [this.emailRegisterInput, this.passwordRegisterInput, this.repeatPasswordRegisterInput,
                      userTypeLayout, nameAndBirthLayout, genderAndDiagnosisLayout, alreadyRegisteredLabel]
        }).addStyleClass('centeredLayout');

        var registerContentLayout = new sap.ui.layout.VerticalLayout('registerContentLayout', {
            content: [registerLayout, detailsLayout],
            width: '100%'
        });

        var registerFooterLabel = new sap.m.Label('registerFooterLabel', {
            text: "Register",
            width: "100%",
            textAlign: 'Center'
        }).attachBrowserEvent('click', controller.onRegister.bind(controller));

        var layout = new sap.ui.layout.VerticalLayout('registerPageLayout', {
            content: [registerContentLayout, registerFooterLabel],
            width: '100%'
        });
        
        var scrollContainer = new sap.m.ScrollContainer('formScroll', {
       	 	content: [layout],
       	 	height:  "100%",
       	 	vertical: true
        });
 
        function handleUserTypeSelection(userType) {
        	if(userType === alsApp.config.USER_TYPE.PATIENT) {
        		that.diagnosisDateTimeInput.setEnabled(true);
        	} else if(userType === alsApp.config.USER_TYPE.CONTROL) {
        		that.diagnosisDateTimeInput.setEnabled(false);
        		that.diagnosisDateTimeInput.setValue();
        	}
        }

        return scrollContainer;
    },

    setEmail: function(email) {
        this.emailRegisterInput.setValue(email);
    }

});
