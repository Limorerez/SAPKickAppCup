describe('Writing Tests Test', function() {
    it('Gets all writing tests', function() {
    	runTestAuthenticated(function () {
	    	result = getWritingTests();
	        data = JSON.parse(result.data);
	        expect(result.status).toBe(Response.SUCCESS);
    	});
    });

    it('Creates a new writing test', function() {
    	runTestAuthenticated(function () {
	    	var result = createWritingTest(TestData.writingTest);
	        var data = JSON.parse(result.data);
	        expect(result.status).toBe(Response.SUCCESS);
	        expect(data.status).toBe("OK");
	        var writingTestId = data.id;
	
	        result = getWritingTestById(writingTestId);
	        data = JSON.parse(result.data);
	        stripWritingTestServerGeneratedData(data);
	        TestData.writingTest.patientId = data.patientId;
	        expect(data).toEqual(TestData.writingTest);
	        
    		result = deleteWritingTest(writingTestId);
	        data = JSON.parse(result.data);
	        expect(result.status).toBe(Response.SUCCESS);
	        expect(data.status).toBe('OK');
    	});
    });

    it('Deletes the writing test', function() {
    	runTestAuthenticated(function () {
	    	var result = createWritingTest(TestData.writingTest);
	        var data = JSON.parse(result.data);
	        expect(result.status).toBe(Response.SUCCESS);
	        expect(data.status).toBe("OK");
	        var writingTestId = data.id;
    		
    		result = deleteWritingTest(writingTestId);
	        data = JSON.parse(result.data);
	        expect(result.status).toBe(Response.SUCCESS);
	        expect(data.status).toBe('OK');
	
	        result = getWritingTests();
	        data = JSON.parse(result.data);
	        expect(data.length).toBe(0);
    	});
    });

    it('Deletes the writing test again (should not be found)', function() {
    	runTestAuthenticated(function () {
	    	var result = deleteWritingTest(1);
	        var data = JSON.parse(result.data);
	        expect(result.status).toBe(Response.SUCCESS);
	        expect(data.status).toBe('NOT_FOUND');
    	});
    });
});
