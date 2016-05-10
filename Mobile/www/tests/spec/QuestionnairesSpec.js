describe('Questionnaires Test', function() {
    it('Gets all questionnaires', function() {
    	runTestAuthenticated(function () {
	    	var result = getQuestionnaires();
	        var data = JSON.parse(result.data);
	        expect(result.status).toBe(Response.SUCCESS);
    	});
    });

    it('Creates a new full questionnaire', function() {
    	runTestAuthenticated(function () {
	    	var result = createQuestionnaire(TestData.fullQuestionnaire);
	        var data = JSON.parse(result.data);
	        expect(result.status).toBe(Response.SUCCESS);
	        expect(data.status).toBe("OK");
	        var fullQuestionnaireId = data.id;
	
	        result = getQuestionnaireById(fullQuestionnaireId);
	        data = JSON.parse(result.data);
	        stripQuestionnaireServerGeneratedData(data);
	        TestData.fullQuestionnaire.patientId = data.patientId;
	        expect(data).toEqual(TestData.fullQuestionnaire);
	        
	        var deleteResult = deleteQuestionnaire(fullQuestionnaireId)
	        expect(deleteResult.status).toBe(Response.SUCCESS);
    	});
    });

    it('Creates a new partial questionnaire', function() {
    	runTestAuthenticated(function () {
	    	var result = createQuestionnaire(TestData.partialQuestionnaire);
	        var data = JSON.parse(result.data);
	        expect(result.status).toBe(Response.SUCCESS);
	        expect(data.status).toBe("OK");
	        var partialQuestionnaireId = data.id;
	
	        var result = getQuestionnaireById(partialQuestionnaireId);
	        var data = JSON.parse(result.data);
	        stripQuestionnaireServerGeneratedData(data);
	        TestData.partialQuestionnaire.patientId = data.patientId;
	        expect(data).toEqual(TestData.partialQuestionnaire);
	        
	        var deleteResult = deleteQuestionnaire(partialQuestionnaireId);
	        expect(deleteResult.status).toBe(Response.SUCCESS);
    	});
    });

    it('Deletes the questionnaires', function() {
    	runTestAuthenticated(function () {
	    	var result = createQuestionnaire(TestData.fullQuestionnaire);
	        var data = JSON.parse(result.data);
	        expect(result.status).toBe(Response.SUCCESS);
	        expect(data.status).toEqual("OK");
	        var fullQuestionnaireId = data.id;
	    	
	        result = deleteQuestionnaire(fullQuestionnaireId);
	        data = JSON.parse(result.data);
	        expect(result.status).toBe(Response.SUCCESS);
	        expect(data.status).toEqual('OK');
	
	        result = getQuestionnaires();
	        expect(JSON.parse(result.data).length).toEqual(0);
    	});
    });

    it('Deletes the full questionnaire again (should not be found)', function() {
    	runTestAuthenticated(function () {
	    	var result = deleteQuestionnaire(1);
	        data = JSON.parse(result.data);
	        expect(result.status).toBe(Response.SUCCESS);
	        expect(data.status).toEqual('NOT_FOUND');
    	});
    });
});
