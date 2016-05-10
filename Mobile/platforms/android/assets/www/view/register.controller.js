sap.ui.controller("view.register", {

    onInit: function() {
    },

    onRegister: function(oEvent) {
        var view = this.getView();
        var name = view.nameRegisterInput.getValue();
        var isPatient = view.patientRadioButton.getSelected();
        var isControl = view.controlRadioButton.getSelected();
        var yearOfBirth = parseInt(view.yearOfBirthInput.getValue());
        var thisYear = new Date().getFullYear();
        var gender = view.genderSelect.getSelectedKey();
        var diagnosis = view.diagnosisDateTimeInput.getDateValue();

        var password = view.passwordRegisterInput.getValue();
        var repPassword = view.repeatPasswordRegisterInput.getValue();
        
        var emailValidation = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z]{2,4})+$/;
        if (!emailValidation.test(alsApp.config.email)) {
            sap.m.MessageBox.alert('Please enter a valid email address', {
                    title: 'Details Missing'
                }
            );
        }

        else if ( password !== repPassword){
    		  sap.m.MessageBox.alert('Password do not match, please check again', {
    	          title: 'Passwords do not match'
    	      	}
    		  );
    	}
        
        else if (isNaN(yearOfBirth) || yearOfBirth < 1900 || yearOfBirth > thisYear) {
            sap.m.MessageBox.alert('Please enter a valid year of birth', {
                    title: 'Details Missing'
                }
            );
        }
        
        else if (isPatient && diagnosis === null) {
            sap.m.MessageBox.alert('Please enter a date of diagnosis', {
                title: 'Details Missing'
            }
        );
        }

        else if (gender !== "Gender") {

            var firstName, lastName;
            if (name === "" || name === null) {
                firstName = "";
                lastName = "";
            }
            else {
                var spaceIndex = name.lastIndexOf(' ');
                if (spaceIndex === -1) {
                    firstName = name;
                    lastName = "";
                }
                else {
                    firstName = name.substring(0, spaceIndex);
                    lastName = name.substring(spaceIndex + 1);
                }
            }

            var data = {
                "email": alsApp.config.email,
                "password" : view.passwordRegisterInput.getValue(),
                "repeatPassword" : view.repeatPasswordRegisterInput.getValue(),
                "firstName": firstName,
                "lastName": lastName,
                "birthday": Date.UTC(yearOfBirth, 0, 1),
                "gender": view.genderSelect.getSelectedKey() === "Male" ? 0 : 1,
                "diagnoseDate": diagnosis !== null ? Date.UTC(diagnosis.getFullYear(), diagnosis.getMonth(), 1) : null,
                "userType": view.patientRadioButton.getSelected() ? alsApp.config.USER_TYPE.PATIENT : alsApp.config.USER_TYPE.CONTROL
            };

            alsApp.busyDialog.open();

            $.ajax({
                type: 'POST',
                url: alsApp.config.SERVER_URL + '/patients',
                contentType: 'application/json',
                data: JSON.stringify(data),
                success: function(result) {
                    if (result === "USER_EXISTS") {
                        sap.m.MessageBox.alert('A user with this email address already exists', {
                            title: 'User exists'
                        });
                    }
                    else {
                        sap.m.MessageBox.alert('Thank you for registering! You can now login using your email.', {
                            title: 'Registration successful'
                        });
                        alsApp.back();
                    }
                },
                error: function() {
                    sap.m.MessageBox.alert('Could not complete registration. Try again later.', {
                        title: 'Registration failed'
                    });
                },
                complete: function() {
                    alsApp.busyDialog.close();
                }
            });
        }
        else {
            sap.m.MessageBox.alert('Please fill in all the details before submitting', {
                title: 'Details Missing'
            });
        }
    },

    onAlreadyRegistered: function() {
        alsApp.back();
    }

});
