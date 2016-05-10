describe('Speech Tests Test', function() {
    it('Gets all Speech tests', function() {
    	runTestAuthenticated(function () {
	    	result = getSpeechTests();
	        data = JSON.parse(result.data);
	        expect(result.status).toBe(Response.SUCCESS);
    	});
    });

    it('Creates a new Speech test', function() {
    	runTestAuthenticated(function () {
	    	var result = createSpeechTest(TestData.speechTest);
	        var data = JSON.parse(result.data);
	        expect(result.status).toBe(Response.SUCCESS);
	        expect(data.status).toBe("OK");
	        var speechTestId = data.id;
	        
	        result = getSpeechTestById(speechTestId);
	        data = JSON.parse(result.data);
	        stripSpeechTestServerGeneratedData(data);
	        TestData.speechTest.patientId = data.patientId;
	        expect(data).toEqual(TestData.speechTest);
	        
    		result = deleteSpeechTest(speechTestId);
	        data = JSON.parse(result.data);
	        expect(result.status).toBe(Response.SUCCESS);
	        expect(data.status).toBe('OK');
    	});
    });

    it('Deletes the speech test', function() {
    	runTestAuthenticated(function () {
    		var result = getSpeechTests();
	        var data = JSON.parse(result.data);
    		var numOfTests  =  data.length;
    		result = createSpeechTest(TestData.speechTest);
	        data = JSON.parse(result.data);
	        expect(result.status).toBe(Response.SUCCESS);
	        expect(data.status).toBe("OK");
	        var speechTestId = data.id;
    		
    		result = deleteSpeechTest(speechTestId);
	        data = JSON.parse(result.data);
	        expect(result.status).toBe(Response.SUCCESS);
	        expect(data.status).toBe('OK');
	
	        result = getSpeechTests();
	        data = JSON.parse(result.data);
	        expect(data.length).toBe(numOfTests);
    	});
    });

    it('Deletes the speech test again (should not be found)', function() {
    	runTestAuthenticated(function () {
	    	var result = deleteSpeechTest(1);
	        var data = JSON.parse(result.data);
	        expect(result.status).toBe(Response.SUCCESS);
	        expect(data.status).toBe('NOT_FOUND');
    	});
    });
});
