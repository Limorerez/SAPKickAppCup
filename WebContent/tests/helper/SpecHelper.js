URLs = {
	SERVER_URL : '../api',
	getPatientsURL : function() {
		return URLs.SERVER_URL + '/patients'
	},
	getQuestionnairesURL : function() {
		return URLs.SERVER_URL + '/tasks/questionnaires'
	},
	getWritingTestsURL : function() {
		return URLs.SERVER_URL + '/tasks/writingTests'
	},
	getSpeechTestsURL : function() {
		return URLs.SERVER_URL + '/tasks/speechTests'
	},
	getLoginUrl : function() {
		return URLs.SERVER_URL + '/../j_spring_security_check'
	},
	getLogoutUrl : function() {
		return URLs.SERVER_URL + '/auth/logout'
	}
}

Response = {
	SUCCESS : 'success',
	ERROR : 'error',
	NOT_MODIFIED : 'notmodified',
	TIMEOUT : 'timeout',
	ABORT : 'abort',
	PARSE_ERROR : 'parsererror'
}

function doAjax(params) {
	var result = {
		data : null,
		status : null,
		jqXHR : null
	};
	$.ajax({
		url : params.url,
		type : params.type,
		contentType : params.contentType || 'application/json',
		data : params.data,
		async : false,
		success : function(data, status, jqXHR) {
			result.data = data;
			result.status = status;
			result.jqXHR = jqXHR;
		},
		error : function(jqXHR, status) {
			result.jqXHR = jqXHR;
			result.status = status;
		}
	});
	return result;
}

/** *** Patients tests **** */

function getPatients() {
	return doAjax({
		url : URLs.getPatientsURL(),
		type : 'GET'
	});
}

function getPatientByEmail(email) {
	return doAjax({
		url : URLs.getPatientsURL() + '?email=' + email,
		type : 'GET'
	});
}

function createPatient() {
	return doAjax({
		url : URLs.getPatientsURL(),
		type : 'POST',
		data : JSON.stringify(TestData.patient)
	});
}

function deletePatient(email) {
	return doAjax({
		url : URLs.getPatientsURL() + '?email=' + email,
		type : 'DELETE'
	});
}

function stripPatientServerGeneratedData(patient) {
	delete patient.id;
	delete patient.created;
	patient.diagnoseDate = new Date(patient.diagnoseDate).getTime();
	patient.birthday = new Date(patient.birthday).getTime();
}

/** *** Questionnaires tests **** */

function getQuestionnaires() {
	return doAjax({
		url : URLs.getQuestionnairesURL(),
		type : 'GET'
	});
}

function getQuestionnaireById(id) {
	return doAjax({
		url : URLs.getQuestionnairesURL() + '?id=' + id,
		type : 'GET'
	});
}

function createQuestionnaire(questionnaire) {
	return doAjax({
		url : URLs.getQuestionnairesURL(),
		type : 'POST',
		data : JSON.stringify(questionnaire)
	});
}

function deleteQuestionnaire(id) {
	return doAjax({
		url : URLs.getQuestionnairesURL() + '?id=' + id,
		type : 'DELETE'
	});
}

function stripQuestionnaireServerGeneratedData(questionnaire) {
	delete questionnaire.id;
	delete questionnaire.created;
	for ( var i in questionnaire.answers) {
		delete questionnaire.answers[i].id;
	}
}

/** *** Writing tests **** */

function getWritingTests() {
	return doAjax({
		url : URLs.getWritingTestsURL(),
		type : 'GET'
	});
}

function getWritingTestById(id) {
	return doAjax({
		url : URLs.getWritingTestsURL() + '?id=' + id,
		type : 'GET'
	});
}

function createWritingTest(questionnaire) {
	return doAjax({
		url : URLs.getWritingTestsURL(),
		type : 'POST',
		data : JSON.stringify(questionnaire)
	});
}

function deleteWritingTest(id) {
	return doAjax({
		url : URLs.getWritingTestsURL() + '?id=' + id,
		type : 'DELETE'
	});
}

function stripWritingTestServerGeneratedData(writingTest) {
	delete writingTest.id;
	delete writingTest.created;
	for ( var i in writingTest.drawings) {
		delete writingTest.drawings[i].id;
	}
}

/** *** Speech tests **** */


function getSpeechTests() {
	return doAjax({
		url : URLs.getSpeechTestsURL(),
		type : 'GET'
	});
}

function getSpeechTestById(id) {
	return doAjax({
		url : URLs.getSpeechTestsURL() + '?id=' + id,
		type : 'GET'
	});
}





function createSpeechTest(speechTest){
    
    var form = new FormData();
     form.append("email", speechTest.email );

     $.each(speechTest.recordings, function(key, value){
         form.append("testIds", value.testId);
         form.append("files", value.recording)
     });

     var result = {
                data: null,
                status: null,
                jqXHR: null
            };
     
     $.ajax({
         type: 'POST',
         url: URLs.getSpeechTestsURL(),
         data: form,
         cache: false,
         processData: false,
         contentType: false,
         async: false,
         success: function(data, status, jqXHR) {
             result.data = data;
             result.status = status;
             result.jqXHR = jqXHR;
         },
         error: function(jqXHR, status) {
             result.jqXHR = jqXHR;
             result.status = status;
         }
     });
     return result;
}


function deleteSpeechTest(id) {
	return doAjax({
		url : URLs.getSpeechTestsURL() + '?id=' + id,
		type : 'DELETE'
	});
}

function stripSpeechTestServerGeneratedData(speechTest) {
	delete speechTest.id;
	delete speechTest.created;
	for ( var i in speechTest.recordings) {
		var recording = speechTest.recordings[i];
		delete recording.id;
		recording['recording'] = RECORDING_BLOB;
		
	}
}


/** *** Login and authentication **** */

function registerAndAuthenticate() {
	var result = createPatient();
	expect(result.status).toBe(Response.SUCCESS);
	var authenticationResult = doAjax({
		url : URLs.getLoginUrl() + "?j_username="
				+ encodeURIComponent(TestData.patient.email) + "&j_password="
				+ TestData.patient.password,
		type : 'POST'
	});
	expect(authenticationResult.status).toBe(Response.SUCCESS);
}

function logout() {
	var logoutResult = doAjax({
		url : URLs.getLogoutUrl(),
		type : 'GET'
	});
	expect(logoutResult.status).toBe(Response.SUCCESS);
}

function runTestAuthenticated(testFunc) {
	registerAndAuthenticate();
	testFunc();
	var deleteResponse = deletePatient(TestData.patient.email);
	expect(deleteResponse.status).toBe(Response.SUCCESS);
	logout();
}
